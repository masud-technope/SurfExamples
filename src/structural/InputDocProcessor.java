package structural;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import utility.ContextCodeLoader;
import visitor.CustomSourceVisitor;
import japa.parser.JavaParser;
import japa.parser.TokenMgrError;
import japa.parser.ast.CompilationUnit;
import core.CodeFragment;

public class InputDocProcessor {
	
	String contextCode;
	CodeFragment inputFragment;
	String exceptionName;
	int index=0;
	int targetExceptionLine;
	
	public InputDocProcessor(String exceptionName, String contextCode, int targetExceptionLine)
	{
		//initialization
		this.contextCode=contextCode;
		this.exceptionName=exceptionName;
		inputFragment=new CodeFragment();
		this.targetExceptionLine=targetExceptionLine;
	}
	
	public CodeFragment extractInputDocumentInfo()
	{
		//code for extracting structural info of input document
		try{
			InputStream istream=new ByteArrayInputStream(this.contextCode.getBytes());
			//CompilationUnit cu=JavaParser.parse(new ByteArrayInputStream(this.contextCode.getBytes()));
			CompilationUnit cu=JavaParser.parse(istream);
			if(cu==null){
				System.err.println("The context code is not compilable");
			}
			CustomSourceVisitor visitor=new CustomSourceVisitor(exceptionName);
			visitor.visit(cu, null);
			ArrayList<CodeFragment> frags=visitor.getExtractedFragments();
			
			if(frags.size()>1){
			for(CodeFragment cf:frags){
				if(targetExceptionLine>cf.beginLine && targetExceptionLine<cf.endLine){
					this.inputFragment=cf; //getting the first method involving the exception
					break;
				}
			}}else{
				this.inputFragment=frags.get(0);
			}
		}catch(Exception exc){
			exc.printStackTrace();
		}catch(TokenMgrError err){
			System.err.println("Failed to compile the context code:"+err.getMessage());
		}
		return this.inputFragment;
	}
	
	public static void main(String[] args){
		String exceptionName="SocketException";
		String contextcode=ContextCodeLoader.loadContextCode(35);
		InputDocProcessor processor=new InputDocProcessor(exceptionName, contextcode,5);
		CodeFragment cf=processor.extractInputDocumentInfo();
		System.out.println(cf.codeObjectMap.size());
		for(String key:cf.codeObjectMap.keySet()){
			System.out.println(key);
		}
	}
}
