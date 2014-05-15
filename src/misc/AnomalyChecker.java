package misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

import core.CodeFragment;
import core.ECodeFragment;
import core.StaticData;
import utility.SolutionLoader;

public class AnomalyChecker {

	static String getCode1(String fileName) {
		String code = new String();
		try {
			File f = new File(fileName);
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			CodeFragment cf = (CodeFragment) ois.readObject();
			code = cf.CompleteCode;
		} catch (Exception exc) {
		}
		return code;

	}

	static String getCode2(String fileName) {
		String code = new String();
		try {
			File f = new File(fileName);
			FileInputStream fis = new FileInputStream(f);
			ObjectInputStream ois = new ObjectInputStream(fis);
			ECodeFragment cf = (ECodeFragment) ois.readObject();
			code = cf.CompleteCode;
		} catch (Exception exc) {
		}
		return code;

	}

	public static void main(String[] args) {
		for (int i = 1; i <= 150; i++) {
			try {

				int exceptionID = i;
				int solutionID = SolutionLoader.getSolutionID(exceptionID);
				String orgFileName = StaticData.Surf_Data_Base
						+ "/fragmentsIndex/" + exceptionID + "/" + solutionID
						+ ".ser";
				String heuFileName = StaticData.Surf_Data_Base + "/hIndex/"
						+ exceptionID + "/" + solutionID + ".ser";
				String orgcode = getCode1(orgFileName);
				String heucode = getCode2(heuFileName);
				if (orgcode.contains(heucode)) {
					System.out.println(i + "They are ok");
				} else {
					System.err.println(i+" is not okay");
					//System.out.println(orgcode);
					//System.out.println("========================");
					//System.out.println(heucode);
				}
			} catch (Exception exc) {

			}
		}

	}
}
