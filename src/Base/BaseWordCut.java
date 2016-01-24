package Base;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.nlpir.DocumentReader;
import com.nlpir.WordSeparation;

public class BaseWordCut {

	public WordSeparation ws;

	public BaseWordCut() {
		init();
	}

	private void init() {
		ws = new WordSeparation();
	}

	/**
	 * 处理分词，生成item:value的hashmap
	 * @param content
	 * @return
	 */
	public HashMap<String, Integer> doCutWord(String content) {
		HashMap<String, Integer> resultMap = new HashMap<String, Integer>();
		try {

			String nativeStr1 = ws.separate(content);
//			ws.close();
			// System.out.println(nativeStr1);

			String[] arr = nativeStr1.split(" ");
			for (String temp : arr) {
				String[] wt = temp.split("/");
				if (wt.length != 2) {
					continue;
				}
				String item = wt[0];
				String ext = wt[1];
				//TODO　看看其他词性有没有影响
				if (ext.startsWith("n") || ext.startsWith("un")
						|| ext.startsWith("v") || ext.startsWith("a")) {
					addWord(resultMap, item.trim());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		return resultMap;
	}

	/**
	 * 向resultMap 中添加word
	 * @param resultMap
	 * @param word
	 */
	private void addWord(HashMap<String, Integer> resultMap, String word) {
		if (resultMap.containsKey(word)) {// 如果已经存在该项，则词频+1
			resultMap.put(word, resultMap.get(word) + 1);
		} else {// 如果不存在该项，则添加该项，并把词频置为1
			resultMap.put(word, 1);
		}
	}

	/**
	 * 从文件中加载分类的相关信息，返回格式： 类别名=>类别号
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, Integer> loadClassFromFile(File file)
			throws IOException {
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String temp = null;
		while ((temp = reader.readLine()) != null) {
			String[] str = temp.split(" ");
			result.put(str[1], Integer.parseInt(str[0]));
			System.out.println(str[1] + " " + str[0]);
		}
		reader.close();
		return result;
	}

	public static void main(String[] args) {
		BaseWordCut inst = new BaseWordCut();
		String sInput = DocumentReader.readFile("sentence");
		System.out.println(inst.doCutWord(sInput));
	}

}
