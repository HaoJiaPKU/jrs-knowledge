package cn.edu.pku.w2v;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.lang.model.element.VariableElement;

public class Word2VecTest {
	private static final File sportCorpusFile = new File(
			"../proIndustry/计算机软件/tokens.pos.txt");
	public static void main(String[] args) throws IOException {
		//进行分词训练
		Learn learn = new Learn();
		learn.learnFile(sportCorpusFile);
		//这里保存的是JAVA模型
		learn.saveModel(new File("../proIndustry/merge.tokens.pos.model"));
		
		Word2VEC w2v = new Word2VEC();
		w2v.loadJavaModel("../proIndustry/merge.tokens.pos.model");

		Set<WordEntry> result2 = w2v.distance("语言");
		for(int i = 0; i < 20; i ++) {
			System.out.println(result2.toArray()[i]);
		}
	}
}