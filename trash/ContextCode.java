package trash;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ContextCode {
	public static void main(String[] args){
		try{
			URL url=new URL("here is the URL");
			HttpURLConnection conn=(HttpURLConnection) url.openConnection();
			if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
				DataInputStream ds=new DataInputStream(conn.getInputStream());
				//now do some stuff with that
			}
		}catch(Exception e){
			//handle the exception
		}
	}

}
