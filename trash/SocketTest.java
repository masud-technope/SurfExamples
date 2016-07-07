package trash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketTest {
	public static void main(String[] args){
		String hostName="http://www.google.ca";
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader input = null;
	
		// creates a new Socket object and names it socket.
		try {
			socket = new Socket(hostName, 15432);
		// opens a PrintWriter on the socket input auto flush mode
		out = new PrintWriter(socket.getOutputStream(), true);
		// opens a BufferedReader on the socket
		input = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		// send the command to the server
		out.println(Integer.toString(2));
		// read the output from the server
		String outputString;
		while (((outputString = input.readLine()) != null)
				&& (!outputString.equals("END_MESSAGE"))) {
				//process the output
		}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}
