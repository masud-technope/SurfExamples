package structural;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import core.CodeFragment;
import core.StaticData;
import visitor.CustomSourceVisitor;
import japa.parser.JavaParser;
import japa.parser.TokenMgrError;
import japa.parser.ast.CompilationUnit;

public class SourceDocumentProcessor {
	
	String documentContent;
	String exceptionName;
	ArrayList<CodeFragment> Fragments;
	public SourceDocumentProcessor(String exceptionName, String documentContent)
	{
		this.documentContent=documentContent;
		this.exceptionName=exceptionName;
		this.Fragments=new ArrayList<>();
	}
	
	
	public ArrayList<CodeFragment> collectExtractedCodeFragments()
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
			CustomSourceVisitor visitor=new CustomSourceVisitor(this.exceptionName);
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
	
	public static void main(String[] args){
		try {
			File f = new File(StaticData.Surf_Data_Base+"/codes/135/1.java");
			Scanner scanner = new Scanner(f);
			String content = new String();
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				content += line + "\n";
			}
			scanner.close();
			String exceptionName="XMLStreamException";
			SourceDocumentProcessor sprocessor=new SourceDocumentProcessor(exceptionName,content);
			sprocessor.processSourceCodes();

		} catch (Exception exc) {

		}
	}
}
