package misc;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.File;

import core.StaticData;

public class CompileTester {
	public static void main(String[] args){
		String querycode=StaticData.Surf_Data_Base+"/ccontext";
		File fdir=new File(querycode);
		if(fdir.isDirectory()){
			File[] files=fdir.listFiles();
			for(File f2:files){
				try{
				CompilationUnit cu=JavaParser.parse(f2);
				if(cu!=null){
					System.out.println("Compiled:"+f2.getName());
				}
				}catch(Exception exc){
					//handle the exception
					System.err.println("Compile failed:"+f2.getName());
				}
			}
		}
	}
}
