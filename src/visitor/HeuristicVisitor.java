package visitor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import core.ECodeFragment;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class HeuristicVisitor extends VoidVisitorAdapter{

	public ArrayList<ECodeFragment> Fragments;
	String exceptionName;
	static HashSet<String> variableTypes;
	static HashSet<String> methodCalls;
	static HashSet<String> handles;
	public HeuristicVisitor(String exceptionName)
	{
		this.exceptionName=exceptionName;
		this.Fragments=new ArrayList<>();
		variableTypes=new HashSet<>();
		methodCalls=new HashSet<>();
		handles=new HashSet<>();
	}
	public ArrayList<ECodeFragment> getExtractedFragments()
	{
		//returning the code fragments
		return this.Fragments;
	}
	
	@Override
	public void visit(MethodDeclaration m, Object obj){
		String completeCode=new String();
		BlockStmt body=m.getBody();
		//check if the body contains the handler code
		if(body.toString().contains(this.exceptionName)){
			//storing complete code
			completeCode=m.toString(); // body.toString();
			//visiting method parameters
			List<Parameter> params = m.getParameters();
			if (params != null) {
				for (Parameter param : params) {
					String paramType = param.getType().toString();
					variableTypes.add(paramType);
				}
			}
			//visiting statements
			List<Statement> stmts = body.getStmts();
			for (Statement stmt : stmts) {
				stmt.accept(this, obj);
			}
			//all statements are browsed
			addMethodDetails(completeCode);
			//save the information for each method
			//saveMethodInfo();
			//MethodInfoDisplay.showMethodDetails(codeObjectMap);
			//MethodInfoDisplay.showDependencies(dependencies);
			//clear the hashmap
			clearContainers();
		}
	}
	
	protected void addMethodDetails(String completeCode)
	{
		//code for saving the browsed method details
			try{
				ECodeFragment codeFragment=new ECodeFragment();
				codeFragment.ExceptionName=this.exceptionName;
				codeFragment.CompleteCode=completeCode; 
				codeFragment.variableTypes.addAll(variableTypes);
				codeFragment.methodCalled.addAll(methodCalls);
				Fragments.add(codeFragment);
			}catch(Exception exc){
				//handle the exception
				exc.printStackTrace();
			}
	}
	
	protected void clearContainers()
	{
		//clearing containers
		variableTypes.clear();
		methodCalls.clear();
		handles.clear();
	}
	@Override
	public void visit(TryStmt trystmt, Object obj){
		if (trystmt instanceof TryStmt) {
			// get the t statement
			TryStmt tstmt = (TryStmt) trystmt;
			// catch clauses
			List<CatchClause> catches = tstmt.getCatchs();
			if(catches!=null){
			for (CatchClause myclause : catches) {
				String caughtName=myclause.getExcept().getType().toString();
				handles.add(caughtName);
			}}
			//browsing through the statements in try block
			BlockStmt tryblock=tstmt.getTryBlock();
			List<Statement> stmts=tryblock.getStmts();
			for(Statement stmt:stmts){
				stmt.accept(this, obj);
			}
		}
	}
	
	@Override
	public void visit(VariableDeclarationExpr expr, Object obj){
		String TypeName=expr.getType().toString();
		variableTypes.add(TypeName);
	}
	
	@Override
	public void visit(MethodCallExpr expr, Object obj){
		String call=expr.getName();
		methodCalls.add(call);
	}
	
	
	
}
