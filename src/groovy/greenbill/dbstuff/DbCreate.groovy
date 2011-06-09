/*
 *   Copyright 2008 Joshua Burnett, 9ci Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package greenbill.dbstuff;


import org.apache.commons.logging.LogFactory
import org.apache.ddlutils.PlatformFactory
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration
import org.hibernate.dialect.DialectFactory
import org.hibernate.tool.hbm2ddl.SchemaExport
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.jdbc.support.JdbcUtils

public class DbCreate
{
  private static final log = LogFactory.getLog(this)


  def dataSource
  def platform

  def dropAndCreate (dbname, dsConfig) {
    platform = PlatformFactory.createNewPlatformInstance(dataSource)
    def platformName = platform.name.toLowerCase();
    if (platformName.contains("mssql")) {
      dropMsSql(dbname,
          dsConfig.dataSource.driverClassName, dsConfig.dataLoad.createUrl,
          dsConfig.dataSource.username, dsConfig.dataSource.password)
      createMsSql(dbname, dsConfig.dataLoad.createDbPath,
          dsConfig.dataSource.driverClassName, dsConfig.dataLoad.createUrl,
          dsConfig.dataSource.username, dsConfig.dataSource.password)
    } else if (platformName.contains("mysql")) {
      dropMySql(dbname)
      createMySql(dbname)
    } else if (platformName.contains("hsqldb")) {
      dropHsql(dbname)
    } else throw new IllegalArgumentException("Drop and Create not supported for this databse yet")
  }

  def create (dbname, dsConfig) {
    platform = PlatformFactory.createNewPlatformInstance(dataSource)
    def platformName = platform.name.toLowerCase();
    if (platformName.contains("mssql")) {
      createMsSql(dbname, dsConfig.dataLoad.createDbPath,
          dsConfig.dataSource.driverClassName, dsConfig.dataLoad.createUrl,
          dsConfig.dataSource.username, dsConfig.dataSource.password)
    } else if (platformName.contains("mysql")) {
      createMySql(dbname)
    } else if (platformName.contains("HsqlDb")) {
    } else throw new IllegalArgumentException("Create not supported for this databse yet")
  }

  def dropMsSql (dbname, driverClassName, url, username, password) {
    def sql = """
				if db_id(\'${dbname}\') is not null
				BEGIN
				Alter database ${dbname} set single_user with rollback immediate; 
				use tempdb;
				drop database ${dbname};
				END
		"""
    def ant = new AntBuilder()
    ant.sql(print: true, autocommit: true, keepformat: true, delimitertype: "row",
        driver: "${driverClassName}",
        url: "${url}", userid: "${username}", password: "${password }")
        {
          transaction(sql)
        }
  }

  def dropMySql (dbname) {
    runSql("""
				use mysql;
				drop database if exists ${dbname};
		""")
  }

  def dropHsql (dbname) {
    runSql("DROP SCHEMA PUBLIC CASCADE")
  }

  def createMsSql (dbname, path, driverClassName, url, username, password) {
    def sql = """
			if db_id(\'${dbname}\') is null
			BEGIN
			use tempdb;
			create database ${dbname} ON PRIMARY(
				NAME=\"${dbname}\",
				FILENAME=\"${path}\\${dbname}.mdf\"
			);
			alter database ${dbname} set recovery simple, auto_shrink on;
			END
			"""
    def ant = new AntBuilder()
    ant.sql(print: true, keepformat: true, delimitertype: "row",
        driver: "${driverClassName}",
        url: "${url}", userid: "${username}", password: "${password }")
        {
          transaction(sql)
        }

  }

  def createMySql (dbname) {
    runSql("create database IF NOT EXISTS ${dbname};")

  }

  def runSql (sql) {
    //try {
    println sql
    platform.evaluateBatch(sql, false)
    //def db = new Sql(dataSource)
    //db.execute(sql)
    //} catch(Exception e){
    //	e.printStackTrace()
    //}
  }

  def create (grailsApplication) {
    def props = populateProperties()

    GrailsAnnotationConfiguration config =
    new GrailsAnnotationConfiguration();
    config.setGrailsApplication(grailsApplication)
    config.setProperties(props)

    def classLoader = grailsApplication.classLoader
    def eventsClassLoader = new GroovyClassLoader(classLoader)
    def hibernateCfgXml = eventsClassLoader.getResource('hibernate.cfg.xml')
    if (hibernateCfgXml) {
      config.configure(hibernateCfgXml)
    }

    new SchemaExport(config).create(false, true)
  }

  def populateProperties = {
    def props = new Properties()

    def dsConfig = ConfigurationHolder.config

    props.'hibernate.connection.username' = dsConfig?.dataSource?.username ?: 'sa'
    props.'hibernate.connection.password' = dsConfig?.dataSource?.password ?: ''
    props.'hibernate.connection.url' = dsConfig?.dataSource?.url ?: 'jdbc:hsqldb:mem:testDB'
    props.'hibernate.connection.driver_class' =
      dsConfig?.dataSource?.driverClassName ?: 'org.hsqldb.jdbcDriver'

    if (dsConfig?.dataSource?.configClass) {
      if (dsConfig.dataSource.configClass instanceof Class) {
        configClassName = dsConfig.dataSource.configClass.name
      }
      else {
        configClassName = dsConfig.dataSource.configClass
      }
    }

    def namingStrategy = dsConfig?.hibernate?.naming_strategy
    if (namingStrategy) {
      try {
        GrailsDomainBinder.configureNamingStrategy namingStrategy
      }
      catch (Throwable t) {
        log.warn """WARNING: You've configured a custom Hibernate naming strategy '$namingStrategy' in DataSource.groovy, however the class cannot be found.
              Using Grails' default naming strategy: '${GrailsDomainBinder.namingStrategy.getClass().name}'"""
      }
    }

    if (dsConfig?.dataSource?.dialect) {
      def dialect = dsConfig.dataSource.dialect
      if (dialect instanceof Class) {
        dialect = dialect.name
      }
      props.'hibernate.dialect' = dialect
    }
    else {
      log.info 'NOTICE: Autodetecting the Hibernate Dialect; consider specifying the class name in DataSource.groovy'
      try {
        def ds = new DriverManagerDataSource(
            props.'hibernate.connection.driver_class',
            props.'hibernate.connection.url',
            props.'hibernate.connection.username',
            props.'hibernate.connection.password')
        def dbName = JdbcUtils.extractDatabaseMetaData(ds, 'getDatabaseProductName')
        def majorVersion = JdbcUtils.extractDatabaseMetaData(ds, 'getDatabaseMajorVersion')
        props.'hibernate.dialect' =
          DialectFactory.determineDialect(dbName, majorVersion).class.name
      }
      catch (Exception e) {
        log.error "ERROR: Problem autodetecting the Hibernate Dialect: ${e.message}"
        throw e
      }
    }

    return props
  }
}
