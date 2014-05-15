package misc;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class EncodeDecode {
	public static void main(String[] args) throws UnsupportedEncodingException, ParseException{
		String encoded="%0D%0Aimport+java.io.BufferedReader%3B%0D%0Aimport+java.io.InputStreamReader%3B%0D%0Aimport+java.net.HttpURLConnection%3B%0D%0Aimport+java.net.URL%3B%0D%0A%0D%0Apublic+class+GitHubClient+%7B%0D%0A%09%0D%0A%09static+String+developerToken%3D%2258882f411bbee0425d6d3c32f9114f169eb7d974%22%3B%0D%0A%09%0D%0A%09public+static+String+exceuteGitHubCall%28String+callURL%29%0D%0A%09%7B%0D%0A%09%09%2F%2Fcode+for+executing+GitHub+Call%0D%0A%09%09String+response%3Dnew+String%28%29%3B%0D%0A%09%09try%7B%0D%0A%09%09%09%2F%2Fadding+the+token%0D%0A%09%09%09%2F%2FcallURL%3DcallURL%2B%22%2Baccess_token%3D%22%2BdeveloperToken%3B%0D%0A%09%09%09System.out.println%28callURL%29%3B%0D%0A%09%09%09URL+url%3Dnew+URL%28callURL%29%3B%0D%0A%09%09%09HttpURLConnection+httpconn%3D%28HttpURLConnection%29url.openConnection%28%29%3B%0D%0A%09%09%09httpconn.setRequestMethod%28%22GET%22%29%3B%0D%0A%09%09%09if%28httpconn.getResponseCode%28%29%3D%3DHttpURLConnection.HTTP_OK%29%0D%0A%09%09%09%7B%0D%0A%09%09%09%09BufferedReader+breader%3Dnew+BufferedReader%28new+InputStreamReader%28httpconn.getInputStream%28%29%29%29%3B%0D%0A%09%09%09%09String+line%3Dnull%3B%0D%0A%09%09%09%09while%28%28line%3Dbreader.readLine%28%29%29%21%3Dnull%29%0D%0A%09%09%09%09%7B%0D%0A%09%09%09%09%09response%2B%3Dline%2B%22%5Cn%22%3B%0D%0A%09%09%09%09%7D%0D%0A%09%09%09%09System.out.println%28response%29%3B%0D%0A%09%09%09%7Delse%7B%0D%0A%09%09%09%09System.out.println%28httpconn.getResponseMessage%28%29%29%3B%0D%0A%09%09%09%7D%0D%0A%09%09%7Dcatch%28Exception+exc%29%7B%0D%0A%09%09%09exc.printStackTrace%28%29%3B%0D%0A%09%09%7D%0D%0A%09%09return+response%3B%0D%0A%09%7D%0D%0A%7D%0D%0A";
		String code=URLDecoder.decode(encoded, "UTF-8");
		System.out.println(code);
		CompilationUnit cu=JavaParser.parse(new ByteArrayInputStream(code.getBytes()));
	}
}
