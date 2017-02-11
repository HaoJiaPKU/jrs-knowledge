package cn.edu.pku.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class Statistic {

	public static HashMap<String, Integer> dict
		= new HashMap<String, Integer>();
	public static int wordTotalNum = 0;
	
	public static void init() {
		wordTotalNum = 0;
		dict.clear();
	}
	
	public static void clear() {
		wordTotalNum = 0;
		dict.clear();
	}
	
	/**
	 * HashMap排序器
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map.Entry[] getSortedHashMapByValue(Map h, String string)
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
	public static Map.Entry[] getSortedHashMapByValue(Map h, int num)
	{
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() 
		{
			public int compare(Object arg0, Object arg1) 
			{
				int value1 = (int) ((Map.Entry) arg0).getValue();
				int value2 = (int) ((Map.Entry) arg1).getValue();
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
	
	public static void load(String inputPath, String inputSeperator, int thresNum) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(" +");
				for (int i = 0; i < tokens.length; i ++) {
					if (tokens[i] == null
						|| tokens[i].length() == 0) {
							continue;
					}
					if (dict.containsKey(tokens[i])) {
						dict.put(tokens[i], dict.get(tokens[i]) + 1);
					} else {
						dict.put(tokens[i], 1);
					}
					wordTotalNum ++;
				}
			}
			HashMap<String, Integer> temp = new HashMap<String, Integer>();
			for (String key : dict.keySet()) {
				if (dict.get(key) >= thresNum) {
					temp.put(key, dict.get(key));
				}
			}
			dict.clear();
			dict = (HashMap<String, Integer>) temp.clone();
			temp.clear();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void saveToFile(String outputPath, String outputSeperator) {
		FileOutput fo = new FileOutput(outputPath);
		
		try {
			Map.Entry[] set = getSortedHashMapByValue(dict, 0);
			for (int i = 0; i < set.length; i ++) {
				String key = set[i].getKey().toString();
				int value = (int) set[i].getValue();
				fo.t3.write(key + outputSeperator + value);
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fo.closeOutput();
	}
}
