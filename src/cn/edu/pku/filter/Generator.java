package cn.edu.pku.filter;

import java.io.IOException;
import java.util.ArrayList;
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

public class Generator {

	public static ArrayList<String> dictRes
			= new ArrayList<String>();
	public static HashMap<String, HashMap<String, Double>> dictOcc
			= new HashMap<String, HashMap<String, Double>>();
	public static HashMap<String, Integer> dictPos
			= new HashMap<String, Integer>();
	
	public static void init() {
		dictRes.clear();
		dictOcc.clear();
		dictPos.clear();
	}
	
	public static void clear() {
		dictOcc.clear();
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
				for (int i = 1; i < tokens.length; i ++) {
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
			double threshold) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeperator);
				String key = tokens[0].substring(
						0, tokens[0].indexOf("→"));
				String subKey = tokens[0].substring(
						tokens[0].indexOf("→") + 1, tokens[0].length() - 1);
				tokens[5] = tokens[5].substring(
						tokens[5].indexOf("=") + 1);
				double score = Double.parseDouble(tokens[5]);
				if (score < threshold) {
					continue;
				}
				
				HashMap<String, Double> temp = new HashMap<String, Double> ();
				if (dictOcc.containsKey(key)) {
					temp = dictOcc.get(key);
					dictOcc.remove(key);
				}
				if (temp.containsKey(subKey)) {
					double subScore = temp.get(subKey);
					subScore = Math.sqrt(subScore * score);
					temp.remove(subKey);
					temp.put(subKey, subScore);
				} else {
					temp.put(subKey, score);
				}
				dictOcc.put(key, temp);
				
				temp = new HashMap<String, Double> ();
				if (dictOcc.containsKey(subKey)) {
					temp = dictOcc.get(subKey);
					dictOcc.remove(subKey);
				}
				if (temp.containsKey(key)) {
					double subScore = temp.get(key);
					subScore = Math.sqrt(subScore * score);
					temp.remove(key);
					temp.put(key, subScore);
				} else {
					temp.put(key, score);
				}
				dictOcc.put(subKey, temp);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void getCandidate(
			String[] seed, int num, double countThres) {
		for (int i = 0; i < seed.length; i ++) {
			dictRes.add(seed[i]);
		}
		
		for (int i = 0; i < num; i ++) {
			//对所有的种子词统计最相近的词、分数和种子词的贡献次数
			//candidate, score + seed
			HashMap <String, String> tempDict
				= new HashMap <String, String> ();
			for (int j = 0; j < dictRes.size(); j ++) {
//				System.out.println(dictRes.get(j));
				//选出种子词最相近的词
				HashMap <String, Double> subDict
					= dictOcc.get(dictRes.get(j));
				//对于这样的每一个词
				for (String key : subDict.keySet()) {
					//词性标注过滤
					if (!dictPos.containsKey(key)) {
						continue;
					}
					if (dictRes.contains(key)) {
						continue;
					}
					
					//如果candidate中不包含
					if (!tempDict.containsKey(key)) {
						//记录来源种子词汇和贡献分数
						tempDict.put(key,
							String.valueOf(subDict.get(key))
							+ " " + dictRes.get(j)
							+ " " + String.valueOf(subDict.get(key)));
					} else {
						String value = tempDict.get(key);
						double score = Double.parseDouble(
								value.substring(0, value.indexOf(" "))) * 0.5
								+ subDict.get(key);
						value = String.valueOf(score)
								+ value.substring(value.indexOf(" "))
								+ " " + dictRes.get(j)
								+ " " + String.valueOf(subDict.get(key));
						tempDict.remove(key);
						tempDict.put(key, value);
					}
				}
			}
			
//			Map.Entry[] sets = getSortedHashMapByValue(tempDict, null);
//			for (int j = 0; j < sets.length; j ++) {
//				String newSeed = sets[j].getKey().toString();
//				String newScore = sets[j].getValue().toString();
//				System.out.println(newSeed + " " + newScore);
//			}
			
			//计算平均距离
			HashMap <String, Double> distance
				= new HashMap <String, Double> ();
			for (String key : tempDict.keySet()) {
				String value = tempDict.get(key);
				double score = Double.parseDouble(
						value.substring(0, value.indexOf(" ")));
				double count =
						(double) value.substring(value.indexOf(" "))
						.trim().split(" +").length;
				if (count < dictRes.size() * countThres) {
					continue;
				}
				score = Math.pow(score, 1.0 / count);
				distance.put(key, score);
			}
			
			Map.Entry[] set = getSortedHashMapByValue(distance);
//			System.out.println(set.length);
//			for (int j = 0; j < set.length; j ++) {
//				String newSeed = set[j].getKey().toString();
//				String newScore = set[j].getValue().toString();
//				System.out.print(newSeed + " " + newScore + " ");
//			}
//			System.out.println();
			
			for (int j = 0; j < set.length; j ++) {
				String newSeed = set[j].getKey().toString();
				if (!dictRes.contains(newSeed)) {
					dictRes.add(newSeed);
					break;
				}
			}
			
			for (int j = 0; j < dictRes.size(); j ++) {
				System.out.print(dictRes.get(j) + " ");
			}
			System.out.println();
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
	
	/**
	 * HashMap排序器
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map.Entry[] getSortedHashMapByValue(Map h, String suc)
	{
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() 
		{
			public int compare(Object arg0, Object arg1) 
			{
				String str1 = ((Map.Entry) arg0).getValue().toString();
				double value1 = Double.parseDouble(
						str1.substring(0, str1.indexOf(" ")));
				String str2 = ((Map.Entry) arg1).getValue().toString();
				double value2 = Double.parseDouble(
						str2.substring(0, str2.indexOf(" ")));
				if (value2 < value1) {
					return -1;
				} else if (value2 == value1) {
					return 0;
				} else {
					return 1;
				}
		    }
		});
		return entries;
	}

	public static void main(String [] args) {
		
		String [] seeds = {
//							"struts",
							"java",
//							"spring",
//							"c",
//							"python"
							};
		double threshold = 0.0;
		String outputDir = "../processingDir2/计算机软件";
		double countThres = 0.5;
		int candidateNum = 100;
		
		init();
		loadDictPos(outputDir + "/" + "tokens.pos", " ");
		loadDictOcc(outputDir + "/" + "tokens.occ", " ",
					threshold);
		getCandidate(seeds, candidateNum, countThres);
		clear();
		saveToFile(outputDir + "/" + "tokens.through.occ.sim", " ");
	}
	
}
