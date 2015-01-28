package visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import core.CodeFragment;
import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.FieldDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.body.VariableDeclarator;
import japa.parser.ast.body.VariableDeclaratorId;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.VariableDeclarationExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.CatchClause;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.stmt.TryStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class CustomSourceVisitor extends VoidVisitorAdapter {
	
	private static HashMap<String, CodeObject> codeObjectMap;
	private static ArrayList<Dependence> dependencies;
	private static ArrayList<CustomCatchClause> catchClauses;
	private static HashMap<String, String> IDTypeMap;
	private HashSet<String> importedClasses;
	String exceptionName;
	public ArrayList<CodeFragment> Fragments;
	public String errorMessage;
	boolean querycode=false;
	
	
	public CustomSourceVisitor(String exceptionName,boolean querycode)
	{
		//initializing the code objects
		codeObjectMap=new HashMap<>();
		dependencies=new ArrayList<>();
		IDTypeMap=new HashMap<>();
		catchClauses=new ArrayList<>();
		
		//storing the exception name
		this.exceptionName=exceptionName;
		//initializing code fragments container
		this.Fragments=new ArrayList<>();
		//initiating imported classes
		this.importedClasses=new HashSet<>();
		//checking query code
		this.querycode=querycode;
	}
	
	public ArrayList<CodeFragment> getExtractedFragments()
	{
		//returning the code fragments
		return this.Fragments;
	}
	
	@Override
	public void visit(ImportDeclaration importdec, Object obj){
		//visit the constructor
		//System.out.println(imports.toString());
		try{
		String cano_className=importdec.getName().toString();
		String[] parts=cano_className.split("\\.");
		String lastPart=parts[parts.length-1];
		if(!lastPart.equals("*")){
			//adding imported classes
			importedClasses.add(lastPart);
		}}catch(Exception exc){
			errorMessage=exc.getMessage();
			exc.printStackTrace();
		}
	}
	
	@Override
	public void visit(FieldDeclaration field, Object obj){
		//visit the declared fields in the class
		try{
		String memberType=field.getType().toString();
		if(DataTypes.typeclasses.contains(memberType)){
			return;
		}
		//creating a code object for this Type
		CodeObject codeObject=new CodeObject(memberType);
		if(!codeObjectMap.containsKey(memberType))
		codeObjectMap.put(memberType, codeObject);
		else codeObject=codeObjectMap.get(memberType);
		
		List<VariableDeclarator> declarations=field.getVariables();
		for(VariableDeclarator dec:declarations){
			VariableDeclaratorId ID=dec.getId();
			//storing the identifier for later usage
			IDTypeMap.put(ID.toString(), memberType);
		}}catch(Exception exc){
			errorMessage=exc.getMessage();
			exc.printStackTrace();
		}
	}

	@Override
	public void visit(MethodDeclaration m, Object obj){
		//visit the method
		//check if the body contains the handler code
		BlockStmt body=m.getBody();
		if(this.querycode){
			analyzeMethodBody(m, obj);
		}else{
		if(body.toString().contains(this.exceptionName)){
			//storing complete code
			analyzeMethodBody(m, obj);
		}}
	}
	
	protected void analyzeMethodBody(MethodDeclaration m, Object obj)
	{
		//analyzing the method body
		int startLine=m.getBeginLine();
		int endLine=m.getEndLine();
		String completeCode=new String();
		BlockStmt body=m.getBody();
		completeCode=m.toString(); // body.toString();
		//visiting method parameters
		List<Parameter> params = m.getParameters();
		if (params != null) {
			for (Parameter param : params) {
				String paramType = param.getType().toString();
				if (!DataTypes.typeclasses.contains(paramType)) {
					CodeObject codeObject = new CodeObject(paramType);
					codeObjectMap.put(paramType, codeObject);
					IDTypeMap.put(param.getId().toString(), paramType);
				}
			}
		}
		//visiting statements
		List<Statement> stmts = body.getStmts();
		for (Statement stmt : stmts) {
			stmt.accept(this, obj);
		}
		//all statements are browsed
		addMethodDetails(completeCode,startLine,endLine);
		//save the information for each method
		//saveMethodInfo();
		//MethodInfoDisplay.showMethodDetails(codeObjectMap);
		//MethodInfoDisplay.showDependencies(dependencies);
		//clear the HashMap
		clearContainers();
	}
	
	
	protected void addMethodDetails(String completeCode, int startLine, int endLine)
	{
		//code for adding details of the browsed method
		try{
			CodeFragment codeFragment=new CodeFragment();
			codeFragment.ExceptionName=this.exceptionName;
			codeFragment.CompleteCode=completeCode; 
			//scope of the method
			codeFragment.beginLine=startLine;
			codeFragment.endLine=endLine;
			//copying hash map
			codeFragment.codeObjectMap=(HashMap<String, CodeObject>) codeObjectMap.clone();
			codeFragment.dependencies.addAll(dependencies);
			codeFragment.handlers.addAll(catchClauses);
			//add to the collection
			Fragments.add(codeFragment);
			
		}catch(Exception exc){
			//handle the exception
			exc.printStackTrace();
		}
	}
	protected boolean analyzeEligibilityOfCatchClause(CatchClause myclause)
	{
		// code for analyzing the eligibility of catch Clause
		boolean eligible = false;
		try {
			BlockStmt catchbody = myclause.getCatchBlock();
			List<Statement> stmts = catchbody.getStmts();
			if (stmts == null || stmts.size() == 0) {
				eligible = false;
				return eligible;
			}
			if (stmts.size() == 1) {
				Statement mystmt = stmts.get(0);
				if (mystmt.toString().contains("printStackTrace")) {
					// just prints the stack trace
					eligible = false;
				} else
					eligible = true;
			} else if (stmts.size() >= 2) {
				eligible = true;
			}

		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return eligible;
	}
	
	
	protected void clearContainers()
	{
		//clearing containers
		codeObjectMap.clear();
		dependencies.clear();
		IDTypeMap.clear();
		catchClauses.clear();
	}
	

	protected void addParamDependency(String fromTypeName, String destTypeName,String methodName)
	{
		//code for adding dependency relationships
		CodeObject fromObj=codeObjectMap.get(fromTypeName);
		CodeObject destObj=codeObjectMap.get(destTypeName);
		if(destObj==null){
			//if not created yet
			destObj=new CodeObject(destTypeName);
			codeObjectMap.put(destTypeName, destObj);
		}
		Dependence dependence=new Dependence();
		dependence.fromObject=fromObj;
		dependence.destObject=destObj;
		dependence.dependenceName=methodName;
		dependencies.add(dependence);
	}
	
	protected void addAField(String TypeName, String FieldName)
	{
		//add a field access
		CodeObject codeObj=codeObjectMap.get(TypeName);
		codeObj.fields.add(FieldName);
		codeObjectMap.put(TypeName, codeObj);
	}
	
	protected void addAMethod(String TypeName, String methodName){
		//add a method call
		CodeObject codeObj=codeObjectMap.get(TypeName);
		codeObj.methods.add(methodName);
		codeObjectMap.put(TypeName, codeObj);
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
				if (analyzeEligibilityOfCatchClause(myclause)) {
					//add eligible clauses within a method
					String exceptionParams=myclause.getExcept().toString();
					String catchBlock=myclause.getCatchBlock().toString();
					catchClauses.add(new CustomCatchClause(exceptionParams,catchBlock));
				}
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
	public void visit(ObjectCreationExpr expr, Object obj){
		//System.out.println("Object:"+expr);
		try{
			String TypeName=expr.getType().toString();
			String constructorName=TypeName;
			//check if the type already entered
			if(!codeObjectMap.containsKey(TypeName)){
				//create an entry
				CodeObject mycodeObject=new CodeObject(TypeName);
				//String methodName=TypeName+"()"; //constructor
				mycodeObject.methods.add(constructorName);
				codeObjectMap.put(TypeName, mycodeObject);
			}else{
				CodeObject mycodeObject=codeObjectMap.get(TypeName);
				//String methodName=TypeName+"()"; //constructor
				mycodeObject.methods.add(constructorName);
				codeObjectMap.put(TypeName, mycodeObject);
			}
			
			//now discover the arguments
			List<Expression> args=expr.getArgs();
			for(Expression pexpr:args){
				if(IDTypeMap.containsKey(pexpr.toString())){
					String paramType=IDTypeMap.get(pexpr.toString());
					//now encode the parameter relationship
					addParamDependency(TypeName, paramType,constructorName);
				}else
				{
					// if identifier is not discovered yet
					String parameterType = analyzeParameterType(pexpr);
					if (parameterType != null) {
						if (!parameterType.isEmpty())
							addParamDependency(TypeName, parameterType,
									constructorName);
					}
					pexpr.accept(this, obj);
					//now check if you added a dependency
					
				}
				
			}
		}catch(Exception exc){
			
		}
	}
	
	protected String analyzeParameterType(Expression expr)
	{
		String parameterType=new String();
		if(expr instanceof ObjectCreationExpr){
			ObjectCreationExpr oexpr=(ObjectCreationExpr) expr;
			parameterType=oexpr.getType().toString();
		}else if(expr instanceof MethodCallExpr){
			MethodCallExpr mexpr=(MethodCallExpr) expr;
			try{
				parameterType=IDTypeMap.get(mexpr.getScope().toString());
			}catch(Exception exc){
				
			}
		}else if(expr instanceof FieldAccessExpr){
			FieldAccessExpr mexpr=(FieldAccessExpr) expr;
			try{
				parameterType=IDTypeMap.get(mexpr.getScope().toString());
			}catch(Exception exc){
				
			}
		}
		return parameterType;
	}
	
	
	
	@Override
	public void visit(VariableDeclarationExpr expr, Object obj){
		//System.out.println("Variable:"+expr);
		
		String TypeName=expr.getType().toString();
		if(DataTypes.typeclasses.contains(TypeName)){
			return;
		}
		//creating a code object for this Type
		CodeObject codeObject=new CodeObject(TypeName);
		if(!codeObjectMap.containsKey(TypeName))
		codeObjectMap.put(TypeName, codeObject);
		else codeObject=codeObjectMap.get(TypeName);
		
		List<VariableDeclarator> declarations=expr.getVars();
		for(VariableDeclarator dec:declarations){
			VariableDeclaratorId ID=dec.getId();
			IDTypeMap.put(ID.toString(), TypeName);
			dec.accept(this, obj);
		}
	}
	
	
	@Override
	public void visit(AssignExpr expr, Object obj) {
		
		// handling the assignment expression
		Expression targetExpr = expr.getTarget();
		Expression valueExpr = expr.getValue();
		String fromType = null;
		String destType = null;
		try{
		String targetObj=null;
		if(targetExpr instanceof FieldAccessExpr){
			targetObj=((FieldAccessExpr)targetExpr).getScope().toString();
		}
		
		if (IDTypeMap.containsKey(targetExpr.toString())|| IDTypeMap.containsKey(targetObj) ) {
			
			//collecting destination type
			destType = IDTypeMap.get(targetExpr.toString()).toString();
			if(destType==null || destType.isEmpty())
				destType=IDTypeMap.get(targetObj).toString();
			
			if (valueExpr instanceof FieldAccessExpr) {
				String scope = ((FieldAccessExpr) valueExpr).getScope()
						.toString();
				String fieldName = ((FieldAccessExpr) valueExpr).getField()
						.toString();
				// make them browse the field access visitor
				valueExpr.accept(this, obj);
				if (IDTypeMap.containsKey(scope)) {
					fromType = IDTypeMap.get(scope).toString();
					addParamDependency(fromType, destType, fieldName);
				}

			} else if (valueExpr instanceof MethodCallExpr) {
				String scope = null;
				try {
					scope = ((MethodCallExpr) valueExpr).getScope().toString();
				} catch (Exception exc) {
					// handle the exception
					valueExpr.accept(this, obj);
					return;
				}
				String methodName = ((MethodCallExpr) valueExpr).getName()
						.toString();
				// make them browse the method access visitor
				valueExpr.accept(this, obj);
				if (IDTypeMap.containsKey(scope)) {
					fromType = IDTypeMap.get(scope).toString();
					addParamDependency(fromType, destType, methodName);
				}

			} else {
				valueExpr.accept(this, obj);
			}
		} else {
			targetExpr.accept(this, obj);
		}
		}catch(Exception exc){
			errorMessage=exc.getMessage();
		}
	}
	
	@Override
	public void visit(FieldAccessExpr expr, Object obj){
		//System.out.println("Field:"+expr);
		try{
			String identifier=expr.getScope().toString();
			if(IDTypeMap.containsKey(identifier)){
				String fieldName=expr.getField();
				String TypeName=IDTypeMap.get(identifier);
				addAField(TypeName, fieldName);
			}else{
				//in case of a static field
				if(this.importedClasses.contains(identifier))
				{
					//adding necessary information
					String className=identifier;
					CodeObject codeObject=new CodeObject(className);
					if(!codeObjectMap.containsKey(className))
					codeObjectMap.put(className, codeObject);
					if(!IDTypeMap.containsKey(className))
					IDTypeMap.put(className, className);
					//revisiting the method call expression
					expr.accept(this, obj);	
				}
			}
			
			
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	@Override
	public void visit(MethodCallExpr expr, Object obj){
		//System.out.println("Method call:"+expr);
		try{
			String identifier=expr.getScope().toString();
			if(IDTypeMap.containsKey(identifier)){
				String methodName=expr.getName();
				String TypeName=IDTypeMap.get(identifier);
				addAMethod(TypeName, methodName);
				
				//now discover the parameters
				List<Expression> params=expr.getArgs();
				for(Expression pexpr:params){
					if(IDTypeMap.containsKey(pexpr.toString())){
						String paramType=IDTypeMap.get(pexpr.toString());
						//now encode the parameter relationship
						addParamDependency(TypeName, paramType,methodName);
					}else
					{
						//if identifier is not discovered yet
						String parameterType=analyzeParameterType(pexpr);
						if(parameterType!=null){
							if(!parameterType.isEmpty())
							addParamDependency(TypeName, parameterType,methodName);
						}
						pexpr.accept(this, obj);
					}
				}
			}else
			{
				// in case of static method call
				String scope = identifier; // it may be a static class
				if (this.importedClasses.contains(scope)) {
					//adding necessary information
					String className=scope;
					CodeObject codeObject=new CodeObject(className);
					if(!codeObjectMap.containsKey(className))
					codeObjectMap.put(className, codeObject);
					if(!IDTypeMap.containsKey(className))
					IDTypeMap.put(className, className);
					//revisiting the method call expression
					expr.accept(this, obj);
				}
			}
		}catch(Exception exc){
			//handle the exception
		}
	}
	
	
	
	
	
	
	
	
}
