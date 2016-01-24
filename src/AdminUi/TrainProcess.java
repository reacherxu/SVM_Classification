package AdminUi;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import Base.BaseWordCut;
import Helper.TfIdfHelper;

/**
 * ��̨��������ѵ������ֱ�����м���
 * @author Administrator
 *
 */
public class TrainProcess extends BaseWordCut{
	public static int curFileIndex = 1;//���ڶ�ȡ���ļ�
	/**
	 * ����ѵ�����ִʺ��map
	 */
	Map<File, HashMap<String, Integer>> wordsMap = new LinkedHashMap<File, HashMap<String, Integer>>();
	/**
	 * wordsMap��Ӧ��tf-idfƵ��
	 */
	Map<File, HashMap<String, Double>> tfIdfMap = new LinkedHashMap<File, HashMap<String, Double>>();
	/**
	 * ����ѵ�������ɵĴʵ�
	 */
	Map<String, Integer> wordsDict = new HashMap<String,Integer>();

	public static HashMap<String, Integer> classLabel = new HashMap<String, Integer>();

	public TrainProcess() throws IOException{
		classLabel = loadClassFromFile(new File("corpus_train/classLabel.txt"));
	}

	/**
	 * ��ѵ�����ж�ȡ�ļ�
	 * @param path
	 * 	Path   �������ļ���Ŀ¼
	 * 	 --catDir	���Ŀ¼
	 * 		--dir	�ļ�Ŀ¼
	 * @return	�ļ��Ͷ�Ӧ������
	 * @throws Exception
	 */
	private Map<File, String> readFile(String path) throws Exception{
		/* pathΪ����Ŀ¼�ṹ  */
		File baseDir = new File(path);
		File[] catDir = baseDir.listFiles();

		Map<File, String> articles = new LinkedHashMap<File,String>();
		for (File dir : catDir) {
			if(dir.isDirectory()){
				File[] files = dir.listFiles();
				System.out.print(dir.getName()+" ");
				for (File file : files) {
					//					if(FileHelper.getFileExt(file).equals("txt")){
					BufferedReader reader = new BufferedReader(new FileReader(file));
					String temp = null;
					String content = "";
					while((temp = reader.readLine()) != null){
						content += temp;
					}
					articles.put(file, content);
					System.out.print(curFileIndex+" ");
					curFileIndex++;
					reader.close();
					//					}
				}
				System.out.println();
			}
		}
		return articles;
	}

	/**
	 * ͨ���ļ�������� �磺����_1.txt ���ڵ����Ϊ�����Ρ�
	 * @param file ����ѵ�����ļ��Ķ���
	 * @return
	 */
	private int getClassLabel(File file){
		//�ļ���Ŀ¼����������
		String className = file.getParentFile().getName();
		if (classLabel.containsKey(className)) {
			return classLabel.get(className);
		}else{
			return -1;
		}
	}

	/**
	 * ����ѵ�����ִʺ��map
	 * @param path
	 * @throws Exception
	 */
	public void cutWord(String path) throws Exception{
		//ѵ�����ж�ȡȫ���ļ�
		Map<File, String> articles = readFile(path);

		//ѵ�������зִ�
		Iterator<File> iterator = articles.keySet().iterator();
		while(iterator.hasNext()){
			File file = iterator.next();
			String content = articles.get(file);
			HashMap<String, Integer> tempWordMap = doCutWord(content);
			this.wordsMap.put(file, tempWordMap);
		}
	}

	/**
	 * �����ֵ䣬index item,�磺0 �й�
	 * @param file
	 */
	public void makeDictionary(File outFile){
		try {
			int index = 1;
			PrintWriter writer = new PrintWriter(outFile);
			
			Iterator<File> classIterator = wordsMap.keySet().iterator();
			while (classIterator.hasNext()) {
				File file = classIterator.next();
				//����=>��Ƶ
				HashMap<String, Integer> itemMap = wordsMap.get(file);
				Iterator<String> itemIterator = itemMap.keySet().iterator();
				while(itemIterator.hasNext()){
					String itemName = itemIterator.next();
					if(!wordsDict.containsKey(itemName)){
						wordsDict.put(itemName, index);
						writer.println(index+ " " +itemName);
						index ++ ;
					}
				}
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ת����libsvm�����ϸ�ʽ
	 */
	public void convertToSvmFormat(File outFile){
		try {
			TfIdfHelper tfIdfHelper = new TfIdfHelper(wordsMap);
			this.tfIdfMap = tfIdfHelper.calculate();
			PrintWriter writer = new PrintWriter(outFile);
			Iterator<File> classIterator = tfIdfMap.keySet().iterator();
			while (classIterator.hasNext()) {
				File file = classIterator.next();
				//����=>��Ƶ
				HashMap<String, Double> itemMap = tfIdfMap.get(file);
				Iterator<String> itemIterator = itemMap.keySet().iterator();
				writer.print(getClassLabel(file) + " ");
				System.out.print(getClassLabel(file) + " ");
				while(itemIterator.hasNext()){
					String itemName = itemIterator.next();
					int index = -1;
					if(wordsDict.containsKey(itemName)){
						index = wordsDict.get(itemName);
					}	
					System.out.print(index + ":" + itemMap.get(itemName) + " ");
					writer.print(index + ":" + itemMap.get(itemName) + " ");
				}
				System.out.println();
				writer.println();
				writer.flush();
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		Date begin = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(dateFormat.format(begin));

		TrainProcess model;
		try {
			model = new TrainProcess();
			//			model.cutWord("article/");
			model.cutWord("D:\\TrainningSet\\");
			model.cutWord("D:\\TestingSet\\");
			System.out.println("���ڿ�ʼ�����ֵ�");
			model.makeDictionary(new File("corpus_train/dictionary.txt"));//�������дʵ��ֵ�
			System.out.println("�ֵ��������");
			System.out.println("��ʼת����libsvm����");
			model.convertToSvmFormat(new File("corpus_train/svm.train"));//������ת����libsvm��ģʽ
			System.out.println("ת�����");
			Date end = new Date();
			System.out.println(dateFormat.format(begin));
			System.out.println(dateFormat.format(end));
			int min = (int)(end.getTime() - begin.getTime())/(1000*60);
			System.out.println("��ʱ��"+min);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}





}

