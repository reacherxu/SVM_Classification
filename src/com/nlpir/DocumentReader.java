package com.nlpir;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Logger;
/**
 * 
 * @author Reacher
 *
 */
public class DocumentReader {
	private static Logger logger = Logger.getLogger("com.nlpir.DocumentReader"); 
	
	/**
	 * 一个文件代表一行
	 * @param path
	 * @return
	 */
	public static String readFile(String path) {
		Scanner scanner;
		String content = new String();
		try {
			scanner = new Scanner(new File(path));

			while (scanner.hasNextLine())
				content += scanner.nextLine();
			scanner.close();
		} catch (FileNotFoundException e) {
			logger.info("File Not Found ["+path+"]");
		}
		return content;
	}

	public static void main(String[] args) {
		System.out.println(DocumentReader.readFile("sentence"));
	}
}
