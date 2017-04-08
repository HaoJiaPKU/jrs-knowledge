package cn.edu.pku.w2v;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import javax.lang.model.element.VariableElement;

import cn.edu.pku.pipeline.ConceptProcessor;

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
		
//		W2V.run("../java-apps/quansongci.txt",
//				"../java-apps/quansongci.model");
		
//		ConceptProcessor.init();
//		ConceptProcessor.saveAllToFile(
//				"../java-apps/quansongci.model",
//				"../java-apps/quansongci.tsv",
//				"	");
//		ConceptProcessor.clear();
		
		Word2Vec w2v = new Word2Vec();
		w2v.loadJavaModel("../proConcept/互联网or电子商务/6.w2v.model");
		
		ArrayList<String> a = new ArrayList<String>();
		a.add("java");
		a.add("数据库");
		a.add("框架");
		Set<WordEntry> result2 = w2v.distance(a);
		for(int i = 0; i < 20; i ++) {
			System.out.println(result2.toArray()[i]);
		}
	}
}