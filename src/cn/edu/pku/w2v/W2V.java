package cn.edu.pku.w2v;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import javax.lang.model.element.VariableElement;

public class W2V {
	
	public static void run(
			String corpusFile,
			String modelPath
			) {
		Learn learn = new Learn();
		try {
			learn.learnFile(new File(corpusFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//这里保存的是JAVA模型
		learn.saveModel(new File(modelPath));
	}
	
	public static void main(String[] args) throws IOException {
		
		W2V.run("../proIndustry/计算机软件/tokens.pos.txt",
				"../proIndustry/merge.tokens.pos.model");
		
		Word2Vec w2v = new Word2Vec();
		w2v.loadJavaModel("../proIndustry/merge.tokens.pos.model");

		Set<WordEntry> result2 = w2v.distance("语言");
		for(int i = 0; i < 20; i ++) {
			System.out.println(result2.toArray()[i]);
		}
	}
}