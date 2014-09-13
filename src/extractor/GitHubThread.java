package extractor;

import java.net.URLEncoder;
import java.util.ArrayList;
import client.GitHubClient;
import utility.CodeDownloader;
import utility.GitHubResponseDecoder;
import core.CodeFile;

public class GitHubThread implements Runnable {

	ArrayList<CodeFile> codeFiles;
	String targetRepo;
	String searchQuery;
	final int MAXIMUM_EXAMPLES=50; //maximum results to be taken from a source repo

	public GitHubThread(String targetRepo, String searchQuery) {
		//initialization
		this.codeFiles = new ArrayList<>();
		this.targetRepo=targetRepo;
		this.searchQuery=searchQuery;
	}

	private void collectResultsfromRepo()
	{
		//code for collecting results through API call
		try{
			String callURL = developSearchURL();
			String apiResponse = GitHubClient.exceuteGitHubCall(callURL);
			this.codeFiles =GitHubResponseDecoder.extractResultsFromJSON(apiResponse);
			//this.codeFiles=getPageContent();
			//this.codeFiles=refineCodeResults();
		}catch(Exception exc){
			//handle the exception
		}
	}

	private ArrayList<CodeFile> refineCodeResults() {
		// code for refining the results
		int limit = this.codeFiles.size() < MAXIMUM_EXAMPLES ? this.codeFiles
				.size() : MAXIMUM_EXAMPLES;
		if (this.codeFiles.size() > MAXIMUM_EXAMPLES) {
			for (int i = limit; i < this.codeFiles.size(); i++) {
				this.codeFiles.remove(i);
			}
		}
		return this.codeFiles;
	}
	
	private ArrayList<CodeFile> getPageContent() {
		// code for downloading the content
		int limit = this.codeFiles.size() < MAXIMUM_EXAMPLES ? this.codeFiles
				.size() : MAXIMUM_EXAMPLES;
		if (this.codeFiles.size() > MAXIMUM_EXAMPLES) {
			for (int i = limit; i < this.codeFiles.size(); i++) {
				this.codeFiles.remove(i);
			}
		}
		// now download the contents
		CodeDownloader downloader = new CodeDownloader(this.codeFiles);
		this.codeFiles = downloader.downloadCodeContents();
		return this.codeFiles;
	}
	
	
	public ArrayList<CodeFile> getCollectedResults()
	{
		//returning the collected fragments
		return this.codeFiles;
	}
	
	protected String developSearchURL() {
		// code for developing the base query
		String callURL = new String();
		try {
			String encodedItem = URLEncoder.encode(this.searchQuery, "UTF-8");
			callURL = "https://api.github.com/search/code?q="+encodedItem
					+ "+in:file+user:"+this.targetRepo+"+extension:java";
		} catch (Exception exc) {
			// handle the exception
		}
		return callURL;
	}

	public void run() {
		// code for downloading the code files
		try {
			collectResultsfromRepo();
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
	}
}
