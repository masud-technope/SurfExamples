package misc;

import indexing.CFIndexManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import weka.clusterers.FarthestFirst;
import core.CodeFragment;
import core.StaticData;

public class ScoreLoader {

	
	ArrayList<CodeFragment> Fragments;
	int exceptionID;
	public ScoreLoader(int exceptionID)
	{
		this.exceptionID=exceptionID;
		this.Fragments=new ArrayList<>();
	}
	
	
	public  ArrayList<CodeFragment> loadDifferentScores()
	{
		//code for loading the code fragments
		try{
			int totalFragment=CFIndexManager.getFileCount(exceptionID);
			for(int i=0;i<totalFragment;i++){
				CodeFragment cfragment=new CodeFragment();
				cfragment.FragmentID=i;
				Fragments.add(cfragment);
			}
			loadContentScores(exceptionID);
			loadStructuralScores(exceptionID);
			loadMiscScores(exceptionID);
		}catch(Exception exc){
			//handle the exception
		}
		return Fragments;
	}
	protected void loadContentScores(int exceptionID)
	{
		//code for loading the content scores
		try{
			String contentFile=StaticData.Surf_Data_Base+"/weights/content/"+exceptionID+".txt";
			File cfile=new File(contentFile);
			Scanner scanner=new Scanner(cfile);
			for(CodeFragment cf:Fragments){
				if(scanner.hasNext()){
					String line=scanner.nextLine();
					String[] parts=line.split("\\s+");
					int ID=Integer.parseInt(parts[0].trim());
					if(cf.FragmentID!=ID){
						System.err.println("Inconsistent:"+cf.FragmentID+" "+ID);
					}
					double clonescore=Double.parseDouble(parts[1].trim());
					double cosinescore=Double.parseDouble(parts[2].trim());
					cf.CodeCloneScore=clonescore;
					cf.CosineSimilarityScore=cosinescore;
				}				
			}
			scanner.close();
		}catch(Exception exc){
			
		}
		
	}
	
	protected void loadStructuralScores(int exceptionID)
	{
		//code for loading the content scores
		try{
			String contentFile=StaticData.Surf_Data_Base+"/weights/structure/"+exceptionID+".txt";
			File cfile=new File(contentFile);
			Scanner scanner=new Scanner(cfile);
			for(CodeFragment cf:Fragments){
				if(scanner.hasNext()){
					String line=scanner.nextLine();
					String[] parts=line.split("\\s+");
					int ID=Integer.parseInt(parts[0].trim());
					if(cf.FragmentID!=ID){
						System.err.println("Inconsistent:"+cf.FragmentID+" "+ID);
					}
					double fieldmatch=Double.parseDouble(parts[1].trim());
					double methodmatch=Double.parseDouble(parts[2].trim());
					double objectmatch=Double.parseDouble(parts[3].trim());
					double dependencymatch=Double.parseDouble(parts[4].trim());
					
					cf.FieldMatchScore=fieldmatch;
					cf.MethodMatchScore=methodmatch;
					cf.CodeObjectMatchScore=objectmatch;
					cf.DependencyMatchScore=dependencymatch;
				}				
			}
		}catch(Exception exc){
			
		}
		
	}
	protected void loadMiscScores(int exceptionID)
	{
		//code for loading the content scores
		try{
			String contentFile=StaticData.Surf_Data_Base+"/weights/quality/"+exceptionID+".txt";
			File cfile=new File(contentFile);
			Scanner scanner=new Scanner(cfile);
			for(CodeFragment cf:Fragments){
				if(scanner.hasNext()){
					String line=scanner.nextLine();
					String[] parts=line.split("\\s+");
					int ID=Integer.parseInt(parts[0].trim());
					if(cf.FragmentID!=ID){
						System.err.println("Inconsistent:"+cf.FragmentID+" "+ID);
					}
					double readability=Double.parseDouble(parts[1].trim());
					double stmtcount=Double.parseDouble(parts[2].trim());
					double h2cratio=Double.parseDouble(parts[3].trim());
					cf.ReadabilityScore=readability;
					cf.StatementCountScore=stmtcount;
					cf.HandlerToCodeRatio=h2cratio;
				}				
			}
		}catch(Exception exc){
			
		}
		
	}
	
	
	
	
	
	
}
