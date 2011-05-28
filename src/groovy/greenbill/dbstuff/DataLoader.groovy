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

import org.springframework.context.ApplicationContextAware
import org.springframework.context.ApplicationContext
	
import org.apache.ddlutils.io.DatabaseDataIO;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;

import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.DdlUtilsException;

public class DataLoader {
	/** The database data io object. */
    def dataio = new DatabaseDataIO();
	def dataSource
	//ApplicationContext applicationContext

	def load(path,operation) {
		//this.dataSource = dataSource
		//def context = WebApplicationContextUtils.getWebApplicationContext(ServletContextHolder.servletContext);
		def appCtx = ApplicationHolder.application.parentContext
		//dataSource = appCtx.getBean('dataSource')
		def platform = PlatformFactory.createNewPlatformInstance(dataSource)
		def sarray = appCtx.getResources(path).collect{it.inputStream} as InputStream[]
		try{
			dataio.dataLoadType="INSERT_NEW"
			dataio.writeDataToDatabase(platform,sarray)	//methods insert_new,insert_update
		}catch(e){
			println "!!!!! error loading data from ${path}."
			e.printStackTrace() 
		}finally{
			sarray.each(){it.close() }
		}
/*		appCtx.getResources(path).each{
			println "---loading data from ${it.filename}."
			try{
				dataio.writeDataToDatabase(platform,it.inputStream)
			}catch(e){
				println "!!!!! error loading data from ${it.filename}."
				e.printStackTrace() 
			}
		}*/
	}
	
	def loadSchema(path,alterDb) {
		def appCtx = ApplicationHolder.application.parentContext

		def platform = PlatformFactory.createNewPlatformInstance(dataSource)
		DatabaseIO dreader = new DatabaseIO()

		Database   model  = null

		appCtx.getResources(path).each{
			Database curModel = readSchemaFile(dreader, it.file);

			if(!model){
				model = curModel
			}else if (curModel){
				try{
					model.mergeWith(curModel);
				}
				catch (ex) {
					throw new DdlUtilsException("Could not merge with schema from file ${it.file}: "+ex.getLocalizedMessage(), ex);
				}
			}
		}//end file loop
		if(model){
			if (alterDb){
				platform.alterTables(model, false)
			}else{
				platform.createTables(model, true, false)
			}
		}else{
			throw new DdlUtilsException("No schemas found for $path");
			//println "No schemas found for $path"
		}	
	}
	
	def loadSql(path) {
		
	}


	Database readSchemaFile(DatabaseIO dreader, File schemaFile){
		println "reading schema from file ${schemaFile.name}"
		Database model = null;
		try{
			dreader.setValidateXml(false)
			model = dreader.read(schemaFile);
			
		}catch (Exception ex){
			throw new DdlUtilsException("Could not read schema file "+schemaFile.getAbsolutePath()+": "+ex.getLocalizedMessage(), ex);
		}
		return model;
	}

}
