package solfixer;

import java.io.File;
import java.util.Scanner;
import querymanager.MyQueryMaker;
import core.StaticData;

public class GitHubQuery {
	
	String exceptFile;
	public GitHubQuery()
	{
		exceptFile=StaticData.Surf_Data_Base+"/exceplist.txt";
	}
	
	public static void main(String[] args){
		GitHubQuery gitQuery=new GitHubQuery();
		try{
		File f=new File(gitQuery.exceptFile);
		Scanner scanner=new Scanner(f);
		while(scanner.hasNext()){
			String line=scanner.nextLine();
			String[] parts=line.split("\\s+");
			int exceptionID=Integer.parseInt(parts[0].trim().split("\\.")[0]);
			String exceptionName=parts[1].trim();
			MyQueryMaker maker=new MyQueryMaker(exceptionID, exceptionName);
			System.out.println(exceptionID+" "+maker.getGitHubSearchQuery());
			Thread.sleep(2000);
		}
		scanner.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
