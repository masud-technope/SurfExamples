package surfexamples.app;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import core.CodeFragment;
import core.Result;
import core.SurfExamplesProvider;

public class SurfExamples_appServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String searchQuery;
	String codeContext;
	String charset="UTF-8";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		
		long curr_time=System.currentTimeMillis();
		this.searchQuery=req.getParameter("searchquery");
		this.codeContext=req.getParameter("codecontext");
		
		//decoding of parameters
		try
		{
			this.searchQuery=URLDecoder.decode(this.searchQuery, charset);
			this.codeContext=URLDecoder.decode(this.codeContext, charset);
			
		}catch(Exception exc){
		}
		
		//this.searchQuery="java.net.SocketException: Permission denied: connect";
		//this.stackTrace=get_stack_trace();
		//this.codeContext="";
		
		SurfExamplesProvider provider=new SurfExamplesProvider(searchQuery, codeContext);
		ArrayList<CodeFragment> results=provider.provideFinalRankedExamples();
		int total_result=results.size();
		long end_time=System.currentTimeMillis();
		long duration=end_time-curr_time;
		JSONArray jsonItems=convert_to_json(results);
		resp.setContentType("application/json");
		//resp.getWriter().println("Total results found:"+total_result+" in "+duration+" milliseconds");
		resp.getWriter().print(jsonItems);
		//System.out.println("Total results found:"+total_result+" in "+duration+" milliseconds");
	}
	
	@SuppressWarnings("unchecked")
	protected JSONArray convert_to_json(ArrayList<CodeFragment> myResults)
	{
		//code for converting results into JSON
		JSONArray items=new JSONArray();
		int rank=1;
		for(CodeFragment result:myResults)
		{
			JSONObject jsonObj=new JSONObject();
			try
			{
			jsonObj.put("rank",rank);
			jsonObj.put("title", result.matchedKeywords);
			jsonObj.put("example",result.CompleteCode);
			jsonObj.put("totalscore", result.total_lexical_structural_readability_handlerquality_score);
			jsonObj.put("structure", result.StructuralSimilarityScore);
			jsonObj.put("lexical", result.LexicalSimilarityScore);
			jsonObj.put("quality", result.HandlerQualityScore);
			items.add(jsonObj);
			rank++;
			}catch(Exception exc){}
			
		}
		return items;
	}
	
	protected Result[] convert_to_result_array(ArrayList<CodeFragment> myResults) {
		// code for converting the items to result objects
		ArrayList<Result> items = new ArrayList<>();
		for (CodeFragment fragment : myResults) {
			Result result = new Result();
			result.matchedKeyWords = fragment.matchedKeywords;
			result.completeCode = fragment.CompleteCode;
			result.totalScore = fragment.total_lexical_structural_readability_handlerquality_score;
			result.structural_relevance = fragment.StructuralSimilarityScore;
			result.content_relevance = fragment.LexicalSimilarityScore;
			result.handler_quality = fragment.HandlerQualityScore;
			items.add(result);
		}
		int len = items.size();
		Result[] results = new Result[len];
		// returning the array
		return items.toArray(results);
	}
	

}
