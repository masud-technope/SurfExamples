package casestudy;

import global.StaticData;
import japa.parser.JavaParser;
import japa.parser.TokenMgrError;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.Statement;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import core.ExceptionHandler;
import visitor.CustomSourceVisitor;

public class CollectHandler {

	/**
	 * @param args
	 */
	String sourceFolder;
	public CollectHandler(String sourceFolder)
	{
		this.sourceFolder=sourceFolder;
	}
	
	protected int collectExceptionHandlers(int startID)
	{
		//code for collecting exception handlers
		int handlerID = startID;
		try {
			File f = new File(this.sourceFolder);
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				for (File srcFile : files) {
					try {
						CompilationUnit cu = JavaParser.parse(srcFile);
						CustomSourceVisitor visitor = new CustomSourceVisitor();
						visitor.visit(cu, null);
						handlerID = saveHandler(handlerID, visitor.handlercoll);
					} catch (Exception exc) {
					} catch (TokenMgrError e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception exc) {
			// handle the exception
		}
		return handlerID;
	}
	
	protected int saveHandler(int handlerID,ArrayList<HashMap<String,BlockStmt>> handlers){
		for(HashMap<String, BlockStmt> map:handlers){
			String fileName=StaticData.handlerDir+"/"+handlerID+".ser";
			ExceptionHandler handler=new ExceptionHandler();
			handler.handlerID=handlerID;
			HashMap<String, ArrayList<String>> newMap=new HashMap<>();
			for(String key:map.keySet()){
				BlockStmt block=map.get(key);
				List<Statement> stmts=block.getStmts();
				ArrayList<String> actions=new ArrayList<>();
				for(Statement stmt:stmts){
					actions.add(stmt.toString());
				}
				newMap.put(key,actions);
			}
			handler.handlers=newMap; 
			//now write the object
			File f=new File(fileName);
			try {
				ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream(f));
				oos.writeObject(handler);
				oos.close();
				handlerID++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return handlerID;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//String srcFolders[]={"Ant-Contrib","AOI","Carol","DNSJava","Fidocadj","Groimp","Im4java","Jabref","Javapeg","JFreeChart",
		//		"Jgeom","JHotDraw","JID","JKiwi","JLine","Jomic","JSch","Jtds","K-9","Notelab","TreeView"};
		String srcFolders[]={"android","androidannotations","AndroidImageLoader","AndroidStaggeredGrid","libgdx"};
		int startID=0;
		//for(String src:srcFolders){
		for(int i=1;i<=150;i++){
		String srcFolder=StaticData.sourceDir+"/"+i;
		if(new File(srcFolder).exists()){
		CollectHandler collector=new CollectHandler(srcFolder);
		startID=collector.collectExceptionHandlers(startID);
		}
		}
	}
}
