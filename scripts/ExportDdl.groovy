import groovy.sql.Sql
import java.sql.SQLException
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.model.Database;
import org.apache.ddlutils.model.CloneHelper;

//includeTargets << grailsScript("Init")
//includeTargets << grailsScript("_GrailsCompile")
includeTargets << grailsScript("Bootstrap")

target(main: "Export the table schemas for xml file") {
	depends(parseArguments,bootstrap)
	def outpath = argsMap.params[1] ? argsMap.params[1] : "sql/schema/out/schema.xml"
	def tables = argsMap.params[0] ? argsMap.params[0] : "*"
	def platform = PlatformFactory.createNewPlatformInstance(appCtx.getBean('dataSource'))
	Database model = platform.readModelFromDatabase(null);
	
	def file = new File(outpath)
	file.parentFile.mkdirs()
	file.createNewFile()
	FileWriter outputWriter = new FileWriter(outpath);
    DatabaseIO dbIO         = new DatabaseIO();
	
	dbIO.write(model, outputWriter);
    outputWriter.close();
	println("file has been exported to $outpath")

	//def tables = argsMap.params[0]  ? argsMap.params[0] : "*" //* is all, export all table data if not specified

}

target(split: "Splits a big xml schema file into one table per file arrangment") {
	depends(parseArguments,bootstrap)

	DatabaseIO dreader = new DatabaseIO()
	dreader.setValidateXml(false)
	Database   model = dreader.read("sql/schema/out/schema.xml");

	def outpath = argsMap.params[1] ? argsMap.params[1] : "sql/schema/out"
	CloneHelper cloneHelper = new CloneHelper()
	model.tables.each{table->
		def tableModel=cloneHelper.clone(model)
		tableModel.removeAllTablesExcept(table);
		def file = new File("${outpath}/${table.name}.xml")
		println "exporting ${file.path}"
		//file.parentFile.mkdirs()
		file.createNewFile()
		FileWriter outputWriter = new FileWriter(file);
    	DatabaseIO dbIO         = new DatabaseIO();
		dbIO.write(tableModel, outputWriter);
    	outputWriter.close();
	}

	//def tables = argsMap.params[0]  ? argsMap.params[0] : "*" //* is all, export all table data if not specified

}

setDefaultTarget(main)
