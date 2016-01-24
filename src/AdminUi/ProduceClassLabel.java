package AdminUi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ��������������ֱ�����м���
 * @author Administrator
 *
 */
public class ProduceClassLabel {

	public static void makeClassLabel(File baseDir, File outClassFile) throws IOException{
		PrintWriter writer = new PrintWriter(new FileWriter(outClassFile));
		File[] catDir = baseDir.listFiles();
		int index = 1;
		for (File dir : catDir) {
			if(dir.isDirectory()){
				writer.println(index + " " + dir.getName());
				System.out.println(index + " " + dir.getName());
				index++;
			}
		}
		writer.flush();
		writer.close();
	}
	
	public static void main(String[] args){
		try {
			File baseDir = new File("D:\\TrainningSet");
			File outClassFile = new File("corpus_train/classLabel.txt");
			makeClassLabel(baseDir, outClassFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
