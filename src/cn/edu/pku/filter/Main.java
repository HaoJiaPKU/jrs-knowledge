package cn.edu.pku.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class Main {

	public static ArrayList<HashMap<String, Double>> docs
			= new ArrayList<HashMap<String, Double>>();
	public static HashMap<String, Double> dict
			= new HashMap<String, Double>();
	public static HashMap<String, Integer> dictCounter
			= new HashMap<String, Integer>();
	public static final double Threshold = 0.0;
	public static final int ResultLimit = 10;
	
	public static void init() {
		clear();
	}
	
	//计算一项指标
	public static void calculate(String inputPath) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
//		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(" +");
//				System.out.println(++ counter);
				HashMap<String, Double> docDict = new HashMap<String, Double>();
				for (int i = 0; i < tokens.length; i = i + 2) {
					docDict.put(tokens[i], Double.parseDouble(tokens[i + 1]));
				}
				docs.add((HashMap<String, Double>) docDict.clone());				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	//计算多项指标合并后的值
	public static void calculate(String inputPath1, String inputPath2) {
		FileInput fi = new FileInput(inputPath1);
		String line = new String ();
//		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(" +");
//				System.out.println(++ counter);
				HashMap<String, Double> docDict = new HashMap<String, Double>();
				for (int i = 0; i < tokens.length; i = i + 2) {
					docDict.put(tokens[i], Double.parseDouble(tokens[i + 1]));
				}
				docs.add((HashMap<String, Double>) docDict.clone());				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
		
		fi = new FileInput(inputPath2);
		line = new String ();
		int docsIndex = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(" +");
				
				HashMap<String, Double> docDict = 
						(HashMap<String, Double>) docs.get(docsIndex).clone();
				for (int i = 0; i < tokens.length; i = i + 2) {
					if (docDict.containsKey(tokens[i])) {
						Double t = docDict.get(tokens[i]);
						docDict.remove(tokens[i]);
						docDict.put(tokens[i], t * Double.parseDouble(tokens[i + 1]));
					}
				}
				docs.set(docsIndex, ((HashMap<String, Double>) docDict.clone()));
				docsIndex ++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	//将结果输出到文件
	public static void saveToFile(String outputPath) {
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			for (int i = 0; i < docs.size(); i ++) {				
				Map.Entry [] set = getSortedHashMapByValue((Map<String, Double>)docs.get(i).clone());
				for(int j = 0; j < set.length; j ++) {
					if (j > ResultLimit) {
						break;
					}
					String key = set[j].getKey().toString();
					double value = Double.parseDouble(set[j].getValue().toString());
					if (value >= Threshold) {
						fo.t3.write(key
								+ " " + value + " ");
					}
				}
				fo.t3.newLine();;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	//将结果输出到html
	public static void saveToHtml(String outputPath) {
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			for (int i = 0; i < docs.size(); i ++) {				
				Map.Entry [] set = getSortedHashMapByValue((Map<String, Double>)docs.get(i).clone());
				for(int j = 0; j < set.length; j ++) {
					if (j > ResultLimit) {
						break;
					}
					String key = set[j].getKey().toString();
					double value = Double.parseDouble(set[j].getValue().toString());
					if (value >= Threshold) {
						fo.t3.write("<p>"
							+ key + " " + value
							+ "</p>");
					}
				}
				fo.t3.write("<br/>");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	public static void clear() {
		docs.clear();
		dict.clear();
		dictCounter.clear();
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
	
	//以指标平均值统计
	public static void statistic() {
		for (int i = 0; i < docs.size(); i ++) {
			Map.Entry [] set = getSortedHashMapByValue((Map<String, Double>)docs.get(i).clone());
			for(int j = 0; j < set.length; j ++) {
				if (j > ResultLimit) {
					break;
				}
				String key = set[j].getKey().toString();
				double value = Double.parseDouble(set[j].getValue().toString());
				if (!dict.containsKey(key)) {
					dict.put(key, value);
					dictCounter.put(key, 1);
				} else {
					double t = dict.get(key);
					int num = dictCounter.get(key);
					dict.remove(key);
					dictCounter.remove(key);
					dict.put(key, t + value);
					dictCounter.put(key, num + 1);
				}
			}
		}
		
		HashMap<String, Double> tempDict = (HashMap<String, Double>) dict.clone();
		dict.clear();
		int freq = (int) (tempDict.size() * 0.02);
		System.out.println("word freq threshold: " + freq);
		for (String key : tempDict.keySet()) {
			if (dictCounter.get(key) > freq) {
				double t = tempDict.get(key)
						/ (double) dictCounter.get(key);
				dict.put(key, t);
			}
		}
		tempDict.clear();
	}
	
	//将结果输出到html
	public static void saveStatistic(String outputPath) {
		FileOutput fo = new FileOutput(outputPath);
		int counter = 0;
		try {
			Map.Entry [] set = getSortedHashMapByValue((Map<String, Double>)dict.clone());
			for(int j = 0; j < set.length; j ++) {
				String key = set[j].getKey().toString();
				double value = Double.parseDouble(set[j].getValue().toString());
				if (value >= Threshold) {
					fo.t3.write("<p>"
						+ key + " " + value
						+ " " + dictCounter.get(key)
						+ "</p>");
					counter ++;
				}
			}
			fo.t3.write("<br/>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(counter + " results");
		fo.closeOutput();
	}
	
	public static void main(String [] args) {
		init();
		calculate(FilterConf.ProcessingPath + "tokens.pos.tfidf",
				FilterConf.ProcessingPath + "tokens.pos.span");
//		calculate(FilterConf.ProcessingPath + "tokens.pos.tfidf");
		saveToFile(FilterConf.ProcessingPath + "tokens.pos.span.tfidf");
		saveToHtml(FilterConf.ProcessingPath + "tokens.pos.span.tfidf."
				+ Threshold + ".html");
		statistic();
		saveStatistic(FilterConf.ProcessingPath + "tokens.pos.span.tfidf.statistic."
				+ Threshold + ".html");
	}
	
}
