package casestudy;

import java.io.File;
import java.nio.file.Files;

import core.StaticData;

public class SourceCodeCollector {
	String projectFolder;
	String projectName;
	String currentDestFolder;
	
	public SourceCodeCollector(String projectFolder, String projectName)
	{
		//initialization
		this.projectFolder=projectFolder;
		this.projectName=projectName;
		String folderpath=StaticData.Surf_Data_Base+"/casestudy/sources/project3";
		//current destination folder
		this.currentDestFolder=folderpath;
		this.createProjectFolder(this.currentDestFolder);
	}
	
	protected void createProjectFolder(String folderpath) {
		File f = new File(folderpath);
		if (!f.exists())
			f.mkdir();
	}
	
	protected void extractSourceFiles()
	{
		//extracting directory files
		extractDirectoryFiles(new File(this.projectFolder));
	}
	
	protected void extractDirectoryFiles(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (File f : files) {
				if (!f.isDirectory()) {
					if (f.getName().endsWith(".java")) {
						try {
							Files.copy(f.toPath(),new File(this.currentDestFolder + "/"+ f.getName()).toPath());
						} catch (Exception exc) {
							// handle it
						}
					}
				} else {
					extractDirectoryFiles(f);
				}
			}
		} else {
			if (directory.getName().endsWith(".java")) {
				try {
					Files.copy(directory.toPath(),new File(this.currentDestFolder + "/"+ directory.getName()).toPath());
				} catch (Exception exc) {
					// handle it
				}
			}
		}
	}
	
	public static void main(String[] args){
		String projectFolder=StaticData.Surf_Data_Base+"/casestudy/projects/jclf-3.1.0-src";
		SourceCodeCollector scollector=new SourceCodeCollector(projectFolder, "JCLF");
		scollector.extractSourceFiles();
	}	
}
