package Action;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import svmHelper.svm_predict;

public class Classfy {

	/**
	 * ���÷���Ĺ����ӿ�
	 * @return �������б�
	 * @throws IOException
	 */
	public static ArrayList<Double> run() throws IOException{
		String[] parg = {"testfile/svmscale.test","trainfile/svm.model","testfile/result.txt"};
		svm_predict.main(parg);
		ArrayList<Double> result = new ArrayList<Double>();
		File file = new File("testfile/result.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String temp = null;
		while((temp = reader.readLine()) != null){
//			System.out.println(Double.parseDouble(temp));
			result.add(Double.parseDouble(temp));
		}
		reader.close();
		return result;
	}
	
	/**
	 * ����������Ϣ
	 */
	private static HashMap<Double, String> classMap = new HashMap<Double, String>();
	private static void loadClassFromFile() throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File("trainfile/classLabel.txt")));
		String temp = null;
		while((temp = reader.readLine()) != null){
			String[] str = temp.split(" ");
			classMap.put(Double.parseDouble(str[0]), str[1]);
//			System.out.println(Double.parseDouble(str[0]) + " " + str[1]);
		}
		reader.close();
	}
	
	private static String getClassByLabel(double label){
		if (classMap.containsKey(label)) {
			return classMap.get(label);
		}else{
			System.out.println(label);
			return "����";
		}
	}
	
	public static HashMap<String, ArrayList<File>> run(File[] sourceFiles) throws IOException{
		//������ʼ����
		String[] parg = {"testfile/svmscale.test","trainfile/svm.model","testfile/result.txt"};
		svm_predict.main(parg);
		
		//�Է���������ת��
		loadClassFromFile();
		File file = new File("testfile/result.txt");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		ArrayList<Double> tempResult = new ArrayList<Double>();
		String temp = null;
		while((temp = reader.readLine()) != null){
			double label = Double.parseDouble(temp);
			tempResult.add(label);
		}
		
		if (sourceFiles.length != tempResult.size()) {
			reader.close();
			throw new IOException("Classify-->runfile,������ļ����鳤�Ȳ�ƥ��");
		}
		reader.close();
		
		HashMap<String, ArrayList<File>> result = new HashMap<String, ArrayList<File>>();
		for(int i=0; i<tempResult.size(); i++){
			double label = tempResult.get(i);
			String className = getClassByLabel(label);
			if (!result.containsKey(className)) {
				ArrayList<File> tmpList = new ArrayList<File>();
				tmpList.add(sourceFiles[i]);
				result.put(className, tmpList);
			}else{
				ArrayList<File> tmpList = result.get(className);
				tmpList.add(sourceFiles[i]);
			}
		}
		
		return result;
	}
	
	
	public static void main(String[] args) throws IOException{
		run();
//		loadClassFromFile();
//		run(new File[]{new File("article"),new File("article"),new File("article")});
	}
}
