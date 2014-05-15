package app;

import java.io.File;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import core.StaticData;

public class MySearchFiles {

	/**
	 * @param args
	 */
	String searchQuery;
	ArrayList<LuceneResult> LEntryList;
	
	String index = StaticData.Dataset_home+"/"+"index";
    String field = "contents";
    String queries = null;
    int repeat = 0;
    boolean raw = true;
    String queryString = null;
    int hitsPerPage = 10;
	
	public MySearchFiles(String searchQuery,int exceptionID, String[] args)
	{
		this.searchQuery=searchQuery;
		this.LEntryList=new ArrayList<>();
		index+="/"+exceptionID;
		
		//assigning variables
		for(int i = 0;i < args.length;i++) {
		      if ("-index".equals(args[i])) {
		        index = args[i+1];
		        i++;
		      } else if ("-field".equals(args[i])) {
		        field = args[i+1];
		        i++;
		      } else if ("-queries".equals(args[i])) {
		        queries = args[i+1];
		        i++;
		      } else if ("-query".equals(args[i])) {
		        queryString = args[i+1];
		        i++;
		      } else if ("-repeat".equals(args[i])) {
		        repeat = Integer.parseInt(args[i+1]);
		        i++;
		      } else if ("-raw".equals(args[i])) {
		        raw = true;
		      } else if ("-paging".equals(args[i])) {
		        hitsPerPage = Integer.parseInt(args[i+1]);
		        if (hitsPerPage <= 0) {
		          System.err.println("There must be at least 1 hit per page.");
		          System.exit(1);
		        }
		        i++;
		      }
		    }
	}
	
	
	protected long getPostID(String fileURL)
	{
		//code for getting post id
		File file=new File(fileURL);
		String fileName=file.getName();
		String postIDStr=fileName.split("\\.")[0];
		return Long.parseLong(postIDStr);
	}
	
	public ArrayList<LuceneResult> find_lucene_results()
	{
		//code for performing Lucene search
		try
		{
		 IndexReader reader = DirectoryReader.open(FSDirectory.open(new File(index)));
		 IndexSearcher searcher = new IndexSearcher(reader);
		 Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
		 QueryParser parser=new QueryParser(Version.LUCENE_40, field, analyzer);
		 if(!this.searchQuery.isEmpty())
		 {
			 Query query=parser.parse(searchQuery);
			 TopDocs results=searcher.search(query, 100);
			 ScoreDoc[] hits=results.scoreDocs;
			 for(ScoreDoc doc:hits)
			 {
				LuceneResult lucres=new LuceneResult();
				lucres.luceneScore=doc.score;
				Document document=searcher.doc(doc.doc);
				lucres.title=document.get("title");
				lucres.fileURL=document.get("path");
				lucres.postID=getPostID(lucres.fileURL);
				this.LEntryList.add(lucres);
			 }
		 }
		}catch(Exception exc){
			exc.printStackTrace();
		}
		
		//returning the results
		return this.LEntryList;		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}
