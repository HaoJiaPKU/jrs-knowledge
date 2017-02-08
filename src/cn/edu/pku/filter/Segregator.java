package cn.edu.pku.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class Segregator {

	public static HashMap<String, Integer> dict
			= new HashMap<String, Integer>();
	public static ArrayList<ArrayList<String>> res
			= new ArrayList<ArrayList<String>> ();
	
	public static void init() {
		dict.clear();
		res.clear();
	}
	
	public static void clear() {
		dict.clear();
		res.clear();
	}
	
	public static void makeDict(
			String processingDir,
			String [] industryDirs,
			String inputFileName
			) {
		for (int i = 0; i < industryDirs.length; i ++) {
			makeDict(processingDir
					+ "/" + industryDirs[i] + "/" + inputFileName, " ");
		}
	}
	
	public static void makeDict (
			String inputPath,
			String inputSeperator
			) {
		
			FileInput fi = new FileInput(inputPath);
			
			String line = new String ();
			try {
				while ((line = fi.reader.readLine()) != null) {
					
					String tokens[] = line.trim().split(inputSeperator);
					ArrayList<String> ind = new ArrayList<String>();
					ind.add(tokens[0].trim());
					for (int j = 1; j < tokens.length; j ++) {
						tokens[j] = tokens[j].trim();
						if (dict.containsKey(tokens[j])) {
							dict.put(tokens[j], dict.get(tokens[j]) + 1);
						} else {
							dict.put(tokens[j], 1);
						}
						ind.add(tokens[j]);
					}
					res.add(ind);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fi.closeInput();
	}
	
	public static void saveToFile(
			String outputPath,
			String outputSeperator,
			int thres
			) {
		
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			for (int i = 0; i < res.size(); i ++) {
				ArrayList<String> ind = res.get(i);
				fo.t3.write(ind.get(0));
				for (int j = 1; j < ind.size(); j ++) {
					if (dict.get(ind.get(j)) <= thres) {
						fo.t3.write(outputSeperator + ind.get(j));
					}
				}
				fo.t3.newLine();
			}
			for (String key : dict.keySet()) {
				if (dict.get(key) > thres) {
					fo.t3.write(key + outputSeperator);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fo.closeOutput();
		
	}
}
