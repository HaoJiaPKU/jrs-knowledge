package cn.edu.pku.word2vec;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.lang.model.element.VariableElement;

public class Word2VecTest {
	private static final File sportCorpusFile = new File(
			"../proIndustry/merge.tokens.dat");
	public static void main(String[] args) throws IOException {
		//进行分词训练
//		Learn learn = new Learn();
//		learn.learnFile(sportCorpusFile);
//		//这里保存的是JAVA模型
//		learn.saveModel(new File("merge.tokens.pos.model"));
		
		Word2VEC w2v = new Word2VEC();
		w2v.loadJavaModel("../proIndustry/merge.tokens.pos.model");
		
		float[] vector = w2v.getWordVector("java");
		System.out.println(vector.length);
		for (int i = 0; i < vector.length; i ++) {
			System.out.println(vector[i]);
		}
		Set<WordEntry> result2 = w2v.distance("java");
		for(int i = 0; i < 20; i ++) {
			System.out.println(result2.toArray()[i]);
			}
		}
}