package misc;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import utility.SolutionLoader;
import core.ScoreWeights;
import core.StaticData;

public class MLWeightMaker {
	
	
	int exceptionID;
	HashMap<Integer, ArrayList<Double>> content;
	HashMap<Integer, ArrayList<Double>> structure;
	HashMap<Integer, ArrayList<Double>> quality;
	int maxLimit=10;
	public MLWeightMaker(int exceptionID)
	{
		//initialization
		this.exceptionID=exceptionID;
		this.content=new HashMap<>();
		this.structure=new HashMap<>();
		this.quality=new HashMap<>();
		this.loadContentWeights();
		this.loadStructureWeights();
		this.loadMiscScores();
	}
	
	protected void loadContentWeights()
	{
		//code for loading the content weights
		try{
		String contentFile=StaticData.Surf_Data_Base+"/weights/content/"+exceptionID+".txt";
		File f=new File(contentFile);
		Scanner scanner=new Scanner(f);
		//scanner.nextLine(); //discard the header
		while(scanner.hasNext())
		{
			String line=scanner.nextLine();
			String[] parts=line.split("\\s+");
			int key=Integer.parseInt(parts[0]);
			double clonescore=Double.parseDouble(parts[1]);
			double cosscore=Double.parseDouble(parts[2]);
			ArrayList<Double> tempList=new ArrayList<>();
			tempList.add(clonescore);
			tempList.add(cosscore);
			this.content.put(key, tempList);
		}
		scanner.close();
		}catch(Exception exc){
			//handle the exception
		}
	}
	protected void loadStructureWeights()
	{
		//code for loading the structure weights
		try{
			String contentFile=StaticData.Surf_Data_Base+"/weights/structure/"+exceptionID+".txt";
			File f=new File(contentFile);
			Scanner scanner=new Scanner(f);
			//scanner.nextLine(); //discard the header
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				int key=Integer.parseInt(parts[0]);
				double fieldcore=Double.parseDouble(parts[1]);
				double methodcore=Double.parseDouble(parts[2]);
				double objectcore=Double.parseDouble(parts[3]);
				double dependencecore=Double.parseDouble(parts[4]);
				ArrayList<Double> tempList=new ArrayList<>();
				tempList.add(fieldcore);
				tempList.add(methodcore);
				tempList.add(objectcore);
				tempList.add(dependencecore);
				this.structure.put(key, tempList);
			}
			scanner.close();
			}catch(Exception exc){
				//handle the exception
			}
	}
	protected void loadMiscScores()
	{
		//code for loading the quality scores
		try{
			String contentFile=StaticData.Surf_Data_Base+"/weights/quality/"+exceptionID+".txt";
			File f=new File(contentFile);
			Scanner scanner=new Scanner(f);
			//scanner.nextLine(); //discard the header
			while(scanner.hasNext())
			{
				String line=scanner.nextLine();
				String[] parts=line.split("\\s+");
				int key=Integer.parseInt(parts[0]);
				double readability=Double.parseDouble(parts[1]);
				double stmtcount=Double.parseDouble(parts[2]);
				double cratio=Double.parseDouble(parts[3]);
				ArrayList<Double> tempList=new ArrayList<>();
				tempList.add(readability);
				tempList.add(stmtcount);
				tempList.add(cratio);
				this.quality.put(key, tempList);
			}
			scanner.close();
			}catch(Exception exc){
				//handle the exception
			}
	}
	
	protected void createSubTotalScoreFile()
	{
		//code for creating the subtotal scores in file
		//code for creating the scoreFile
				try{
				ArrayList<Integer> sols=SolutionLoader.getSolutionIDs(this.exceptionID);
				int count=0;
				String mycontent=new String();
				for(Integer key:this.content.keySet()){
					String scoreLine=new String();
					
					ArrayList<Double> contents=content.get(key);
					ArrayList<Double> structures=structure.get(key);
					ArrayList<Double> qualities=quality.get(key);
					
					double lexical=ScoreWeights.CloneWeight*contents.get(0).doubleValue();
					double structural=ScoreWeights.CodeObjectFieldMatchWeight*structures.get(0).doubleValue();
					structural+=ScoreWeights.CodeObjectMethodMatchWegiht*structures.get(1).doubleValue();
					structural+=ScoreWeights.CodeObjectMatchWeight*structures.get(2).doubleValue();
					structural+=ScoreWeights.CodeObjectDependencyWegiht*structures.get(3).doubleValue();
					
					double quality=ScoreWeights.ReadabilityWeight*qualities.get(0).doubleValue();
					quality+=ScoreWeights.AvgStmtWeight*qualities.get(1).doubleValue();
					quality+=ScoreWeights.H2cRatioWeight*qualities.get(2).doubleValue();
					
					scoreLine=lexical+"\t"+structural+"\t"+quality;
					
					if(sols.contains(key)){
						scoreLine+="\t"+1;
					}else{
						scoreLine+="\t"+0;
					}
					mycontent+=scoreLine+"\n";
					count++;
					if(count==maxLimit)break;
				}
				String outFile=StaticData.Surf_Data_Base+"/weights/allsubscores.txt";
				FileWriter fwriter=new FileWriter(new File(outFile),true);
				fwriter.write(mycontent);
				fwriter.close();
				}catch(Exception exc){
					exc.printStackTrace();
				}
		
	}
	
	protected void createScoreFile()
	{
		//code for creating the scoreFile
		try{
		ArrayList<Integer> sols=SolutionLoader.getSolutionIDs(this.exceptionID);
		int count=0;
		String mycontent=new String();
		for(Integer key:this.content.keySet()){
			String scoreLine=new String();
			ArrayList<Double> contents=content.get(key);
			ArrayList<Double> structures=structure.get(key);
			ArrayList<Double> qualities=quality.get(key);
			scoreLine=contents.get(0).doubleValue()+"\t"+contents.get(1).doubleValue();
			scoreLine+="\t"+structures.get(0).doubleValue()+"\t"+structures.get(1).doubleValue();
			scoreLine+="\t"+structures.get(2).doubleValue()+"\t"+structures.get(3).doubleValue();
			scoreLine+="\t"+qualities.get(0).doubleValue()+"\t"+qualities.get(1).doubleValue()+"\t"+
			qualities.get(2).doubleValue();
			if(sols.contains(key)){
				scoreLine+="\t"+1;
			}else{
				scoreLine+="\t"+0;
			}
			mycontent+=scoreLine+"\n";
			count++;
			if(count==maxLimit)break;
		}
		String outFile=StaticData.Surf_Data_Base+"/weights/allscore.txt";
		FileWriter fwriter=new FileWriter(new File(outFile),true);
		fwriter.write(mycontent);
		fwriter.close();
		}catch(Exception exc){
			exc.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		for(int i=1;i<=150;i++){
		int exceptionID=i;
		if(new File(StaticData.Surf_Data_Base+"/querycodes/"+i+".txt").exists()==false){
			continue;
		}
		MLWeightMaker maker=new MLWeightMaker(exceptionID);
		maker.createSubTotalScoreFile();
		//maker.createScoreFile();
		}
		
	}
}
