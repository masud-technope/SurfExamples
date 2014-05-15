package compare.heuristic;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import java.io.ByteArrayInputStream;
import visitor.HeuristicVisitor;
import core.ECodeFragment;

public class HInputDocProcessor {
	
	String contextCode;
	ECodeFragment inputFragment;
	String exceptionName;
	int index=0;
	
	public HInputDocProcessor(String exceptionName, String contextCode)
	{
		//initialization
		this.contextCode=contextCode;
		this.exceptionName=exceptionName;
		inputFragment=new ECodeFragment();
	}
	public ECodeFragment extractInputDocumentInfo()
	{
		//code for extracting structural info of input document
		try{
			CompilationUnit cu=JavaParser.parse(new ByteArrayInputStream(this.contextCode.getBytes()));
			if(cu==null){
				System.err.println("The code block is not compilable");
			}
			HeuristicVisitor visitor=new HeuristicVisitor(exceptionName);
			visitor.visit(cu, null);
			if(visitor.Fragments.size()>0)
			this.inputFragment=visitor.Fragments.get(index); //getting the first method involving the exception
		}catch(Exception exc){
			exc.printStackTrace();
		}
		return this.inputFragment;
	}
	

}
