package misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MotivationExample {

	protected static void sendCommandReceiveResponse(int menuSelection,
			String hostName, boolean printOutput) {

		// every clientThread is passed the host name of the server to connect
		// to
		Socket socket = null;
		PrintWriter out = null;
		BufferedReader input = null;
		try {
			// creates a new Socket object and names it socket.
			socket = new Socket(hostName, 15432);
			// opens a PrintWriter on the socket input auto flush mode
			out = new PrintWriter(socket.getOutputStream(), true);
			// opens a BufferedReader on the socket
			input = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			// send the command to the server
			out.println(Integer.toString(menuSelection));
			// read the output from the server
			String outputString;
			while (((outputString = input.readLine()) != null)
					&& (!outputString.equals("END_MESSAGE"))) {
				// process the output
			}
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + hostName);
			System.exit(1);
		} catch (ConnectException e) {
			System.err.println("Connection refused by host: " + hostName);
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// finally, close the socket and decrement runningThreads
		finally {
			if (printOutput)
				System.out.println("closing");
			try {
				socket.close();
				// runningThreads.decrementAndGet();
				System.out.flush();
			} catch (IOException e) {
				System.out.println("Couldn't close socket");
			}
		}
	}

	public static void main(String[] args) {
		sendCommandReceiveResponse(1, "http://www.oracle.com", true);

	}

}
