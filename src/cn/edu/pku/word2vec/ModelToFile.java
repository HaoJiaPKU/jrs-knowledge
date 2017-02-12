package cn.edu.pku.word2vec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class ModelToFile {
	
	public static HashMap<String, Integer> dictPos
		= new HashMap<String, Integer> ();
	
	public static void init() {
		dictPos.clear();
	}
	
	public static void clear() {
		dictPos.clear();
	}
	
	//加载词性标注的词典
	public static void loadDictPos(String inputPath, String inputSeperator) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeperator);
				if (tokens.length <= 1) {
					continue;
				}
				for (int i = 0; i < tokens.length; i ++) {
					int tempCounter = 1;
					if (dictPos.containsKey(tokens[i])) {
						tempCounter = dictPos.get(tokens[i]) + 1;
						dictPos.remove(tokens[i]);
					}
					dictPos.put(tokens[i], tempCounter);
				}			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void saveToFile(String modelPath,
			String outputPath, String outputSeperator) {
		FileOutput fo = new FileOutput(outputPath);
		Word2VEC w2v = new Word2VEC();
		try {
			w2v.loadJavaModel(modelPath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			for (String word : dictPos.keySet()) {
				float[] vec = w2v.getWordVector(word);
				if (vec != null) {
					fo.t3.write(word);
					for (int i = 0; i < vec.length; i ++) {
						fo.t3.write(outputSeperator + String.valueOf(vec[i]));
					}
					fo.t3.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fo.closeOutput();
	}
	
	public static void main(String[] args) {
		init();
		loadDictPos("../proIndustry/merge.tokens.pos.dat", " ");
		saveToFile("../proIndustry/merge.tokens.pos.model",
				"../proIndustry/vec.txt", "	");
		clear();
	}
}
