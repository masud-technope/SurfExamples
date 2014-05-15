package utility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import core.CodeFile;

public class CodeDownloader {

	ArrayList<CodeFile> CodeFiles;

	public CodeDownloader(ArrayList<CodeFile> codeFiles) {
		this.CodeFiles = codeFiles;
	}

	protected String getRawContent(String codeFileURL) {
		// code for downloading the content
		String code = new String();
		try {
			URL u = new URL(codeFileURL);
			HttpURLConnection connection = (HttpURLConnection) u
					.openConnection();
			// connection.setConnectTimeout(2000); // setting connect time out
			// connection.setReadTimeout(5000); // setting read time out
			connection.addRequestProperty("Content-Type",
					"text/html;charset=utf-8");
			connection.setRequestMethod("GET");
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String line = null;
				while ((line = br.readLine()) != null) {
					code += line + "\n";
				}
			}
		} catch (Exception exc) {
			// handle the code
		}
		return code;

	}

	public ArrayList<CodeFile> downloadCodeContents() {
		// code for downloading the codes
		try {
			for (CodeFile codefile : this.CodeFiles) {
				String rawContent = getRawContent(codefile.rawFileURL);
				codefile.CompleteCode = rawContent;
			}
		} catch (Exception exc) {
		}
		return this.CodeFiles;
	}

}
