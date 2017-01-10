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

public class TFIDF {

	public static HashMap<String, Double> dict = new HashMap<String, Double>();
	public static ArrayList<HashMap<String, Double>> docs
			= new ArrayList<HashMap<String, Double>>();
	public static final double Threshold = 0.03;
	
	public static void init() {
		clear();
	}
	
	public static void calculate(String inputPath) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(" +");
				if (tokens.length <= 2) {
					continue;
				}
				
				HashMap<String, Double> doc = new HashMap<String, Double>();
				//计算tf值，统计单一文档中单词出现次数
				for (int i = 2; i < tokens.length; i ++) {
					if (!doc.containsKey(tokens[i])) {
						doc.put(tokens[i], 1.0);
					} else {
						double t = doc.get(tokens[i]);
						doc.remove(tokens[i]);
						doc.put(tokens[i], t + 1.0);
					}
				}
				//计算tf值，计算单词出现次数/文档长度
				HashMap<String, Double> docTF = new HashMap<String, Double>();
				for (String token : doc.keySet()) {
					docTF.put(token, doc.get(token) / (double) tokens.length);
				}
				docs.add((HashMap<String, Double>) docTF.clone());
				
				//统计出现单词的文档
				for (String token : doc.keySet()) {					
					if (!dict.containsKey(token)) {
						dict.put(token, 1.0);
					} else {
						double t = dict.get(token);
						dict.remove(token);
						dict.put(token, t + 1.0);
					}
				}				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
		
		double docSize = docs.size();
		//计算idf值
		HashMap<String, Double> dictIDF = new HashMap<String, Double>();
		for (String token : dict.keySet()) {
			dictIDF.put(token, Math.log(docSize / (1.0 + dict.get(token))));
		}
		dict.clear();
		dict = (HashMap<String, Double>) dictIDF.clone();
		dictIDF.clear();
		
		//计算tf-idf值
		ArrayList<HashMap<String, Double>> docsTFIDF = new ArrayList<HashMap<String, Double>>();
		for (int i = 0; i < docs.size(); i ++) {
			HashMap<String, Double> docTFIDF = new HashMap<String, Double>();
			for (String token : docs.get(i).keySet()) {
				docTFIDF.put(token, docs.get(i).get(token) * dict.get(token));
			}
			docsTFIDF.add(docTFIDF);
		}
		docs = (ArrayList<HashMap<String, Double>>) docsTFIDF.clone();
		docsTFIDF.clear();
	}
	
	public static void saveToFile(String outputPath) {
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			for (int i = 0; i < docs.size(); i ++) {
				for (String token : docs.get(i).keySet()) {
					double tfidf = docs.get(i).get(token);
					if (tfidf >= Threshold) {
						fo.t3.write(token + " " + docs.get(i).get(token) + " ");
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
	
	public static void saveToHtml(String outputPath) {
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			for (int i = 0; i < docs.size(); i ++) {
				if (i > 5) {
					break;
				}
				
				Map.Entry [] set = getSortedHashMapByValue((Map<String, Double>)docs.get(i).clone());
				for(int j = 0; j < set.length; j ++)
				{
					double tfidf = Double.parseDouble(set[j].getValue().toString());
					if (tfidf >= Threshold) {
						fo.t3.write("<p>"
							+ set[j].getKey().toString()
							+ " " + tfidf
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
		dict.clear();
		docs.clear();
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
	
	public static void main(String [] args) {
		init();
//		calculate(FilterConf.ProcessingPath + "tokens");
//		saveToFile(FilterConf.ProcessingPath + "tokens.tfidf");
		calculate(FilterConf.ProcessingPath + "tokens.pos");
		saveToFile(FilterConf.ProcessingPath + "tokens.pos.tfidf");
		saveToHtml(FilterConf.ProcessingPath + "tokens.pos.tfidf."
				+ Threshold + ".html");
	}
	
}
