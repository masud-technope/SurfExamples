package trash;

public class MyTest {
	
	
	void unregister() throws IOException {
		Socket s = new Socket(hostname, portno);
		try {
			s.setSoTimeout(60000);
		} catch (SocketException e) {
			// generic exception handler
			e.printStackTrace();
		}
		PrintWriter bw = new PrintWriter(new OutputStreamWriter(
				s.getOutputStream()));
		bw.println("SHUTDOWN " + id);
		bw.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				s.getInputStream()));
		String resp = br.readLine();
		if (resp == null || !resp.startsWith("OK")) {
			// do something
		} else if (resp.startsWith("OK")) {
			String[] respSplit = resp.split(" ");
			String newOffset = respSplit[respSplit.length - 1];
			try {
				offset = Long.parseLong(newOffset);
			} catch (NumberFormatException nfe) {
				// generic exception handler
				nfe.printStackTrace();
			}
		}
		s.close();
	}
	
	 Socket socket = null;
		PrintWriter out = null;
		BufferedReader input = null;
	try {
		// creates a new Socket object and names it socket.
		Socket socket = new Socket(hostName, 15432);
		// opens a PrintWriter on the socket input auto flush mode
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		// opens a BufferedReader on the socket
		BufferedReader input = new BufferedReader(new InputStreamReader(
				socket.getInputStream()));
		// send the command to the server
		out.println(Integer.toString(menuSelection));
		// read the output from the server
		String outputString;
		while (((outputString = input.readLine()) != null)
				&& (!outputString.equals("END_MESSAGE"))) {
				//process the output
		}
	}catch (UnknownHostException e) {
		System.err.println("Unknown host: " + hostName);
		System.exit(1);
	} catch (ConnectException e) {
		System.err.println("Connection refused by host: " + hostName);
		System.exit(1);
	} catch (IOException e) {
		e.printStackTrace();
	}
	// finally, close the socket and release resources
	finally {
		//releasing resources
		try {
			socket.close();
			System.out.flush();
		} catch (IOException e) {
			System.out.println("Couldn't close socket");
		}
	}
	

}
