package cn.edu.pku.hyp;

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
	//带有向量的单词词典
	public HashMap<String, Cluster> wordOfCluster
			= new HashMap<String, Cluster>();
	//单词与维基百科解释
	public HashMap<String, String> wordWiki
			= new HashMap<String, String>();
	//单词与百度百科解释
	public HashMap<String, String> wordBaidu
			= new HashMap<String, String>();

	public void init(String inputPath,
			String inputSeperator,
			String wikiPath,
			String baiduPath) {
		hypDict.clear();
		wordOfCluster.clear();
		wordWiki.clear();
		wordBaidu.clear();
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
			    wordOfCluster.put(s[0], cluster);
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
		wordOfCluster.clear();
		wordWiki.clear();
		wordBaidu.clear();
	}
	
	public void getExplainationFromWiki(String outputPath) {
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
			String [] tokens = HanLPSegmenter.segmentation(candidate, true, false, null);
			System.out.println(word + " " + tokens[tokens.length - 1]);
			HyponymyObj hp = new HyponymyObj(word, tokens[tokens.length - 1], candidate);
			hypDict.add(hp);
		}
		return flag;
	}
	
	public void getExplainationFromBaidu(String outputPath) {
//		int counter = 0;
		for (String word : wordOfCluster.keySet()) {
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
	
	public void getHyponymyPair() {
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
	
	public void save (String outputPath) {
		FileOutput fo = new FileOutput(outputPath);
		ObjectMapper om = new ObjectMapper();
		try {
			fo.t3.write(om
				.writerWithDefaultPrettyPrinter()
				.writeValueAsString(this.hypDict));
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
			Hyponymy model = om.readValue(fi.reader.readLine(), Hyponymy.class);
			this.hypDict = (HashSet<HyponymyObj>) model.hypDict.clone();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void main(String [] args) {
		Hyponymy hyponymy = new Hyponymy();
		hyponymy.init(FilterConf.FeaturePath
				+ "/" + "计算机软件" + "/" + "w2v.vec.txt", "	",
			FilterConf.FeaturePath
				+ "/" + "计算机软件" + "/" + "token.pos.wiki.txt",
			FilterConf.FeaturePath
				+ "/" + "计算机软件" + "/" + "token.pos.baidu.txt");
//		hyponymy.getExplainationFromWiki(FilterConf.FeaturePath
//				+ "/" + "计算机软件" + "/" + "token.pos.wiki.txt");
//		hyponymy.getExplainationFromBaidu(FilterConf.FeaturePath
//				+ "/" + "计算机软件" + "/" + "token.pos.baidu.txt");
		
		hyponymy.getHyponymyPair();
		hyponymy.save(FilterConf.FeaturePath
				+ "/" + "计算机软件" + "/" + "hyp.txt");
	}
}
