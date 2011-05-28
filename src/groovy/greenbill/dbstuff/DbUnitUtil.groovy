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
import groovy.sql.Sql
import java.sql.SQLException

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.commons.ApplicationHolder

import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.database.DatabaseDataSourceConnection

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext

public class DbUnitUtil {
	static database =""
	static def getConnection(dataSource){
		def driver  = ApplicationHolder.application.config.dataSource.driverClassName.toLowerCase()
		if (driver.contains("jtds") || driver.contains("sqlserver")){
			database="mssql"
			return new org.dbunit.ext.mssql.MsSqlConnection(dataSource.connection,null)
		}else if (driver.contains("mysql")){
			database="mysql"
			return new org.dbunit.ext.mysql.MySqlConnection(dataSource.connection,null)
		}else if (driver.contains ("oracle")){
			return new org.dbunit.ext.oracle.OracleConnection(dataSource.connection,null)
		}else if (driver.contains ("hsqldb")){
			database="hsqldb"
			return new org.dbunit.ext.hsqldb.HsqldbConnection(dataSource.connection,null)
		}else if (driver.contains ("ibm"))
			return new org.dbunit.ext.db2.Db2Connection(dataSource.connection,null)
		else 
			return new DatabaseDataSourceConnection(dataSource)
	}

	static def getDatabaseOperation(operation) {
		operation = operation.toUpperCase()
		if ("UPDATE".equals(operation))
			return DatabaseOperation.UPDATE;
		else if ("INSERT".equals(operation))
			return DatabaseOperation.INSERT;
		//else if ("INSERT_NEW".equals(operation))
			//	return new InsertNewOperation();
		else if ("REFRESH".equals(operation))
			return DatabaseOperation.REFRESH;
		else if ("DELETE".equals(operation))
			return DatabaseOperation.DELETE;
		else if ("DELETE_ALL".equals(operation))
			return DatabaseOperation.DELETE_ALL;
		else if ("CLEAN_INSERT".equals(operation))
			return DatabaseOperation.CLEAN_INSERT;
		else if ("NONE".equals(operation))
			return DatabaseOperation.NONE;
		else
			throw new IllegalArgumentException(
					"Type must be one of: UPDATE, INSERT, "
							+ "REFRESH, DELETE, DELETE_ALL, CLEAN_INSERT, MSSQL_INSERT, "
							+ "or MSSQL_REFRESH but was: "
							+ operation);
	}

}
