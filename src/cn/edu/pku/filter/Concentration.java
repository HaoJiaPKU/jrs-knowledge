package cn.edu.pku.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class Concentration {

	public static HashMap<Long, HashMap<String, Double>> docs
			= new HashMap<Long, HashMap<String, Double>>();
	public static final double Threshold = 0.0;
	public static final int Border = 4;
	
	public static void init() {
		clear();
	}
	
	public static void calculate(String inputPath, String inputSeperator) {
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
				
				int [] loc = new int [tokens.length];
				for (int i = 0; i < tokens.length; i ++) {
					String [] t = tokens[i].trim().split("/");
					if (t.length < 2) {
						continue;
					}
					loc[i] = Integer.parseInt(t[1]);
				}
				
				HashMap<String, Double> docDict = new HashMap<String, Double>();
				for (int i = 0; i < tokens.length; i ++) {
					String [] t = tokens[i].trim().split("/");
					if (t.length < 2) {
						continue;
					}
					double con = 0;
					for (int j = 1; j <= Border; j ++) {
						int tokenSpan = 0;
						if (i - j < 0 && i + j < tokens.length) {
							tokenSpan = 2 * (loc[i + j] - loc[i]);
						} else if (i - j >= 0 && i + j >= tokens.length) {
							tokenSpan = 2 * (loc[i] - loc[i - j]);
						} else if (i - j >= 0 && i + j < tokens.length) {
							tokenSpan = loc[i + j] - loc[i - j];
						} else {
							continue;
						}
						con += 1.0 / (double) (tokenSpan);
					}
					con /= (double)Border;
					if (!docDict.containsKey(t[0])) {
						docDict.put(t[0], con);
					} else {
						double preCon = docDict.get(t[0]);
						if (con > preCon) {
							docDict.remove(t[0]);
							docDict.put(t[0], con);
						}
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
		calculate(FilterConf.ConceptPath + "tokens.pos.loc", " ");
		saveToFile(FilterConf.ConceptPath + "tokens.pos.con", " ");
	}
	
}
