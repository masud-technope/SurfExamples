package misc;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DecodeTest {

	public static void main(String[] args){
		String code="package+core%3B%0A%0Aimport+java.io.BufferedReader%3B%0Aimport+java.io.PrintWriter%3B%0Aimport+java.net.Socket%3B%0A%0Aclass+SocketExceptionTest+%7B%0A%0A%09public+static+void+main%28String%5B%5D+args%29+%7B%0A%09%09Socket+socket+%3D+null%3B%0A%09%09PrintWriter+out+%3D+null%3B%0A%09%09BufferedReader+input+%3D+null%3B%0A%0A%09%09try+%7B%0A%09%09%09%2F%2F+creates+a+new+Socket+object+and+names+it+socket.%0A%09%09%09%2F%2F+Establishes+the+socket+connection+between+the+client+%26+server%0A%09%09%09%2F%2F+name+of+the+machine+%26+the+port+number+to+which+we+want+to+connect%0A%09%09%09socket+%3D+new+Socket%28hostName%2C+15432%29%3B%0A%09%09%09if+%28printOutput%29+%7B%0A%09%09%09%09System.out.print%28%22Establishing+connection.%22%29%3B%0A%09%09%09%7D%0A%09%09%09%2F%2F+opens+a+PrintWriter+on+the+socket+input+autoflush+mode%0A%09%09%09out+%3D+new+PrintWriter%28socket.getOutputStream%28%29%2C+true%29%3B%0A%0A%09%09%09%2F%2F+opens+a+BufferedReader+on+the+socket%0A%09%09%09input+%3D+new+BufferedReader%28new+InputStreamReader%28%0A%09%09%09%09%09socket.getInputStream%28%29%29%29%3B%0A%09%09%09if+%28printOutput%29%0A%09%09%09%09System.out.println%28%22%5CnRequesting+output+for+the+%27%22%0A%09%09%09%09%09%09%2B+menuSelection+%2B+%22%27+command+from+%22+%2B+hostName%29%3B%0A%0A%09%09%09%2F%2F+get+the+current+time+%28before+sending+the+request+to+the+server%29%0A%09%09%09startTime+%3D+System.currentTimeMillis%28%29%3B%0A%0A%09%09%09%2F%2F+send+the+command+to+the+server%0A%09%09%09out.println%28Integer.toString%28menuSelection%29%29%3B%0A%09%09%09if+%28printOutput%29%0A%09%09%09%09System.out.println%28%22Sent+output%22%29%3B%0A%0A%09%09%09%2F%2F+read+the+output+from+the+server%0A%09%09%09String+outputString%3B%0A%09%09%09while+%28%28%28outputString+%3D+input.readLine%28%29%29+%21%3D+null%29%0A%09%09%09%09%09%26%26+%28%21outputString.equals%28%22END_MESSAGE%22%29%29%29+%7B%0A%09%09%09%09if+%28printOutput%29%0A%09%09%09%09%09System.out.println%28outputString%29%3B%0A%09%09%09%7D%0A%0A%09%09%09%2F%2F+get+the+current+time+%28after+connecting+to+the+server%29%0A%09%09%09endTime+%3D+System.currentTimeMillis%28%29%3B%0A%09%09%09%2F%2F+endTime+-+startTime+%3D+the+time+it+took+to+get+the+response+from%0A%09%09%09%2F%2F+the+sever%0A%09%09%09totalTime.addAndGet%28endTime+-+startTime%29%3B%0A%0A%09%09%7D+catch+%28UnknownHostException+e%29+%7B%0A%09%09%09%0A%09%09%7D+catch+%28ConnectException+e%29+%7B%0A%09%09%09%0A%09%09%7D+catch+%28SocketException+e%29+%7B%0A%09%09%09%2F%2Fhandle+the+exception%0A%09%09%7D%0A%0A%09%7D%0A%7D%0A";
		try {
			String code1=URLDecoder.decode(code, "UTF-8");
			System.out.println(code1);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
