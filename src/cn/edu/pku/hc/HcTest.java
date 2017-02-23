package cn.edu.pku.hc;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import cn.edu.pku.w2v.Learn;
import cn.edu.pku.w2v.ModelToFile;
import cn.edu.pku.w2v.Word2VEC;
import cn.edu.pku.w2v.WordEntry;

public class HcTest {

	private static final File corpusFile = new File(
			"../proIndustry/互联网|电子商务/tokens.txt");
//			"../proIndustry/merge.tokens.dat");
	
	public static void main(String[] args) throws IOException {
		//进行分词训练
		Learn learn = new Learn();
		learn.learnFile(corpusFile);
		//这里保存的是JAVA模型
		learn.saveModel(new File("../proIndustry/w2v.model"));
		
		Word2VEC w2v = new Word2VEC();
		w2v.loadJavaModel("../proIndustry/w2v.model");

		Set<WordEntry> result2 = w2v.distance("java");
		for(int i = 0; i < 20; i ++) {
			System.out.println(result2.toArray()[i]);
		}
		
		ModelToFile.init();
		ModelToFile.loadDictPos("../proIndustry/merge.tokens.pos.dat", " ");
		ModelToFile.saveToFile("../proIndustry/w2v.model",
				"../proIndustry/vec.txt", "	");
		ModelToFile.clear();
		
		HierarchicalClustering hierarchicalClustering
	    	= new HierarchicalClustering("../proIndustry/vec.txt", "	",
		    		"../proIndustry/hclusterDis.txt");  
	    hierarchicalClustering.saveCluster(
	    		"../proIndustry/hcluster.txt",
	    		"../proIndustry/hclusterSeq.txt",
	    		"../proIndustry/hclusterNonSeq.txt",
	    		hierarchicalClustering.hcluster(), 0);  
	}
}
