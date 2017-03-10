package cn.edu.pku.hyp;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hankcs.hanlp.HanLP;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.hc.Cluster;
import cn.edu.pku.hc.Model;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.util.HanLPSegmenter;
import cn.edu.pku.util.UrlUtil;

public class Hyponymy {
	
	public static final String Sign = "[\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]";
	public static final String NonSign = "[^\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]";
	
	//最终提取结果
	public HashSet<HyponymyObj> hypDict
			= new HashSet<HyponymyObj>();
	//单词词典
	public HashSet<String> wordDict
			= new HashSet<String>();
	//单词与维基百科解释
	public HashMap<String, String> wordWiki
			= new HashMap<String, String>();
	//单词与百度百科解释
	public HashMap<String, String> wordBaidu
			= new HashMap<String, String>();

	public Hyponymy() {
		// TODO Auto-generated constructor stub
	}

	public void init(String inputPath,
			String inputSeperator) {
		hypDict.clear();
		wordDict.clear();
		wordWiki.clear();
		wordBaidu.clear();
		
		FileInput fi = new FileInput(inputPath); 
        String line = null;
        try {
			while ((line = fi.reader.readLine()) != null) {  
				String[] s = line.split(inputSeperator);
				if (s.length < 2) {
					continue;
				}
			    wordDict.add(s[0].trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void init(String inputPath,
			String inputSeperator,
			String wikiPath,
			String baiduPath) {
		hypDict.clear();
		wordDict.clear();
		wordWiki.clear();
		wordBaidu.clear();
		
        FileInput fi = new FileInput(inputPath); 
        String line = null;
        try {
			while ((line = fi.reader.readLine()) != null) {  
				String[] s = line.split(inputSeperator);
				if (s.length < 2) {
					continue;
				}
			    wordDict.add(s[0].trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if (new File(wikiPath).exists()) {
        	fi = new FileInput(wikiPath); 
           	line = null;
            try {
    			while ((line = fi.reader.readLine()) != null) {  
    				String[] s = line.split(inputSeperator);
    				if (s.length < 2) {
    					continue;
    				}
    			    wordWiki.put(s[0].trim(), s[1].trim().toLowerCase());
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
        
        if (new File(baiduPath).exists()) {
        	fi = new FileInput(baiduPath); 
           	line = null;
            try {
    			while ((line = fi.reader.readLine()) != null) {  
    				String[] s = line.split(inputSeperator);
    				if (s.length < 2) {
    					continue;
    				}
    			    wordBaidu.put(s[0].trim(), s[1].trim().toLowerCase());
    			}
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
	}
	
	public void clear() {
		hypDict.clear();
		wordDict.clear();
		wordWiki.clear();
		wordBaidu.clear();
	}
	
	public void crawlExplainationFromWiki(String outputPath) {
		for (String word : wordDict) {
			try {
				System.out.println(word);
				String content = UrlUtil.getHTML(
						"https://zh.wikipedia.org/wiki/"
						+ URLEncoder.encode(word,"gb2312"));
				Document doc = Jsoup.parse(content);
				Element div = doc.select("p").first();
				if (div == null) {
					continue;
				}
				wordWiki.put(word, HanLP.convertToSimplifiedChinese(div.text()));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileOutput fo = new FileOutput(outputPath);
		try {
			for (String word : wordWiki.keySet()) {
				fo.t3.write(word + "	" + wordWiki.get(word));
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	public void crawlExplainationFromBaidu(String outputPath) {
//		int counter = 0;
		for (String word : wordDict) {
			try {
//				if (counter ++ >= 1) {
//					break;
//				}
//				System.out.println(word);
				String content = UrlUtil.getHTML(
						"http://www.baidu.com/s?wd="
						+ URLEncoder.encode(word + " 百度百科","gb2312")
						+ "&rn=10");
				Document doc = Jsoup.parse(content);
				Elements divs = doc.select("div[fk]");
				if (divs == null) {
					continue;
				}
				Element div = divs.first();
				if (div == null) {
					continue;
				}
				Elements as = div.select("a");
				if (as == null) {
					continue;
				}
				Element a = as.first();
				content = UrlUtil.getHTML(a.attr("href"));
				doc = Jsoup.parse(content);
				Elements metas = doc.select("meta[name=description]");
				if (metas == null) {
					continue;
				}
				Element meta = metas.first();
				if (meta == null || meta.attr("content") == null) {
					continue;
				}
				System.out.println(word + "	" + meta.attr("content").trim());
				wordBaidu.put(word, HanLP.convertToSimplifiedChinese(meta.attr("content").trim()));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileOutput fo = new FileOutput(outputPath);
		try {
			for (String word : wordBaidu.keySet()) {
				fo.t3.write(word + "	" + wordBaidu.get(word));
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	public boolean analyze(String word, String p, String explaination) {
		String expression = word + p;
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(explaination);
		String candidate = null;
		boolean flag = false;
		while (matcher.find()) {
			String group = matcher.group().toString();
			if (candidate == null || group.length() < candidate.length()) {
				candidate = group;
			}
			flag = true;
		}
		if (flag) {
			System.out.println(word + " " + candidate);
			String [] tokens = HanLPSegmenter.segmentation(candidate, true, false, true, null);
			System.out.println(word + " " + tokens[tokens.length - 1]);
			HyponymyObj hyp = new HyponymyObj(word, tokens[tokens.length - 1], candidate);
			hypDict.add(hyp);
		}
		return flag;
	}
	
	public void analyzeHyponymyPairFromBaike() {
		for (String word : wordWiki.keySet()) {
			String explaination = wordWiki.get(word);
			while (explaination.indexOf("（") != -1 && explaination.indexOf("）") != -1
					&& explaination.indexOf("（") < explaination.indexOf("）")) {
				explaination = explaination.substring(0, explaination.indexOf("（"))
						+ explaination.substring(explaination.indexOf("）") + 1);
			}
			while (explaination.indexOf("[") != -1 && explaination.indexOf("]") != -1
					&& explaination.indexOf("[") < explaination.indexOf("]")) {
				explaination = explaination.substring(0, explaination.indexOf("["))
						+ explaination.substring(explaination.indexOf("]") + 1);
			}
			word = word.replaceAll("[+]", "p");
			explaination = explaination.replaceAll("[+]", "p");
			explaination = explaination.replaceAll(" ", "").trim();

			boolean flag = analyze(
					word, ".*?是[一]*[种|类|门|个|款|套].+?[，|。|；]", explaination);
//			if (!flag) {
//				flag = analyze(
//					word, ".*?是.*?[一]*[种|类|门|个|款].+?[，|。|；]", explaination);
//			}
//			if (!flag) {
//				flag = analyze(
//					word, ".*?[是|指].*?的.+?[，|。|；]", explaination);
//			}
		}
		System.out.println(hypDict.size());
		
		for (String word : wordBaidu.keySet()) {
			String explaination = wordBaidu.get(word);
			while (explaination.indexOf("（") != -1 && explaination.indexOf("）") != -1
					&& explaination.indexOf("（") < explaination.indexOf("）")) {
				explaination = explaination.substring(0, explaination.indexOf("（"))
						+ explaination.substring(explaination.indexOf("）") + 1);
			}
			while (explaination.indexOf("[") != -1 && explaination.indexOf("]") != -1
					&& explaination.indexOf("[") < explaination.indexOf("]")) {
				explaination = explaination.substring(0, explaination.indexOf("["))
						+ explaination.substring(explaination.indexOf("]") + 1);
			}
			word = word.replaceAll("[+]", "p");
			explaination = explaination.replaceAll("[+]", "p");
			explaination = explaination.replaceAll(" ", "").trim();

			boolean flag = analyze(
					word, ".*?是[一]*[种|类|门|个|款|套].+?[，|。|；]", explaination);
//			if (!flag) {
//				flag = analyze(
//					word, ".*?是.*?[一]*[种|类|门|个|款].+?[，|。|；]", explaination);
//			}
//			if (!flag) {
//				flag = analyze(
//					word, ".*?[是|指].*?的.+?[，|。|；]", explaination);
//			}
		}
		System.out.println(hypDict.size());
	}
	
	public void analyzeHyponymyPairFromHCModel(String HCModelPath) {
		Model model = new Model();
		model.load(HCModelPath);
		
		HashMap<String, HashSet<String>> dict
				= new HashMap<String, HashSet<String>>();
		for (int i = 0; i < model.clusters.size(); i ++) {
			dfs(dict, model.clusters.get(i), null);
    	}
		
		Map.Entry[] set = getSortedHashMapByValueSize(dict);
		dict.clear();
		
		for (int i = 0; i < set.length; i ++) {
			HashSet<String> hashset = (HashSet<String>) set[i].getValue();
			for (int j = i + 1; j < set.length; j ++) {
				String name = set[i].getKey().toString();
				if (hashset.contains(name)) {
					HashSet<String> t = (HashSet<String>) set[j].getValue();
					for (String n : t) {
						hashset.remove(n);
					}
				}
			}
			dict.put(set[i].getKey().toString(), hashset);
		}
		
		set = getSortedHashMapByValueSize(dict);
		dict.clear();
		
		for (int i = set.length - 1; i >= 0; i --) {
			HashSet<String> hashset = (HashSet<String>) set[i].getValue();
			for (int j = i - 1; j >= 0; j --) {
				HashSet<String> t = (HashSet<String>) set[j].getValue();
				for (String n : hashset) {
					t.remove(n);
				}
				set[j].setValue(t.clone());
			}
			dict.put(set[i].getKey().toString(), hashset);
		}
		
		for (String word : dict.keySet()) {
			HashSet<String> s = dict.get(word);
			for (String name : s) {
				HyponymyObj hyp = new HyponymyObj(name, word, "");
				hypDict.add(hyp);
			}
		}
		
//		set = getSortedHashMapByValueSize(dict);
//		dict.clear();
//		for (int i = 0; i < set.length; i ++) {
//			System.out.println(set[i].getKey().toString()
//					+ " " + set[i].getValue());
//		}
		
//		for (String name : dict.keySet()) {
//			System.out.println(name);
//			HashSet<String> t = dict.get(name);
//			for (String k : t) {
//				System.out.print(k + " ");
//			}
//			System.out.println();
//		}
	}
	
	public void dfs(HashMap<String, HashSet<String>> dict,
			Cluster node, Cluster brother) {
		if (node.getId() >= 0) {
			String name = node.getName();
			if (wordDict.contains(name)) {
				HashSet<String> subNodes = new HashSet<String>();
				dfsCollect(subNodes, node, brother);
				subNodes.remove(name);
				dict.put(name, subNodes);
			}
		}
		if (node.getLeft() != null) {
			dfs(dict, node.getLeft(), node.getRight());
		}
		if (node.getRight() != null) {
			dfs(dict, node.getRight(), node.getLeft());
		}
	}
	
	public void dfsCollect(HashSet<String> subNodes,
			Cluster node, Cluster brother) {
		if (node.getId() >= 0) {
			String name = node.getName();
			subNodes.add(name);
		}
		if (brother.getLeft() != null) {
			dfsCollect(subNodes, brother.getLeft(), brother.getRight());
		}
		if (brother.getRight() != null) {
			dfsCollect(subNodes, brother.getRight(), brother.getLeft());
		}
	}
	
	/**
	 * HashMap排序器
	 * */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map.Entry[] getSortedHashMapByValueSize(Map h)
	{
		Set set = h.entrySet();
		Map.Entry[] entries = (Map.Entry[]) set.toArray(new Map.Entry[set.size()]);
		Arrays.sort(entries, new Comparator() 
		{
			public int compare(Object arg0, Object arg1) 
			{
				HashSet<String> value1 = (HashSet<String>) ((Map.Entry) arg0).getValue();
				HashSet<String> value2 = (HashSet<String>) ((Map.Entry) arg1).getValue();
				if (value2.size() < value1.size()) {
					return -1;
				} else if (value2.size() == value1.size()) {
					return 0;
				} else {
					return 1;
				}
		    }
		});
		return entries;
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
			Hyponymy hyp = om.readValue(content, Hyponymy.class);
			this.hypDict = (HashSet<HyponymyObj>) hyp.hypDict.clone();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void main(String [] args) {
		Hyponymy hyponymy = new Hyponymy();
		hyponymy.init(FilterConf.FeaturePath
				+ "/" + "互联网or电子商务" + "/" + "stata.hyp.txt",
				"	");
//		hyponymy.getExplainationFromWiki(FilterConf.FeaturePath
//				+ "/" + "互联网or电子商务" + "/" + "token.pos.wiki.txt");
//		hyponymy.getExplainationFromBaidu(FilterConf.FeaturePath
//				+ "/" + "互联网or电子商务" + "/" + "token.pos.baidu.txt");
//		hyponymy.analyzeHyponymyPairFromBaike();
		
		hyponymy.analyzeHyponymyPairFromHCModel(FilterConf.FeaturePath
				+ "/" + "互联网or电子商务" + "/" + "model.hc.json");
		hyponymy.save(FilterConf.FeaturePath
				+ "/" + "互联网or电子商务" + "/" + "hyp.txt");
		hyponymy.load(FilterConf.FeaturePath
				+ "/" + "互联网or电子商务" + "/" + "hyp.txt");
		
	}
}
