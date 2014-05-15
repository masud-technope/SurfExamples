package ohloh;

import java.io.File;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.sun.java.swing.plaf.windows.WindowsTreeUI.CollapsedIcon;

import core.CodeFragment;
import core.StaticData;

public class OhlohCodeExtractor {
	
	String searchQuery;
	ArrayList<CodeFragment> Fragments;
	public OhlohCodeExtractor(String searchQuery)
	{
		//default constructor
		this.searchQuery=searchQuery;
		this.Fragments=new ArrayList<>();
	}
	
	public ArrayList<CodeFragment> collectOhlohCodeFragments()
	{
		//code for collecting code fragments
		try{
			ArrayList<OhlohThread> tcollection=new ArrayList<>();
			String callURL="http://code.ohloh.net/search?fl=Java&fe=java&s="+URLEncoder.encode(searchQuery,"UTF-8")+"&p=";
			for(int i=1;i<5;i++){
				String mycallURL=callURL+i;
				OhlohThread t=new OhlohThread(mycallURL);
				tcollection.add(t);
				t.start();
				
			}
			
			ArrayList<OhlohThread> temp=new ArrayList<>();
			temp.addAll(tcollection);
			
			while (true) {
				for (OhlohThread t : tcollection) {
					if (!t.isAlive()) {
						ArrayList<CodeFragment> examples = t.codeExamples;
						this.Fragments.addAll(examples);
						temp.remove(t);
					}
				}
				tcollection=temp;
				
				if (temp.isEmpty())
					break;
			}
			//now items are collected.
		    System.out.println("Code fragments collected:"+this.Fragments.size());	
		    saveTheCodes();
		
		}catch(Exception exc){
			//handle the exception
		}
		return this.Fragments;
	}
	
	protected void saveTheCodes() {
		String folder = StaticData.Surf_Data_Base + "/codes";
		int count = 1;
		for (CodeFragment codeFragment : this.Fragments) {
			try {
				String code = codeFragment.CompleteCode;
				FileWriter fwriter = new FileWriter(new File(folder + "/"
						+ count + ".txt"));
				fwriter.write(code);
				fwriter.close();
				count++;
			} catch (Exception exc) {
				// handle the code
			}
		}
	}

	public static void main(String args[]) {
		String searchQuery="com.gargoylesoftware.htmlunit.ScriptException";
		OhlohCodeExtractor extractor=new OhlohCodeExtractor(searchQuery);
		extractor.collectOhlohCodeFragments();
	}
}
