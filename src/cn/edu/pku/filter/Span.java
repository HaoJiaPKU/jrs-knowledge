package cn.edu.pku.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class Span {

	public static ArrayList<HashMap<String, Double>> docs
			= new ArrayList<HashMap<String, Double>>();
	public static final double Threshold = 0.0;
	
	public static void init() {
		clear();
	}
	
	public static void calculate(String inputPath) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
//		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(" +");
				if (tokens.length <= 2) {
					continue;
				}
//				if (++ counter > 1) {
//					break;
//				}
				
				HashMap<String, String> docDict = new HashMap<String, String>();
				for (int i = 2; i < tokens.length; i ++) {
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
//						System.out.println(Integer.parseInt(t[t.length - 1])
//								- Integer.parseInt(t[0]));
						doc.put(token, 1.0
								- ((double) (Integer.parseInt(t[t.length - 1])
								- Integer.parseInt(t[0])) + 1.0)
								/ (double) (tokens.length + 1.0));
					}
				}
				docs.add((HashMap<String, Double>) doc.clone());				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void saveToFile(String outputPath) {
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			for (int i = 0; i < docs.size(); i ++) {
				for (String token : docs.get(i).keySet()) {
					double span = docs.get(i).get(token);
					if (span >= Threshold) {
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
	
	public static void clear() {
		docs.clear();
	}
	
	public static void main(String [] args) {
		init();
//		calculate(FilterConf.ProcessingPath + "tokens");
//		saveToFile(FilterConf.ProcessingPath + "tokens.span");
		calculate(FilterConf.ProcessingPath + "tokens.pos");
		saveToFile(FilterConf.ProcessingPath + "tokens.pos.span");
	}
	
}
