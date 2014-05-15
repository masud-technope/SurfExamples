package structural;

import java.io.ByteArrayInputStream;
import visitor.CustomSourceVisitor;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import core.CodeFragment;

public class InputDocProcessor {
	
	String contextCode;
	CodeFragment inputFragment;
	String exceptionName;
	int index=0;
	
	public InputDocProcessor(String exceptionName, String contextCode)
	{
		//initialization
		this.contextCode=contextCode;
		this.exceptionName=exceptionName;
		inputFragment=new CodeFragment();
	}
	
	public CodeFragment extractInputDocumentInfo()
	{
		//code for extracting structural info of input document
		try{
			CompilationUnit cu=JavaParser.parse(new ByteArrayInputStream(this.contextCode.getBytes()));
			if(cu==null){
				System.err.println("The code block is not compilable");
			}
			CustomSourceVisitor visitor=new CustomSourceVisitor(exceptionName);
			visitor.visit(cu, null);
			if(visitor.Fragments.size()>0)
			this.inputFragment=visitor.Fragments.get(index); //getting the first method involving the exception
		}catch(Exception exc){
			exc.printStackTrace();
		}
		return this.inputFragment;
	}
	
	
	
}
