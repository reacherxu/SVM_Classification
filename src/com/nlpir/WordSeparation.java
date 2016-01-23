package com.nlpir;

import java.util.logging.Logger;

import com.sun.jna.Native;

public class WordSeparation {

	CLibrary instance;
	static Logger logger = Logger.getLogger("com.nlpir.wordseperation");
	
	/**
	 * ��ʼ��
	 */
	public WordSeparation() {
		instance = (CLibrary)Native.loadLibrary(
				System.getProperty("user.dir")+"\\source\\NLPIR", CLibrary.class);
		int init_flag = instance.NLPIR_Init("", 1, "0");
		String resultString = null;
		if (0 == init_flag) {
			resultString = instance.NLPIR_GetLastErrorMsg();
			System.err.println("��ʼ��ʧ�ܣ�\n"+resultString);
			return;
		}
		
		//���ô��Ա�ע��(0 ������������ע����1 ������һ����ע����2 ���������ע����3 ����һ����ע��)
		instance.NLPIR_SetPOSmap(2);
		String userdict = "userdict.txt";
		int nCount = instance.NLPIR_ImportUserDict(userdict);
		logger.info("�û��ʵ���Ŀ" + nCount);
	}

	public void close() {
		instance.NLPIR_Exit();
	}

	public String separate(String line) {
		String resultString = instance.NLPIR_ParagraphProcess(line, 1);
//		System.out.println("�ִʽ��Ϊ��" + resultString);

		return resultString;
	}

	public static void main(String[] args) throws Exception {

		String sInput = "The life sentence imposed at the time sparked angry street protests.";

		WordSeparation ws = new WordSeparation();
		ws.separate(sInput);

		sInput = "������12��4����Ϣ��2009��10��21��,����ʡ������ί�յ��ٱ���,�ٱ��Ը����Ϊ��������ǿ�顢��������,������ί����ί���������Ȳ���������ǿ�顢�������ҵȡ��Դ�,������ί�߶�����,��ɸ����й�������������������,�����������չ�����顣���������ڼ�,�����ٱ����Ϲٺ�����ͨ��������(Ů)�����й������̾�֧���ṩ����ٱ�,�ٱ�����Ȳ���������ǿ�顢�������ҡ�11��19��,�����������Ϲٺ�����ܼ���ר��,�ٴ�ʵ���ٱ�����Ȳ���������ǿ�顢��������,��������㷺��ע���Դ�����ʡ����ί��ʡ�������߶����ӡ�����,����й��쵼ר�̸�������ȡ�������������Ϊ��ǿ�԰����Ķ����ָ��,ʡ�йز���Ѹ�ٳ���������,�����¶��졢ָ���������鹤��,��������ϱ��йز��š�������ǰһ�ε���֤��,�ٱ���ʵ������,�Ϲٺ�����Ϊ�������̷�����243��,�����ܸ��ݺ�����ݡ��������Ϸ����йع涨,�����й���������11��27������������顣�Ϲٺ�������2009��12��1�յ���,12��2�ո����к������˴�ί��������ֹͣ������ʸ�,�����й����ֶ���������¾���,����ͬ�������������м��Ӿ�ס������鹤�����ڽ����С�";
		ws.separate(sInput);

		ws.close();

	}
}
