import groovy.sql.Sql
import java.sql.SQLException
//import greenbill.dataloader.DataLoader
import org.springframework.jdbc.datasource.DriverManagerDataSource

includeTargets << grailsScript("Bootstrap")

def props = new Properties()
def filename = "${basedir}/ddl.sql"
boolean export = false
boolean stdout = false
//def dsConfig = getConfig()

target(loadData: "Load the data from the specified directory into the database") {
	depends(parseArguments,packageApp,loadApp)
	
	grailsApp.config.dataSource.dbLoading=true;
   //configureApp()
	def resPath = argsMap.params[1] ? argsMap.params[1] : 
		grailsApp.config.dataLoad.dataFiles
	def opType = argsMap.params[0]  ? argsMap.params[0] : 
		grailsApp.config.dataLoad.loadType
	
	def dl = grailsApp.classLoader.loadClass("greenbill.dbstuff.DataLoader").newInstance()
	//dl.dataSource=appCtx.getBean('dataSource')
	dl.dataSource=getCreateDataSource() 
	dl.load(resPath,opType)

}

setDefaultTarget(loadData)

