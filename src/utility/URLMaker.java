package utility;

public class URLMaker {
	
	
	//converts a GitHub html url to raw URL
	public static String  getRawURL(String htmlURL){
		//code for getting RAW URL
		String rawFileURL=new String();
		//htmlURL="https://github.com/jquery/jquery/blob/c8c32f1d0583711355c593fb4c84332bfba18254/speed/benchmarker.js";
		try{
			//blob path
			int blobStrIndex=htmlURL.indexOf("blob/");
			
			//extracting Repo info
			int gitIndex=htmlURL.indexOf("github.com");
			int braceIndex=gitIndex+10;
			String repoInfo=htmlURL.substring(braceIndex+1, blobStrIndex-1);
			
			//extracting blob
			int blobIndex=blobStrIndex+5;
			String restPart=htmlURL.substring(blobIndex);
			int nextSlash=restPart.indexOf("/");
			String blob=restPart.substring(0, nextSlash);
			//extracting file path
			String filePath=restPart.substring(nextSlash+1);
			//now form the raw path
			rawFileURL="https://raw2.github.com/"+repoInfo+"/"+blob+"/"+filePath;
			//System.out.println(rawFileURL);
			
		}catch(Exception exc){
		}
		return rawFileURL;
	}

}
