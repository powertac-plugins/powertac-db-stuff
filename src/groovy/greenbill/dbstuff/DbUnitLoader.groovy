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

public class DbUnitLoader {
	
	def dataSource
	//ApplicationContext applicationContext
	
	def loadData(){
		//dataSource = ApplicationHolder.application.parentContext.getBean('dataSource')
		def dbOperation = ApplicationHolder.application.config.dataSource.dbLoad
		def dbLoadFiles = ApplicationHolder.application.config.dataSource.dbLoadFiles
		if(dbLoadFiles && dbOperation){
			load(dbLoadFiles,dbOperation)
		}
	}
	def load(path,operation) {
		//this.dataSource = dataSource
		//def context = WebApplicationContextUtils.getWebApplicationContext(ServletContextHolder.servletContext);
		def appCtx = ApplicationHolder.application.parentContext
		//dataSource = appCtx.getBean('dataSource')
		def db = DbUnitUtil.getConnection(dataSource)
		appCtx.getResources(path).each{
			if(DbUnitUtil.database != "mssql" && it.filename =="Dual.xml") {
			}else{
				println "loading data from ${it.filename}."
				def xmlds = new FlatXmlDataSet(it.file,false,true,false)
				try{
					DbUnitUtil.getDatabaseOperation(operation).execute(db, xmlds)
				}catch(e){
					println "!!!!! error loading data from ${it.filename}."
					e.printStackTrace() 
				}
			}
		}
	}


}
