package cn.edu.pku.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import cn.edu.pku.filter.Statistic;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.w2v.Word2Vec;

public class FeatureProcessor {
	
	public static HashMap<String, Integer> posDict
		= new HashMap<String, Integer> ();
	
	public static void init() {
		posDict.clear();
	}
	
	public static void clear() {
		posDict.clear();
	}
	
	//加载词性标注的词典
	public static void loadPosDict(
			String inputPosPath,
			String inputHypPath,
			String inputSeperator) {
		FileInput fi = new FileInput(inputPosPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeperator);
				if (tokens.length <= 1) {
					continue;
				}
				posDict.put(tokens[0], Integer.parseInt(tokens[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
		
		fi = new FileInput(inputHypPath);
		line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeperator);
				if (tokens.length <= 1) {
					continue;
				}
				posDict.put(tokens[0], Integer.parseInt(tokens[1]));		
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
		Word2Vec w2v = new Word2Vec();
		try {
			w2v.loadJavaModel(modelPath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			for (String word : posDict.keySet()) {
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
	
	public static void saveAllToFile(String modelPath,
			String outputPath, String outputSeperator) {
		FileOutput fo = new FileOutput(outputPath);
		Word2Vec w2v = new Word2Vec();
		try {
			w2v.loadJavaModel(modelPath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			int counter = 0;
			HashMap<String, float[]> dict = w2v.getWordMap();
			for (String word : dict.keySet()) {
				float[] vec = w2v.getWordVector(word);
				if (vec != null) {
//					fo.t3.write(word);
					
					for (int i = 0; i < vec.length - 1; i ++) {
						fo.t3.write(String.valueOf(vec[i]) + outputSeperator);
					}
					fo.t3.write(String.valueOf(vec[vec.length - 1]));
					
					fo.t3.newLine();
					counter ++;
				}
			}
			System.out.println(counter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fo.closeOutput();
	}
	
	public static void removeDuplicateData(String inputPath, String outputPath) {
		FileInput fi = new FileInput(inputPath);
		FileOutput fo = new FileOutput(outputPath);
		HashSet<String> dataDict = new HashSet<String> ();
		String line = new String ();
		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				line = line.trim();
				int firstSpace = line.indexOf(" ");
				if (firstSpace > 0 && line.length() >= firstSpace + 1
						&& line.substring(firstSpace + 1).length() > 0
						) {
					if (!dataDict.contains(line.substring(firstSpace + 1))) {
						dataDict.add(line.substring(firstSpace + 1));
						fo.t3.write(line);
						fo.t3.newLine();
						counter ++;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(counter + " pieces of data");
		fo.closeOutput();
		fi.closeInput();
	}
	
	public static void removeLongTailWord(
			String inputPath, String outputPath,
			boolean idSave) {
		FileInput fi = new FileInput(inputPath);
		FileOutput fo = new FileOutput(outputPath);

		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				line = line.trim();
				String [] tokens = line.split(" ");
				if (tokens.length < 1) {
					continue;
				}
				String content = new String();
				boolean firstWord = true;
				if (idSave) {
					content = tokens[0];
					firstWord = false;
				}
				for (int i = 1; i < tokens.length; i ++) {
					if (tokens[i] == null || tokens[i].length() < 1) {
						continue;
					}
					if (Statistic.dict.containsKey(tokens[i])) {
						if (firstWord) {
							content += tokens[i];
							firstWord = false;
						} else {
							content += " " + tokens[i];
						}
					}
				}
				if (content.indexOf(" ") != -1) {
					fo.t3.write(content);
					fo.t3.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
		fi.closeInput();
	}

	
}
