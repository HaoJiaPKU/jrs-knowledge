package cn.edu.pku.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class Span {

	public static HashMap<Long, HashMap<String, Double>> docs
			= new HashMap<Long, HashMap<String, Double>>();
	public static final double Threshold = 0.0;
	
	public static void init() {
		clear();
	}
	
	public static void calculate(String inputPath, String inputSeparator, int index) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeparator);
				if (tokens.length <= index) {
					continue;
				}
				
				Long id = Long.parseLong(tokens[0]);
				
				HashMap<String, String> docDict = new HashMap<String, String>();
				for (int i = index; i < tokens.length; i ++) {
					if (!docDict.containsKey(tokens[i])) {
						docDict.put(tokens[i], String.valueOf(i));
					} else {
						String t = docDict.get(tokens[i]);
						docDict.remove(tokens[i]);
						docDict.put(tokens[i], t + " " + String.valueOf(i));
					}
				}
				HashMap<String, Double> doc = new HashMap<String, Double>();
				for (String token : docDict.keySet()) {
					String [] t = docDict.get(token).split(" ");
					if (t.length == 1) {
						doc.put(token, 1.0 
								- 1.0 
								/ (double) (tokens.length + 1.0));
					} else {
						doc.put(token, 1.0
								- ((double) (Integer.parseInt(t[t.length - 1])
								- Integer.parseInt(t[0])) + 1.0)
								/ (double) (tokens.length + 1.0));
					}
				}
				docs.put(id, (HashMap<String, Double>) doc.clone());				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	public static void clear() {
		docs.clear();
	}
	
	public static void main(String [] args) {
		init();
		calculate(FilterConf.ConceptPath + "tokens", " ", 3);
		saveToFile(FilterConf.ConceptPath + "tokens.span", " ");
		calculate(FilterConf.ConceptPath + "tokens.pos", " ", 1);
		saveToFile(FilterConf.ConceptPath + "tokens.pos.span", " ");
	}
	
}
