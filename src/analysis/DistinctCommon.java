package analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import core.StaticData;

public class DistinctCommon {

	ArrayList<Integer> item1;
	ArrayList<Integer> item2;

	public DistinctCommon(ArrayList<Integer> item1, ArrayList<Integer> item2) {
		// initialization
		this.item1 = item1;
		this.item2 = item2;
	}

	
	
	protected static ArrayList<Integer> getDistinctIn1st(ArrayList<Integer> item1, ArrayList<Integer> item2) {
		ArrayList<Integer> distinct = new ArrayList<>();
		for (Integer key : item1) {
			if (!item2.contains(key)) {
				distinct.add(key);
			}
		}
		return distinct;
	}

	protected static ArrayList<Integer> getDistinctIn2nd(ArrayList<Integer> item1, ArrayList<Integer> item2) {
		ArrayList<Integer> distinct = new ArrayList<>();
		for (Integer key : item2) {
			if (!item1.contains(key)) {
				distinct.add(key);
			}
		}
		return distinct;
	}

	protected static ArrayList<Integer> getCommon(ArrayList<Integer> item1, ArrayList<Integer> item2) {
		ArrayList<Integer> common = new ArrayList<>();
		for (Integer key : item2) {
			if (item1.contains(key)) {
				common.add(key);
			}
		}
		return common;
	}
	protected static ArrayList<Integer> getUnion(ArrayList<Integer> item1, ArrayList<Integer> item2){
		HashSet hset=new HashSet<>();
		hset.addAll(item1);
		hset.addAll(item2);
		ArrayList<Integer> temp=new ArrayList<>();
		temp.addAll(hset);
		return temp;
		
	}
	
	
	
	static ArrayList<Integer> getContent(String fileName){
		File f = new File(fileName);
		ArrayList<Integer> temp = new ArrayList<>();
		try{
		Scanner scanner = new Scanner(f);
		while (scanner.hasNext()) {
			int id = Integer.parseInt(scanner.nextLine().trim());
			temp.add(id);
		}}catch(Exception e){}
		return temp;
	}
	
	
	public static void main(String[] args)
	{
		String stf=StaticData.Surf_Data_Base+"/analysis/s5.txt";
		String lexf=StaticData.Surf_Data_Base+"/analysis/l5.txt";
		String qualityf=StaticData.Surf_Data_Base+"/analysis/q5.txt";
		String allf=StaticData.Surf_Data_Base+"/analysis/a5.txt";
		ArrayList<Integer> list1=getContent(stf);
		ArrayList<Integer> list2=getContent(lexf);
		ArrayList<Integer> list3=getContent(qualityf);
		ArrayList<Integer> list4=getContent(allf);
		System.out.println("Total:"+list4.size());
//		ArrayList<Integer> common1=getCommon(list3, list1);
//		System.out.println("Common1:"+common1.size());
		ArrayList<Integer> union1=getUnion(list1, list2);
		ArrayList<Integer> union2=getUnion(list3, union1);
		System.out.println(union2.size());
		ArrayList<Integer> distinct1=getDistinctIn1st(list4, union2);
		System.out.println("Only in 1st:"+distinct1.size());
		//System.out.println("S and L:"+common1.size());
		//ArrayList<Integer> common2=getCommon(common1,list3);
		//System.out.println("All 3 common:"+common2.size());
		
		
	}

}
