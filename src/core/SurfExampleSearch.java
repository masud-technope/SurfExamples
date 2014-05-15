package core;

import java.util.ArrayList;
import extractor.GitHubCodeCollector;

public class SurfExampleSearch {

	ArrayList<CodeFile> CodeFiles;
	String searchQuery;
	
	public SurfExampleSearch(String searchQuery)
	{
		this.CodeFiles=new ArrayList<>();
		this.searchQuery=searchQuery;
		//GitHub collector
		GitHubCodeCollector collector=new GitHubCodeCollector(searchQuery);
		//collect GitHub Results
		this.CodeFiles=collector.collectGitHubResults();
	}
	
}
