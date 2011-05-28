import groovy.sql.Sql
import java.sql.SQLException
import org.springframework.jdbc.datasource.DriverManagerDataSource

includeTargets << grailsScript("Bootstrap")

def dsFile = new File("${basedir}/grails-app/conf/DataSource.groovy")
dsConfig = new ConfigSlurper(grailsEnv).parse(dsFile.text)

getDataSource = {
    def username = dsConfig?.dataSource?.username ?: 'sa'
    def password = dsConfig?.dataSource?.password ?: ''
    def url = dsConfig?.dataSource?.url ?: 'jdbc:hsqldb:mem:testDB'
    def driverClassName = dsConfig?.dataSource?.driverClassName ?: 'org.hsqldb.jdbcDriver'
	def ds = new DriverManagerDataSource(driverClassName,url,username,password)
	return ds
}

getCreateDataSource={

    def username = dsConfig?.dataSource?.username ?: 'sa'
    def password = dsConfig?.dataSource?.password ?: ''
    def url = dsConfig?.dataLoad?.createUrl ?: 'jdbc:hsqldb:mem:testDB'
    def driverClassName = dsConfig?.dataSource?.driverClassName ?: 'org.hsqldb.jdbcDriver'
	def ds = new DriverManagerDataSource(driverClassName,url,username,password)
	return ds
}