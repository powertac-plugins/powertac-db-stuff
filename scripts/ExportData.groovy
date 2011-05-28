includeTargets << new File("${dbStuffPluginDir}/scripts/_ConfigDataSource.groovy")

target(main: "Export table diff data to files") {
	depends(parseArguments,packageApp,loadApp)

	def outpath = argsMap.params[1] ? argsMap.params[1] : "sql/data/out/"
	def tables = argsMap.params[0] ? argsMap.params[0] : "*"
	def file = new File(outpath)
	file.mkdirs()
	def de = grailsApp.classLoader.loadClass("greenbill.dbstuff.DataExport").newInstance()
	de.dataSource=getDataSource()
	println "exporting table data in db for tables $tables"
	de.export(tables,outpath)
	println "exported db data to $outpath"
}

setDefaultTarget(main)
