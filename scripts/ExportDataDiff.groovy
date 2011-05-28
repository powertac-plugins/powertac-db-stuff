includeTargets << new File("${dbStuffPluginDir}/scripts/_ConfigDataSource.groovy")

target(main: "Export table diff data to files") {
	depends(parseArguments,packageApp,loadApp)

	def outPath = argsMap.params[1] ? argsMap.params[1] : "sql/data/diff/"
	def inPath = argsMap.params[0]  ? argsMap.params[0] : dsConfig.dataLoad.seedFiles
	def file = new File(outPath)
	file.mkdirs()
	def de = grailsApp.classLoader.loadClass("greenbill.dbstuff.DataExport").newInstance()
	de.dataSource=getDataSource()
	println "finding data rows in db that don't exist in the data files in $inPath"
	de.exportDiff(inPath,outPath)
	println "data sent to xml files in $outPath"
}

setDefaultTarget(main)
