package extractor;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import core.CodeFile;
import core.StaticData;

public class GitHubCodeCollector {

	ArrayList<CodeFile> CodeFiles;
	String searchQuery;

	/*
	 * ArrayList<CodeFragment> apacheResults; ArrayList<CodeFragment>
	 * eclipseResults; ArrayList<CodeFragment> facebookResults;
	 * ArrayList<CodeFragment> twitterResults;
	 */

	String[] repos = { "apache","eclipse", "facebook", "twitter","google","yahoo"}; //pelick barchart "eclipse", "facebook"
	//String[] repos={"geotools", "GeoNode","boundlessgeo","netconstructor"};
	//String[] repos={"cbeams","DouglasAllen","gitbm","spring-projects"};
	//String[] repos={"mdenburger","ewelinamuciek","kondrak","dxd"};
	//String[] repos={"SiteView", "RMIaou","kgeri","android"};
	//String[] repos={"apache","bbrobe","romanbb","frostwire"};

	public GitHubCodeCollector(String searchQuery) {
		// default constructor
		this.CodeFiles = new ArrayList<>();
		this.searchQuery = searchQuery;
		// initiating the different repos
	}

	public ArrayList<CodeFile> collectGitHubResults() {
		// code for searching GitHub search results
		try {
			ArrayList<GitHubThread> rcollection = new ArrayList<>();
			ArrayList<Thread> tcollection = new ArrayList<>();

			for (String repo : this.repos) {
				GitHubThread rthread = new GitHubThread(repo, this.searchQuery);
				rcollection.add(rthread);
				Thread t = new Thread(rthread);
				tcollection.add(t);
				// starting the thread
				t.start();
			}
			int totalRepo = 6;
			int completed = 0;
			while (completed < totalRepo) {
				for (int i = 0; i < tcollection.size(); i++) {
					Thread t = tcollection.get(i);
					if (!t.isAlive()) {
						GitHubThread gthread = rcollection.get(i);
						ArrayList<CodeFile> tresults = gthread
								.getCollectedResults();
						this.CodeFiles.addAll(tresults);
						tcollection.remove(i);
						rcollection.remove(i);
						completed++;
					}
				}
			}
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
		System.out.println("Total results collected:"+this.CodeFiles.size());
		return this.CodeFiles;
	}

	public ArrayList<CodeFile> collectFromLocalRepo(int exceptionID)
	{
		// collecting code from local repository
		try {
			String codeFolder = StaticData.Surf_Data_Base + "/codes/"
					+ exceptionID;
			File fDir = new File(codeFolder);
			if (fDir.exists()) {
				int filecount = fDir.list().length;
				for (int i = 0; i < filecount; i++) {
					String codeFilePath = fDir + "/" + i + ".java";
					CodeFile codeFile = new CodeFile();
					try {
						Scanner scanner = new Scanner(new File(codeFilePath));
						String content = new String();
						while (scanner.hasNext()) {
							content += scanner.nextLine() + "\n";
						}
						scanner.close();
						// collecting the complete codes
						codeFile.CompleteCode = content;
						this.CodeFiles.add(codeFile);
					} catch (Exception exc) {
						// handle the exception
					}
				}
			}
		} catch (Exception exc) {
		}
		return this.CodeFiles;
	}
	
	
	protected void saveCodeContents(ArrayList<CodeFile> codeFiles) {
		// code for showing the code contents
		try {
			int count = 0;
			for (CodeFile codeFile : codeFiles) {
				String fileURL = StaticData.Surf_Data_Base + "/codes/" + count
						+ ".java";
				File f = new File(fileURL);
				FileWriter fwriter = new FileWriter(f);
				fwriter.write(codeFile.CompleteCode);
				fwriter.close();
			}
		} catch (Exception exc) {
			// handle the exception
		}
	}
	protected void saveCodeContents(int exceptionID, ArrayList<CodeFile> codeFiles)
	{
		//code for saving the code contents
		try{
			int count = 0;
			String dir = StaticData.Surf_Data_Base + "/codes/" + exceptionID;
			File fdir = new File(dir);
			if (!fdir.exists())
				fdir.mkdir();
			
			for (CodeFile codeFile : codeFiles) {
				String fileURL = StaticData.Surf_Data_Base + "/codes/"+exceptionID+"/"+ count
						+ ".java";
				File f = new File(fileURL);
				FileWriter fwriter = new FileWriter(f);
				fwriter.write(codeFile.CompleteCode);
				fwriter.close();
				count++;
				System.out.println(codeFile.htmlFileURL+" "+codeFile.GitHubScore);
			}
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		// main method
		int exceptionID=200;
		String searchQuery = "ArrayList FileInputStream";
		GitHubCodeCollector gitCodeCollector = new GitHubCodeCollector(
				searchQuery);
		gitCodeCollector.collectGitHubResults();
		//gitCodeCollector.saveCodeContents(exceptionID, gitCodeCollector.CodeFiles);
	}
}
