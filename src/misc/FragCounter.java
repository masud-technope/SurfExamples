package misc;
import java.io.File;
import core.StaticData;

public class FragCounter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fragments = StaticData.Surf_Data_Base + "/fragments";
		File f = new File(fragments);
		int total_item=0;
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 1; i <= 150; i++) {
				String subfolder = fragments + "/" + i;
				File f2 = new File(subfolder);
				if (f2.exists()){
					if (f2.isDirectory()) {
						System.out.println("Excep:"+i + " fragments:" + f2.list().length);
						total_item++;
					}
				}
			}
			System.out.println("Total items:"+total_item);
		}
	}
}
