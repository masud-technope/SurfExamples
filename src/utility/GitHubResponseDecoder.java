package utility;

import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import core.CodeFile;

public class GitHubResponseDecoder {

	public static ArrayList<CodeFile> extractResultsFromJSON(String apiResponse) {
		// code for extracting the source file URLs
		ArrayList<CodeFile> codeFiles = new ArrayList<>();
		try {
			JSONParser parser = new JSONParser();
			JSONObject bigObject = (JSONObject) parser.parse(apiResponse);
			JSONArray codeItems = (JSONArray) bigObject.get("items");
			for (Object elem : codeItems) {

				// extracting necessary element
				JSONObject codeItem = (JSONObject) elem;
				String htmlURL = (String) codeItem.get("html_url");
				String rawFileURL = URLMaker.getRawURL(htmlURL);
				double score = (double) codeItem.get("score");

				// creating code file
				CodeFile codefile = new CodeFile();
				codefile.htmlFileURL = htmlURL;
				codefile.rawFileURL = rawFileURL;
				codefile.GitHubScore = score;
				// adding to the collection
				codeFiles.add(codefile);
			}
		} catch (Exception exc) {
		}
		return codeFiles;
	}
}
