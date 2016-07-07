package trash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import org.eclipse.swt.MessageDialog;
import org.apache.log4j.Log;

public class Recommended {
	
	public static void main(String[] args){
		BufferedReader breader=null;
		try {
	        URL url = new URL(web_service_url);
	        HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
	        httpconn.setRequestMethod("GET");
	        //System.out.println(httpconn.getResponseMessage());
	        if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            breader = new BufferedReader(new InputStreamReader(
	                    httpconn.getInputStream())); 
	                String line = null;
	                while ((line = breader.readLine()) != null) {
	                    //more code goes here ...
	                }
	        }
	    } catch (MalformedURLException mue) {
	        Log.warn("Invalid URL " + web_service_url, mue);
	        MessageDialog.openError(Display.getDefault().getShells()[0],
	                "Invalid URL " + web_service_url, mue.getMessage());
	    } catch (ProtocolException pe) {
	        Log.warn("Protocol Exception " + web_service_url, pe);
	        MessageDialog.openError(Display.getDefault().getShells()[0],
	                "Invalid Protocol " + web_service_url, pe.getMessage());
	    } catch (IOException ioe) {
	        Log.warn("Failed to access the data " + web_service_url, ioe);
	    } finally{
	    	breader.close();
	    }

	}
	

}
