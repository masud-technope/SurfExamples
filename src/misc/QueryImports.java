package misc;

import indexing.CFIndexManager;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.File;

import java.util.ArrayList;

import utility.SolutionLoader;
import visitor.ImportVisitor;
import core.CodeFragment;
import core.StaticData;

public class QueryImports {
	
	protected int getSolutionID(int exceptionID)
	{
		//code for getting the solution ID
		return SolutionLoader.getSolutionID(exceptionID);
	}
	
	protected String getTargetSourceFile(int exceptionID)
	{
		//target solution path
		String sourceFile=new String();
		int solutionID=getSolutionID(exceptionID);
		ArrayList<CodeFragment> fragments=CFIndexManager.readCodeFragment(exceptionID);
		CodeFragment targetFrag=fragments.get(solutionID);
		//source code file name
		return targetFrag.sourceFileID;
	}
	
	protected void collectImports(int exceptionID)
	{
		//code for collecting the import statements
		try{
			String sourceFileID=getTargetSourceFile(exceptionID);
			String sourceFilePath=StaticData.Surf_Data_Base+"/codes/"+exceptionID+"/"+sourceFileID;
			System.out.println(sourceFilePath);
			File srcFile=new File(sourceFilePath);
			CompilationUnit cu=JavaParser.parse(srcFile);
			if(cu!=null){
				ImportVisitor visitor=new ImportVisitor();
				visitor.visit(cu, null);
				ArrayList<String> imports=visitor.imports;
				ArrayList<String> fields=visitor.fieldDecs;
				//now show the imports
				for(String str:fields){
					System.out.println(str);
				}
			}else
			{
				System.err.println("The source file not compilable:"+sourceFilePath);
			}
			
		}catch(Exception exc){
			//handle it
		}
	}
	public static void main(String[] args){
		int exceptionID=150;
		QueryImports manager=new QueryImports();
		manager.collectImports(exceptionID);
		
		
	}

}
