package cn.edu.pku.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class Combination {

	public static HashMap<Long, HashMap<String, Double>> docs
			= new HashMap<Long, HashMap<String, Double>>();
	public static HashMap<String, Double> dict
			= new HashMap<String, Double>();
	public static HashMap<String, Integer> dictCounter
			= new HashMap<String, Integer>();
	public static final double Threshold = 0.0;
	public static final int ResultLimit = 10;
	
	public static void init() {
		clear();
	}
	
	//加载一项指标
	public static void load(String inputPath, String inputSeperator) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeperator);
				if (tokens.length <= 1) {
					continue;
				}
				
				Long id = Long.parseLong(tokens[0]);
				tokens = line.trim()
						.substring(line.trim().indexOf(inputSeperator)
								+ inputSeperator.length())
						.trim().split(inputSeperator);
				
				HashMap<String, Double> docDict = new HashMap<String, Double>();
				for (int i = 0; i < tokens.length; i = i + 2) {
					if (tokens[i].length() != 0) {
						docDict.put(tokens[i], Double.parseDouble(tokens[i + 1]));
					}
				}
				docs.put(id, (HashMap<String, Double>) docDict.clone());				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	//增加一项指标
	public static void add(String inputPath, String inputSeperator) {
		HashMap<Long, HashMap<String, Double>> newDocs
			= new HashMap<Long, HashMap<String, Double>>();
		
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeperator);
				if (tokens.length <= 1) {
					continue;
				}
				
				Long id = Long.parseLong(tokens[0]);
				tokens = line.trim()
						.substring(line.trim().indexOf(inputSeperator)
								+ inputSeperator.length())
						.trim().split(inputSeperator);
				
				if (docs.containsKey(id)) {
					HashMap<String, Double> docDict = docs.get(id);
					HashMap<String, Double> newDocDict = new HashMap<String, Double> ();
					for (int i = 0; i < tokens.length; i = i + 2) {
						if (tokens[i].length() != 0 && docDict.containsKey(tokens[i])) {
							newDocDict.put(tokens[i],
									docDict.get(tokens[i])
									* Double.parseDouble(tokens[i + 1]));
						}
					}
					newDocs.put(id, newDocDict);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		docs = (HashMap<Long, HashMap<String, Double>>) newDocs.clone();
		newDocs.clear();
		fi.closeInput();
	}
	
	public static void saveToFile(String outputPath, String outputSeperator) {
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			for (Long id : docs.keySet()) {
				HashMap<String, Double> doc = docs.get(id);
				fo.t3.write(id + outputSeperator);
				for (String token : doc.keySet()) {
					double span = doc.get(token);
					if (span >= Threshold) {
						fo.t3.write(token + outputSeperator
								+ doc.get(token) + outputSeperator);
					}
				}
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fo.closeOutput();
	}
	
	public static void saveToHtml(String outputPath, String outputSeperator) {
		FileOutput fo = new FileOutput(outputPath);
		
		int counter = 0;
		try {
			for (Long id : docs.keySet()) {
				if (counter ++ > 5) {
					break;
				}
				
				Map.Entry [] set = getSortedHashMapByValue((Map<String, Double>)docs.get(id).clone());
				for(int j = 0; j < set.length; j ++)
				{
					double tfidf = Double.parseDouble(set[j].getValue().toString());
					if (tfidf >= Threshold) {
						fo.t3.write("<p>"
							+ set[j].getKey().toString()
							+ outputSeperator + tfidf
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
		for (long id : docs.keySet()) {
			Map.Entry [] set = getSortedHashMapByValue((Map<String, Double>)docs.get(id).clone());
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
	
	public static void mergeFile(
			String FeatureDir,
			String [] industries,
			String [] industryDirs,
			String inputFileName,
			String outputPath
			) {
		FileOutput fo = new FileOutput(outputPath);
		for (int i = 0; i < industryDirs.length; i ++) {
			FileInput fi = new FileInput(FeatureDir
					+ "/" + industryDirs[i] + "/" + inputFileName);
			try {
				fo.t3.write(industries[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = new String();
			String content = new String ();
			try {
				while ((line = fi.reader.readLine()) != null) {
					content += " " + line.trim();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fo.t3.write(content);
				fo.t3.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fi.closeInput();
		}
		fo.closeOutput();
	}
	
	public static void main(String [] args) {
		init();

//		load(FilterConf.FeaturePath + "tokens.pos.con", " ");
//		add(FilterConf.FeaturePath + "tokens.pos.span", " ");
//		saveToFile(FilterConf.FeaturePath + "tokens.pos.con.span", " ");
//		saveToHtml(FilterConf.FeaturePath + "tokens.pos.con.span."
//				+ Threshold + ".html", " ");	
//		statistic();
//		saveStatistic(FilterConf.FeaturePath + "tokens.pos.con.span.statistic."
//				+ Threshold + ".html");
		
//		load(FilterConf.FeaturePath + "tokens.pos.con", " ");
//		add(FilterConf.FeaturePath + "tokens.pos.tfidf", " ");
//		saveToFile(FilterConf.FeaturePath + "tokens.pos.con.tfidf", " ");
//		saveToHtml(FilterConf.FeaturePath + "tokens.pos.con.tfidf."
//				+ Threshold + ".html", " ");	
//		statistic();
//		saveStatistic(FilterConf.FeaturePath + "tokens.pos.con.tfidf.statistic."
//				+ Threshold + ".html");
		
//		load(FilterConf.FeaturePath + "tokens.pos.span", " ");
//		add(FilterConf.FeaturePath + "tokens.pos.tfidf", " ");
//		saveToFile(FilterConf.FeaturePath + "tokens.pos.span.tfidf", " ");
//		saveToHtml(FilterConf.FeaturePath + "tokens.pos.span.tfidf."
//				+ Threshold + ".html", " ");	
//		statistic();
//		saveStatistic(FilterConf.FeaturePath + "tokens.pos.span.tfidf.statistic."
//				+ Threshold + ".html");
		
		load(FilterConf.FeaturePath + "tokens.pos.span", " ");
		add(FilterConf.FeaturePath + "tokens.pos.con", " ");
		add(FilterConf.FeaturePath + "tokens.pos.tfidf", " ");
		saveToFile(FilterConf.FeaturePath + "tokens.pos.span.con.tfidf", " ");
		saveToHtml(FilterConf.FeaturePath + "tokens.pos.span.con.tfidf."
				+ Threshold + ".html", " ");	
		statistic();
		saveStatistic(FilterConf.FeaturePath + "tokens.pos.span.con.tfidf.statistic."
				+ Threshold + ".html");
	}
	
}
