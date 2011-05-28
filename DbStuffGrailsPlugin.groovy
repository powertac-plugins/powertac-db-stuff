import greenbill.dataloader.*

class DbStuffGrailsPlugin {
    // the plugin version
    def version = "0.3.0"
    def grailsVersion = "1.1 > *"
	 def dependsOn = [:] 

    // resources that are excluded from plugin packaging
    def pluginExcludes = ['grails-app/views/error.gsp',
		'grails-app/conf/DataSource.groovy','db/*',
		'lib/jtds-1.2.2.jar','grails-app/domain/Test*',
		'lib/mysql-connector-java-5.1.6-bin.jar']

	  def author = "Joshua Burnett"
	  def authorEmail = "joshua at 9ci com"
	  def title = "db schema managment and data import/export. Generate generic schema files and import or export base/seed/test data into your database."
	  def description = '''\
		Based on DdlUtils from the Apache DB Project http://db.apache.org. 
		Keeps the schema in a generic xml file that can then be used to creates the db schema in any of the 
		supported databases. Data can also be extrernalized in files and then loaded via a grails script or at application startup.
		Load base/seed data into you database. Store/export the data in xml and this will load the database with your applications base records
		This can and is used for integration testing.
		This is also used for populating a databse with its neccesary records to do an initial install. 
		We use it to load tables like configuration params, ACL, acegi requestmaps etc..
		Works with Hsqldb,MsSql,MySql,Oracle,db2 and others that DdlUtils supports
	'''
	
    // URL to the plugin's documentation, I'm still trying to figure out how to create thisxx
    def documentation = "http://grails.org/DbStuff+Plugin"

    def doWithSpring = {
        /*if(dataLoad.loadType && dataLoad.loadType!="none" && !dataLoad.dbLoading){
        			if(dataLoad.loadType?.contains("create")){
        				def dbcreate = new DbCreate()
        				dbcreate.dataSource = applicationContext.getBean('dataSource')
        				if(dataLoad.loadType == "drop-create") 
        					dbcreate.dropAndCreate()
        				
        				dl.load(seedFiles,loadType)
        			}
        		}*/
    }

    def doWithApplicationContext = { applicationContext ->
/*		def dl = new DataLoader()
		dl.dataSource = applicationContext.getBean('dataSource')
		def dataLoad = application.config.dataLoad
		def dload = application.config.dataLoad.loadType
		//dataLoad.dbLoading this is set to true if run via sript so it doen ot get run twice
		if(dataLoad.loadType && dataLoad.loadType!="none" && !dataLoad.dbLoading){
			if(dataLoad.loadType?.contains("create")){
				def dbcreate = new DbCreate()
				dbcreate.dataSource = applicationContext.getBean('dataSource')
				if(dataLoad.loadType == "drop-create") 
					dbcreate.dropAndCreate()
				
				dl.load(seedFiles,loadType)
			}
			if(dataLoad.seedFiles){
				dl.load(seedFiles,loadType)
			}
			if(dataLoad.dataFiles){
				dl.load(dataFiles,loadType)
			}
		}*/
    }

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
