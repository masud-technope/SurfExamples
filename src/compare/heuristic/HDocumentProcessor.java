package compare.heuristic;

import japa.parser.JavaParser;
import japa.parser.TokenMgrError;
import japa.parser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import visitor.HeuristicVisitor;
import core.ECodeFragment;

public class HDocumentProcessor {
	
	String documentContent;
	String exceptionName;
	ArrayList<ECodeFragment> Fragments;
	
	public HDocumentProcessor(String exceptionName, String documentContent){
		//iniitialization
		this.documentContent=documentContent;
		this.exceptionName=exceptionName;
		this.Fragments=new ArrayList<>();
	}
	public ArrayList<ECodeFragment> collectExtractedCodeFragments()
	{
		//visit the methods and extract information
		processSourceCodes();
		//return the fragments
		return this.Fragments;
	}
	protected void processSourceCodes()
	{
		//code for processing the source code (compilable)
		try{
			InputStream inputStream=new ByteArrayInputStream(this.documentContent.getBytes());
			CompilationUnit cu=JavaParser.parse(inputStream);
			HeuristicVisitor visitor=new HeuristicVisitor(exceptionName);
			//cu.accept(visitor, null);
			visitor.visit(cu, null);
			//store extracted fragments
			this.Fragments.addAll(visitor.Fragments);
			//System.out.println("Code fragments extracted:"+visitor.Fragments.size());
		}catch(Exception exc){
			//handle the exception
			//exc.printStackTrace();
		} 
		catch(TokenMgrError err){
			System.err.println("Failed to compile:"+err.getMessage());
		}
	}

}
