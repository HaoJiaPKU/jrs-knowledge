package cn.edu.pku.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class GBDTProcessor {

	public static HashMap<String, Integer> dict = new HashMap<String, Integer>();
	public static HashMap<String, Integer> array = new HashMap<String, Integer>();
	
	public static void init() {
		dict.clear();
		array.clear();
	}
	
	public static void clear() {
		dict.clear();
		array.clear();
	}
	
	public static void removeDuplicateData(String inputPath, String outputPath) {
		FileInput fi = new FileInput(inputPath);
		FileOutput fo = new FileOutput(outputPath);
		HashSet<String> dataDict = new HashSet<String> ();
		String line = new String ();
		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				line = line.trim();
				if (!dataDict.contains(line)) {
					dataDict.add(line);
					fo.t3.write(line);
					fo.t3.newLine();
					counter ++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(counter + " pieces of data");
		fo.closeOutput();
		fi.closeInput();
	}
	
	public static void loadFeatureAsDict(String inputPath, String inputSeperator,
			int num) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				if (counter < num) {
					counter ++;
				} else {
					break;
				}
				String [] tokens = line.trim().split(inputSeperator);
				if (tokens.length <= 1) {
					continue;
				}
				dict.put(tokens[0], Integer.parseInt(tokens[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void loadFeatureAsArray(String inputPath, String inputSeperator,
			int num) {
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split(inputSeperator);
				if (tokens.length <= 1) {
					continue;
				}
				array.put(tokens[0], counter);
				if (counter + 1 < num) {
					counter ++;
				} else {
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void extractFeature(String inputPath, String inputSeperator,
			String outputPath, String outputSeperator) {
		FileInput fi = new FileInput(inputPath);
		FileOutput fo = new FileOutput(outputPath);
		HashSet<String> dataDict = new HashSet<String> ();
		String line = new String ();
		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				line = line.trim();
				boolean flag = false;
				String content = new String();
				String items [] = line.split(inputSeperator);
				for (int i = 0; i < items.length; i ++) {
					if (i != 0) {
						if (content.length() == 0) {
							break;
						}
						content += outputSeperator;
						flag = true;
					}
					String tokens [] = items[i].trim().split(" ");
					for (int j = 0; j < tokens.length; j ++) {
						if (dict.containsKey(tokens[j])) {
							content += tokens[j] + " ";
						}
					}
				}
				if (content.indexOf(outputSeperator) == content.length() - outputSeperator.length()) {
					continue;
				}
				if (flag && !dataDict.contains(content)) {
					dataDict.add(content);
					fo.t3.write(content);
					fo.t3.newLine();
					counter ++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(counter + " pieces of data");
		fo.closeOutput();
		fi.closeInput();
	}

	public static void textToVector(String inputPath, String inputSeperator,
			String outputPath,
			String outputPathForPosition,
			int K) {
		FileInput fi = new FileInput(inputPath);
		FileOutput fo = new FileOutput(outputPath);
		FileOutput foPosition = new FileOutput(outputPathForPosition);
		HashMap<String, HashSet<String>> posDict
			= new HashMap<String, HashSet<String>>();
		HashMap<String, Integer> posNum = new HashMap<String, Integer>();
		String line = new String ();
		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				line = line.trim();
				String features = new String();
				String items [] = line.split(inputSeperator);
				if (items.length < 2) {
					continue;
				}
				String label [] = items[0].trim().split(" ");
				String tokens [] = items[1].trim().split(" ");
				double tf [] = new double [array.size()];
				for (int i = 0; i < tf.length; i ++) {
					tf[i] = 0.0;
				}
				for (int i = 0; i < tokens.length; i ++) {
					if (array.containsKey(tokens[i])) {
						tf[array.get(tokens[i])] += 1.0;
					}
				}
				
				for (int i = 0; i < tf.length; i ++) {
//					tf[i] = Math.exp(tf[i]) / Math.exp((double)tokens.length);
//					tf[i] = (tf[i] + 1.0) / (double) (tokens.length + 1.0);
					features += "," + String.valueOf(tf[i]);
				}
				
				for (int i = 0; i < label.length; i ++) {
					if (label == null || label.length == 0) {
						continue;
					}
					HashSet<String> t;
					if (!posDict.containsKey(label[i])) {
						t = new HashSet<String> ();
					} else {
						t = posDict.get(label[i]);
					}
					t.add(features);
					posDict.put(label[i], t);
				}
			}
			
			fo.t3.write("label");
			Map.Entry[] set = getSortedHashMapByValueAsc(array);
			for (int i = 0; i < set.length; i ++) {
				String key = set[i].getKey().toString();
				fo.t3.write("," + key);
			}
			fo.t3.newLine();
			
			for (String label : posDict.keySet()) {
				posNum.put(label, posDict.get(label).size());
			}
			
			set = getSortedHashMapByValueDec(posNum);
			for (int i = 0; i < set.length; i ++) {
				String key = set[i].getKey().toString();
				String value = set[i].getValue().toString();
				foPosition.t3.write(key + "	" + value);
				foPosition.t3.newLine();
			}
			int sampleNum = posDict.get(set[K - 1].getKey()).size();
//			System.out.println(sampleNum);
			
			for (int i = 0; i < K; i ++) {
				String label = set[i].getKey().toString();
//				System.out.println(label + " " + posDict.get(label).size());
				HashSet<String> t = sample(posDict.get(label), sampleNum);
				for (String str : t) {
					fo.t3.write(label + str);
					fo.t3.newLine();
					counter ++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(counter + " pieces of data");
		fi.closeInput();
		fo.closeOutput();
		foPosition.closeOutput();
	}
	
	public static HashSet<String> sample(HashSet<String> set, int num) {
		HashSet<String> res = new HashSet<String>();
		ArrayList<String> t = new ArrayList<String>();
		for (String str : set) {
			t.add(str);
		}
		Random r = new Random();
		for (int i = 0; i < num; i ++) {
			int index = r.nextInt(t.size());
			res.add(t.get(index));
			t.remove(index);
		}
		return res;
	}
	
	/**
	 * HashMap排序器
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map.Entry[] getSortedHashMapByValueAsc(Map h)
	{
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() 
		{
			public int compare(Object arg0, Object arg1) 
			{
				int value1 = (int) ((Map.Entry) arg0).getValue();
				int value2 = (int) ((Map.Entry) arg1).getValue();
				if (value2 > value1) {
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
	
	/**
	 * HashMap排序器
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map.Entry[] getSortedHashMapByValueDec(Map h)
	{
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() 
		{
			public int compare(Object arg0, Object arg1) 
			{
				int value1 = (int) ((Map.Entry) arg0).getValue();
				int value2 = (int) ((Map.Entry) arg1).getValue();
				if (value2 > value1) {
					return 1;
				} else if (value2 == value1) {
					return 0;
				} else {
					return -1;
				}
		    }
		});
		return entries;
	}

}
