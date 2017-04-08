package cn.edu.pku.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class PatternOcc {

	public static Set<String> dictRes
			= new TreeSet<String>();
	public static HashMap<String, Double> dictOccScore
			= new HashMap<String, Double>();
	public static HashMap<String, Integer> dictOccTf
			= new HashMap<String, Integer>();
	public static HashMap<String, Integer> dictPos
			= new HashMap<String, Integer>();
	
	public static void init() {
		dictRes.clear();
		dictOccScore.clear();
		dictOccTf.clear();
		dictPos.clear();
	}
	
	public static void clear() {
		dictOccScore.clear();
		dictOccTf.clear();
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
	
	//加载共线关系的词典
	public static void loadDictOcc(String inputPath, String inputSeperator,
			String str, int thresTf, double thresScore) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeperator);
				String key = tokens[0].substring(
						0, tokens[0].indexOf("→"));
				if (!key.equals(str)) {
					continue;
				}
				String value = tokens[0].substring(
						tokens[0].indexOf("→") + 1, tokens[0].length() - 1);
				//score=
				tokens[5] = tokens[5].substring(6);
				//tf=
				tokens[1] = tokens[1].substring(3);
				int tf = Integer.parseInt(tokens[1]);
				double score = Double.parseDouble(tokens[5]);
				if (score >= thresScore && tf >= thresTf) {
					dictOccScore.put(value, score);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void getCandidate(int num) {
		Map.Entry[] set = getSortedHashMapByValue(dictOccScore);
		for (int i = 0; i < set.length && i < num; i ++) {
			String key = set[i].getKey().toString();
			if (dictPos.containsKey(key)) {
				if (!dictRes.contains(key)) {
					dictRes.add(key);
				}
			}
		}
	}
	
	public static void saveToFile(String outputPath, String outputSeperator) {
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			for (Iterator iterator = dictRes.iterator(); iterator.hasNext();) {  
	            String key = (String) iterator.next();
	            fo.t3.write(key);
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fo.closeOutput();
	}
	
	/**
	 * HashMap排序器
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map.Entry[] getSortedHashMapByValue(Map h)
	{
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() 
		{
			public int compare(Object arg0, Object arg1) 
			{
		        Object value1 = ((Map.Entry) arg0).getValue();
		        Object value2 = ((Map.Entry) arg1).getValue();
		        return ((Comparable) value2).compareTo(value1);
		    }
		});
		return entries;
	}
	
	public static void calculate(
			String outputDir,
			String [] tokens,
			int [] thresTf,
			double [] thresScore,
			int candidateNum) {
		init();
		for (int i = 0; i < tokens.length; i ++) {
			loadDictPos(outputDir + "/" + "tokens.pos", " ");
			loadDictOcc(outputDir + "/" + "tokens.occ", " ",
					tokens[i], thresTf[i], thresScore[i]);
			getCandidate(candidateNum);
			clear();
		}
		saveToFile(outputDir + "/" + "tokens.pos.through.occ", " ");
	}

	public static void main(String [] args) {
		
		String [] tokens = {
							"熟悉",
							"熟练",
							"精通",
							"了解",
							"掌握"
							};
		int [] thresTf = {
				0, 0, 0, 0, 0,
							};
		double [] thresScore = {
				0, 0, 0, 0, 0,
							8.5,
							2.5,
							3.5,
							8.5,
							8.5
							};
		calculate(FilterConf.ConceptPath, tokens, thresTf, thresScore, 500);
	}
	
}
