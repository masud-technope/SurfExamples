package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GitHubClient {
	
	static String developerToken="58882f411bbee0425d6d3c32f9114f169eb7d974";
	
	public static String exceuteGitHubCall(String callURL)
	{
		//code for executing GitHub Call
		String response=new String();
		try{
			//adding the token
			//callURL=callURL+"+access_token="+developerToken;
			//System.out.println(callURL);
			URL url=new URL(callURL);
			HttpURLConnection httpconn=(HttpURLConnection)url.openConnection();
			httpconn.setRequestMethod("GET");
			if(httpconn.getResponseCode()==HttpURLConnection.HTTP_OK)
			{
				BufferedReader breader=new BufferedReader(new InputStreamReader(httpconn.getInputStream()));
				String line=null;
				while((line=breader.readLine())!=null)
				{
					response+=line+"\n";
				}
				//System.out.println(response);
			}else{
				System.out.println(httpconn.getResponseMessage());
				System.out.println("Failed to collect the results from Github");
			}
		}catch(Exception exc){
			exc.printStackTrace();
		}
		return response;
	}
}
