includeTargets << new File("${dbStuffPluginDir}/scripts/_ConfigDataSource.groovy")

target(createDb: "Load the data from the specified directory into the database") {
	depends(parseArguments,packageApp,loadApp)
	
	def createdrop = argsMap.params ? argsMap.params[0] : "create"
	
	def dbc = grailsApp.classLoader.loadClass("greenbill.dbstuff.DbCreate").newInstance()
	dbc.dataSource=getCreateDataSource() 
	if(createdrop == "clean") {
		if(isInteractive) {
		 def res = confirmInput("this will drop the database ${dsConfig.dataLoad.createDbName} on ${dsConfig.dataLoad.createUrl},\n you sure?")
		 if(!res) exit(0)
		}
		println "about to drop and recreate the database for ${dsConfig.dataLoad.createDbName}"
		dbc.dropAndCreate(dsConfig.dataLoad.createDbName,dsConfig)
	}else{
		println "attempting to create the database for ${dsConfig.dataLoad.createDbName}"
		dbc.create(dsConfig.dataLoad.createDbName,dsConfig)
	}
	//load the schema
	def dl = grailsApp.classLoader.loadClass("greenbill.dbstuff.DataLoader").newInstance()
	dl.dataSource=getDataSource()
	println "creating tables from schema files ${dsConfig.dataLoad.schemaFiles}" 
	dl.loadSchema(dsConfig.dataLoad.schemaFiles,true)
	
	println "creating the base seed data from files ${dsConfig.dataLoad.seedFiles}" 
	dl.load(dsConfig.dataLoad.seedFiles,null)
	
	println "loading the data files from files ${dsConfig.dataLoad.dataFiles}" 
	dl.load(dsConfig.dataLoad.dataFiles,null)
	
	println "loading another set of data files from files ${dsConfig.dataLoad.dataFilesExtra}"
	if(dsConfig.dataLoad.dataFilesExtra){ 
		dl.load(dsConfig.dataLoad.dataFilesExtra,null)
	}
	
	println "running the scripts in ${dsConfig.dataLoad.sqlFiles}" 
	grailsApp.parentContext.getResources(dsConfig.dataLoad.sqlFiles).each{
		
		ant.sql(src:"${it.file.absolutePath}",showheaders:"false",showtrailers:"false",
			print:"false", autocommit:true, keepformat:true, delimitertype:"row",
			driver:"${dsConfig.dataSource.driverClassName}",
			url:"${dsConfig.dataSource.url}", userid:"${dsConfig.dataSource.username}", password:"${dsConfig?.dataSource?.password }"){}
		
	}

}

setDefaultTarget(createDb)
