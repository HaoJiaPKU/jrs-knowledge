package cn.edu.pku.filter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hankcs.hanlp.HanLP;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.hc.Cluster;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.util.HanLPSegmenter;
import cn.edu.pku.util.UrlUtil;
import cn.edu.pku.w2v.Word2Vec;
import cn.edu.pku.w2v.WordEntry;

public class Hyponymy {
	
	public static final String Sign = "[\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]";
	public static final String NonSign = "[^\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]";
	//最终提取结果
	public static HashMap<HyponymyPair, String> hypDict
			= new HashMap<HyponymyPair, String>();
	//带有向量的单词列表
	public static ArrayList<Cluster> wordInCluster
			= new ArrayList<Cluster>();
	//带有向量的单词词典
	public static HashMap<String, Cluster> wordOfCluster
			= new HashMap<String, Cluster>();
	//用于判断是否重复
	public static HashSet<HyponymyPair> hypDup
			= new HashSet<HyponymyPair>();
	//单词与百科解释
	public static HashMap<String, String> wordWiki
			= new HashMap<String, String>();
	public static Word2Vec w2v = new Word2Vec();

	public static void init(String inputPath,
			String inputSeperator,
			String W2VModelPath,
			String wikiPath) {
		hypDict.clear();
		wordInCluster.clear();
		wordOfCluster.clear();
		hypDup.clear();
		wordWiki.clear();
		try {
			w2v.loadJavaModel(W2VModelPath);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        FileInput fi = new FileInput(inputPath); 
        String line = null;
        try {
			while ((line = fi.reader.readLine()) != null) {  
				String[] s = line.split(inputSeperator);  
			    Cluster cluster = new Cluster();  
			    List<Double> list = new ArrayList<Double>();
			    cluster.setName(s[0]);
			    for (int j = 1; j < s.length; j ++) {
			    	list.add(Double.parseDouble(s[j]));
			    }
			    cluster.setVector(list);
			    wordInCluster.add(cluster);
			    wordOfCluster.put(s[0], cluster);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if (!new File(wikiPath).exists()) {
        	return;
        }
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
//        for (String word : wordWiki.keySet()) {
//        	System.out.println(word + " " + wordWiki.get(word));
//        }
//        System.out.println(wordOfCluster.size());
	}
	
	public static void clear() {
		hypDict.clear();
		wordInCluster.clear();
		wordOfCluster.clear();
		hypDup.clear();
		wordWiki.clear();
	}
	
	public static void getExplainationFromWiki(String outputPath) {
		for (String word : wordOfCluster.keySet()) {
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
	
	public static void getHyponymyPairFromWiki(String outputPath) {
		Pattern pattern;
		Matcher matcher;
		String expression;
		
		for (String word1 : wordWiki.keySet()) {
			String explaination = wordWiki.get(word1);
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
			word1 = word1.replaceAll("[+]", "p");
			explaination = explaination.replaceAll("[+]", "p");
			explaination = explaination.replaceAll(" ", "").trim();

//			String candidate = null;
//			while (explaination.indexOf("。") != -1) {
//				String can = explaination.substring(0, explaination.indexOf("。") + 1);
//				explaination = explaination.substring(explaination.indexOf("。") + 1);
//				if (candidate == null || can.length() < candidate.length()) {
//					candidate = can;
//				}
//			}
//			if (candidate != null) {
//				explaination = candidate;
//			} else {
//				continue;
//			}

			boolean flag = wikiAnalyze(
					word1, ".*?是[一]*[种|类|门|个|款|套].+?[，|。|；]", explaination);
//			if (!flag) {
//				flag = wikiAnalyze(
//					word1, ".*?是.*?[一]*[种|类|门|个|款].+?[，|。|；]", explaination);
//			}
//			if (!flag) {
//				flag = wikiAnalyze(
//					word1, ".*?[是|指].*?的.+?[，|。|；]", explaination);
//			}
		}
		
		FileOutput fo = new FileOutput(outputPath);
		try {
			for (HyponymyPair p : hypDict.keySet()) {
				fo.t3.write(p.hypernym + "	" + p.hyponym + "	" + hypDict.get(p));
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	public static boolean wikiAnalyze(String word, String p, String explaination) {
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
			String [] tokens = HanLPSegmenter.segmentation(candidate, true, false, null);
			System.out.println(word + " " + tokens[tokens.length - 1]);
			HyponymyPair hp = new HyponymyPair(word, tokens[tokens.length - 1]);
			hypDict.put(hp, candidate);
		}
		return flag;
	}
	
	//对所有词汇两两判断上下位关系
	public static void run(String outputPath) {
		for (int i = 0; i < wordInCluster.size(); i ++) {
			for (int j = i + 1; j < wordInCluster.size(); j ++) {
				System.out.println(wordInCluster.get(i).getName() + "	" + wordInCluster.get(j).getName());
				extract(wordInCluster.get(i).getName(), wordInCluster.get(j).getName());
				extract(wordInCluster.get(j).getName(), wordInCluster.get(i).getName());
			}
		}
		FileOutput fo = new FileOutput(outputPath);
		try {
			for (HyponymyPair p : hypDict.keySet()) {
				fo.t3.write(p.hypernym + "	" + p.hyponym + "	" + hypDict.get(p));
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	//对20个最接近的词汇两两判断上下位关系
	public static void run(String outputPath, int num) {
		for (int i = 0; i < wordInCluster.size(); i ++) {
			String word1 = wordInCluster.get(i).getName();
			Set<WordEntry> result = w2v.distance(word1);
			int j = 0, k = 0;
			while(j < num && k < result.size()) {
				WordEntry r = (WordEntry) result.toArray()[k];
				k ++;
				String word2 = r.name;
//				System.out.println(j + "	" + k + "	" + word1 + "	" + word2);
				if (wordOfCluster.containsKey(word2)) {
					System.out.println(word1 + "	" + word2);
					j ++;
					HyponymyPair hp = new HyponymyPair(word1, word2);
					if (!hypDup.contains(hp)) {
						hypDup.add(hp);
						extract(word1, word2);
					} else {
						continue;
					}
					hp = new HyponymyPair(word2, word1);
					if (!hypDup.contains(hp)) {
						hypDup.add(hp);
						extract(word2, word1);
					} else {
						continue;
					}					
				}
			}
		}
		FileOutput fo = new FileOutput(outputPath);
		try {
			for (HyponymyPair p : hypDict.keySet()) {
				fo.t3.write(p.hypernym + "	" + p.hyponym + "	" + hypDict.get(p));
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	public static void extract(String word1, String word2) {
		Pattern pattern;
		Matcher matcher;
		String content = null, expression;
		try {
			content = UrlUtil.getHTML(
					"http://www.baidu.com/s?wd="
					+ URLEncoder.encode(word1 + " " + word2,"gb2312")
					+ "&rn=" + String.valueOf(20));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (content == null) {
			return;
		}
//		System.out.println(content);
		boolean flag = false;
		Document doc = Jsoup.parse(content);
		Elements divs = doc.select(".result-op");
		for (Element div : divs) {
			String text = div.text().trim().toLowerCase();
//			System.out.println(text);
//			expression = word1 + "[^，|；|。|！|,|;|.|!|\\s|((?!" + word1 + ").)]*?是[一]*[[种|类|门|个|款]+][^，|；|。|！|,|;|.|!|\\s]*" + word2;
			expression = word1 + "[^不\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]*是[一]*[[种|类|门|个|款]+][^不\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]*" + word2;
			pattern = Pattern.compile(expression);
			matcher = pattern.matcher(text);
			if (matcher.find()) {
				String explaination = matcher.group();
				System.out.println(word1 + "	" + word2 + "	" + explaination);
				HyponymyPair hp = new HyponymyPair(word1, word2);
				hypDict.put(hp, explaination);
				flag = true;
				break;
			}
		}
		if (flag) {
			return;
		}
//		System.out.println("*********************************");
		divs = doc.select(".result");
		for (Element div : divs) {
			String text = div.text().trim().toLowerCase();
//			System.out.println(text);
			expression = word1 + "[^不\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]*是[一]*[[种|类|门|个|款]+][^不\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]*" + word2;
			pattern = Pattern.compile(expression);
			matcher = pattern.matcher(text);
			if (matcher.find()) {
				String explaination = matcher.group();
				System.out.println(word1 + "	" + word2 + "	" + explaination);
				HyponymyPair hp = new HyponymyPair(word1, word2);
				hypDict.put(hp, explaination);
				break;
			}
		}
	}
	
	public static void main(String [] args) {
		init(FilterConf.FeaturePath
				+ "/" + "计算机软件" + "/" + "w2v.vec.txt", "	",
			FilterConf.FeaturePath
				+ "/" + "计算机软件" + "/" + "w2v.model",
			FilterConf.FeaturePath
				+ "/" + "计算机软件" + "/" + "token.pos.wiki.txt");
		getHyponymyPairFromWiki(FilterConf.FeaturePath
				+ "/" + "计算机软件" + "/" + "hyp.txt");
//		getExplainationFromWiki(FilterConf.FeaturePath
//				+ "/" + "计算机软件" + "/" + "token.pos.wiki.txt");
//		run(FilterConf.FeaturePath
//				+ "/" + "计算机软件" + "/" + "hyp.txt", 20);
		
//		try {
//			String content = UrlUtil.getHTML(
//					"https://zh.wikipedia.org/wiki/"
//					+ URLEncoder.encode("maven", "gb2312"));
//			Document doc = Jsoup.parse(content);
//			Element div = doc.select("p").first();
//			System.out.println(HanLP.convertToSimplifiedChinese(div.text()));
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}

class HyponymyPair {
	public String hypernym;
	public String hyponym;
	
	public HyponymyPair(String word1, String word2) {
		hypernym = word1;
		hyponym = word2;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hypernym == null) ? 0 : hypernym.hashCode());
		result = prime * result + ((hyponym == null) ? 0 : hyponym.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HyponymyPair other = (HyponymyPair) obj;
		if (hypernym == null) {
			if (other.hypernym != null)
				return false;
		} else if (!hypernym.equals(other.hypernym))
			return false;
		if (hyponym == null) {
			if (other.hyponym != null)
				return false;
		} else if (!hyponym.equals(other.hyponym))
			return false;
		return true;
	}
}
