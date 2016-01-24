package AdminUi;

import java.io.IOException;

import svmHelper.svm_predict;
import svmHelper.svm_scale;
import svmHelper.svm_train;

/**
 * ��̨�õ�����ģ�ͣ�ֱ�����м���
 * @author Administrator
 *
 */
public class UiMain {

	@SuppressWarnings("static-access")
	public static void main(String[] args) throws IOException{
		//scale����
		String[] sarg = {"-l","0","-s","corpus_train/svm.scale","-o","corpus_train/svmscale.train","corpus_train/svm.train"};
		//train����
		String[] arg = {"-t","0","corpus_train/svmscale.train","corpus_train/svm.model"};
		//predict����
		String[] parg = {"corpus_test/svmscale.test","corpus_train/svm.model","corpus_test/result.txt"};

		System.out.println("��ʼ����");
		svm_scale scale = new svm_scale();
		scale.main(sarg);
		System.out.println("���Ž���");

		System.out.println("ѵ����ʼ");
		svm_train.main(arg);
		System.out.println("ѵ������");

		System.out.println("���࿪ʼ");
		svm_predict.main(parg);
		System.out.println("�������");



		/*//scale����
		String[] sarg = {"-l","0","-s","trainfile/svm.scale","-o","trainfile/svmscale.train","trainfile/svm.train"};
		//train����
		String[] arg = {"-t","0","-v","5","trainfile/svmscale.train","trainfile/svm.model"};
		//predict����
		String[] parg = {"testfile/svmscale.test","trainfile/svm.model","testfile/result.txt"};

		System.out.println("��ʼ����");
		svm_scale scale = new svm_scale();
		scale.main(sarg);
		System.out.println("���Ž���");

		System.out.println("ѵ����ʼ");
		svm_train.main(arg);
		System.out.println("ѵ������");

		System.out.println("���࿪ʼ");
		svm_predict.main(parg);
		System.out.println("�������");*/

	}
}
