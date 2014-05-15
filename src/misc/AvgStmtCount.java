package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import core.StaticData;

public class AvgStmtCount {
	
	public static void main(String[] args){
		String scoreFile=StaticData.Surf_Data_Base+"/weights/allscore.txt";
		int relcount=0;
		int irrelcount=0;
		
		int relevant=0;
		int irrelevant=0;
		
		try {
			Scanner scanner=new Scanner(new File(scoreFile));
			while(scanner.hasNextLine())
			{
				String[] parts=scanner.nextLine().split("\\s+");
				int label=Integer.parseInt(parts[parts.length-1]);
				double stmt=Double.parseDouble(parts[parts.length-3]);
				if(label==1){
					relcount+=stmt;
					relevant++;
				}
				else {
					irrelcount+=stmt;
					irrelevant++;
				}
			}
			System.out.println("Relevant count:"+relcount+" with "+relevant);
			System.out.println("Irrelevant count:"+irrelcount+" with "+irrelevant);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
