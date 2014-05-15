package misc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;

import core.StaticData;

public class DistinctExcep {
	
	public static void main(String[] args){
		String excep=StaticData.Surf_Data_Base+"/exceplist.txt";
		HashSet<String> exceps=new HashSet<>();
		try {
			Scanner scanner=new Scanner(new File(excep));
			while(scanner.hasNextLine())
			{
				String[] parts=scanner.nextLine().split("\\s+");
				String excepName=parts[1].trim();
				exceps.add(excepName);
			}
			System.out.println("Distinct exceptions:"+exceps.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
