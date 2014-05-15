package ohloh;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import core.CodeFragment;

public class OhlohThread extends Thread {

	public ArrayList<CodeFragment> codeExamples;
	String callURL;

	public OhlohThread(String callURL) {
		// default constructor
		this.callURL = callURL;
		this.codeExamples = new ArrayList<>();
	}

	protected String downloadContent() {
		// code for downloading the content
		String content = new String();
		try {
			URL u = new URL(this.callURL);
			HttpURLConnection connection = (HttpURLConnection) u
					.openConnection();
			connection.setConnectTimeout(2000); // setting connect time out
			connection.setReadTimeout(5000); // setting read time out
			connection.addRequestProperty("Content-Type",
					"text/html;charset=utf-8");
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String line = null;
				while ((line = br.readLine()) != null) {
					content += line + "\n";
				}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return content;
	}
	
	
	public void run() {
		// code for getting the code fragments
		try {
			String content=downloadContent();
			Document document =Jsoup.parse(content);
			// System.out.println(document.html());
			Elements results = document.select("div.snippet");
			System.out.println("Item returned:" + results.size());
			for (Element element : results) {
				String code = new String();
				Elements pres = element.select("pre");
				for (Element pre : pres) {
					code += pre.text() + "\n";
				}
				// adding code fragment
				CodeFragment codeFragment = new CodeFragment();
				codeFragment.CompleteCode = code;
				this.codeExamples.add(codeFragment);
			}
		} catch (Exception exc) {
			// handle the exception
			exc.printStackTrace();
		}
	}

}
