package cn.edu.pku.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import cn.edu.pku.hyp.Hyponymy;
import cn.edu.pku.hyp.HyponymyObj;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.w2v.Word2Vec;
import cn.edu.pku.w2v.WordEntry;

public class SimilarityProcessor {

	public HashMap<String, String> tree
		= new HashMap<String, String>();
	public HashMap<String, ArrayList<String>> dict
		= new HashMap<String, ArrayList<String>>();
	public HashMap<String, HashMap<String, Double>> simMap
		= new HashMap<String, HashMap<String, Double>>();
	public HashMap<String, Map.Entry[]> sim
		= new HashMap<String, Map.Entry[]>();
	public final double w1 = 0.5, w2 = 0.5, w3 = 0.5;
	
	public void calculate (Hyponymy hyponymy, Word2Vec w2v) {
		for (HyponymyObj o : hyponymy.hypDict) {
			tree.put(o.hyponym, o.hypernym);
		}
		for (String c : tree.keySet()) {
			if (!dict.containsKey(c)) {
				ArrayList<String> list = new ArrayList<String>();
				String concept = c;
				if (c == null) {
					continue;
				}
				while (tree.containsKey(c)) {
					list.add(tree.get(c));
					c = tree.get(c);
				}
				Collections.reverse(list);
				dict.put(concept, list);
			}
			c = tree.get(c);
			if (!dict.containsKey(c)) {
				ArrayList<String> list = new ArrayList<String>();
				String concept = c;
				if (c == null) {
					continue;
				}
				while (tree.containsKey(c)) {
					list.add(tree.get(c));
					c = tree.get(c);
				}
				Collections.reverse(list);
				dict.put(concept, list);
			}
		}
		
		for (String c1 : dict.keySet()) {
			ArrayList<String> list1 = dict.get(c1);
			int len1 = list1.size() + 1;
			HashMap<String, Double> m = new HashMap<String, Double>();
			for (String c2 : dict.keySet()) {
				if (c1.equals(c2)) {
					continue;
				}
				ArrayList<String> list2 = dict.get(c2);
				int distCommon = 0;
				int len2 = list2.size() + 1;
				for (int i = 0; i < list1.size() && i < list2.size(); i ++) {
					if (list1.get(i).equals(list2.get(i))) {
						distCommon ++;
					} else {
						break;
					}
				}
				double sim1 = Math.pow(Math.E, 1.0 - (double) (len1 + len2) / 2.0 / distCommon);
				double sim2 = Math.pow(Math.E, (double) (distCommon * 2.0 - len1 - len2));
				m.put(c2, Math.pow(sim1, w1) * Math.pow(sim2, w2));
			}
			simMap.put(c1, m);
		}
		
		for (String c1 : dict.keySet()) {
			HashMap<String, Double> m = simMap.get(c1);
			Set<WordEntry> result = w2v.distance(c1);
			for(int i = 0; i < result.size(); i ++) {
				String c2 = result.toArray()[i].toString().split("	")[0];
				if (!m.containsKey(c2)) {
					continue;
				}
				double sim3 = Double.parseDouble(result.toArray()[i].toString().split("	")[1]);
				m.put(c2, Math.pow(m.get(c2), (1.0 - w3)) * Math.pow(sim3, w3));
			}
			Map.Entry[] set = getSortedHashMapByValueDes(m);
			sim.put(c1, set);
		}
		
		tree.clear();
		simMap.clear();
	}
	
	public void save(String outputPath) {
		FileOutput fo = new FileOutput(outputPath);
		ObjectMapper om = new ObjectMapper();
		try {
			fo.t3.write(om
				.writerWithDefaultPrettyPrinter()
				.writeValueAsString(this));
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	public void load(String modelPath) {
		FileInput fi = new FileInput(modelPath);
		ObjectMapper om = new ObjectMapper();
		try {
			String content = new String();
			String line = null;
			while((line = fi.reader.readLine()) != null) {
				content += line;
			}
			SimilarityProcessor sp = om.readValue(content, SimilarityProcessor.class);
			this.sim = (HashMap<String, Map.Entry[]>) sp.sim.clone();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	/**
	 * HashMap排序器
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map.Entry[] getSortedHashMapByValueDes(Map h)
	{
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() 
		{
			public int compare(Object arg0, Object arg1) 
			{
				double value1 = (double) ((Map.Entry) arg0).getValue();
				double value2 = (double) ((Map.Entry) arg1).getValue();
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
		
	}
}
