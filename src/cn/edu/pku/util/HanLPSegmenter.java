/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/7 20:14</create-date>
 *
 * <copyright file="DemoPosTagging.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package cn.edu.pku.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dependency.CRFDependencyParser;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;

import cn.edu.pku.conf.FilePathConf;
import cn.edu.pku.filter.Statistic;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class HanLPSegmenter
{
	/** 停用词、停用符号表 */
	public static HashSet<String> stopword = new HashSet<String> ();
	/** 停用词、停用符号文件 */
	public static String stopwordsFile = FilePathConf.stopwordsFile;
	/** 停用符号 */
	public static final String StopSigns = "[\\p{P}~$`^=|<>～｀＄＾＋＝｜＜＞￥× \\s|\t|\r|\n]+";
	/** 停用模式 */
	public static Pattern pattern = Pattern.compile("[a-b]|[d-q]|[s-z]");
	
	/** 
	 * 加载停用词、停用符号表
	 * @param stopWordsFilePath 停用词表文件路径
	 * @throws IOException 找不到停用词、停用符号文件
	 */
	public static void loadStopword(String _stopwordsFile) {
		if(_stopwordsFile != null)
			stopwordsFile = _stopwordsFile;
		stopword.clear();
		FileInput fi = new FileInput(stopwordsFile);
		String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				stopword.add(line.trim());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	/** 
	 * 停用词、停用符号判断
	 * @param token 待识别的词素
	 * @return true表示该词素为停用词或停用符号，false表示该词素不是停用词或停用符号
	 */
	public static boolean isStopWords(String token)
	{
		if(stopword.contains(token))
			return true;
		return false;
	}
	
	/** 
	 * 将字母的大写形式转化为小写形式，如果不包含大写字母返回原值，否则返回小写形式
	 * @param token 待转化的词素
	 * @return 转化后的词素
	 */
	public static String lowerCase(String token)
	{
		if(Pattern.compile("(?i)[A-Z]").matcher(token).find())
			return token.toLowerCase();
		return token;
	}
	
	/** 
	 * 去除停用词预处理，保留c++，.net，c#，sqlserver等特殊词素
	 * @param textContent 待分词的文本
	 * @return 处理后的文本
	 */
	public static String preStopWord(String textContent)
	{
		textContent = textContent.replaceAll("c#", "+++++");
		textContent = textContent.replaceAll("C#", "+++++");
		textContent = textContent.replaceAll(StopSigns, " ");
		textContent = textContent.replaceAll("[+][+][+][+][+]", "c# ");
		textContent = textContent.replaceAll("c[+][+]", "@pattern1@");
		textContent = textContent.replaceAll("C[+][+]", "@pattern1@");
		textContent = textContent.replaceAll(".net", "@pattern2@");
		textContent = textContent.replaceAll(".Net", "@pattern2@");
		textContent = textContent.replaceAll(".NET", "@pattern2@");
		textContent = textContent.replaceAll("div[+]css", "@pattern3@");
		textContent = textContent.replaceAll("css[+]div", "@pattern3@");
		textContent = textContent.replaceAll("DIV[+]CSS", "@pattern3@");
		textContent = textContent.replaceAll("CSS[+]DIV", "@pattern3@");
		textContent = textContent.replaceAll("Div[+]Css", "@pattern3@");
		textContent = textContent.replaceAll("Css[+]Div", "@pattern3@");
		textContent = textContent.replaceAll("[+]", " ");
		textContent = textContent.replaceAll("@pattern1@", "cpp ");
		textContent = textContent.replaceAll("@pattern2@", ".net");
		textContent = textContent.replaceAll("@pattern3@", "div+css");
		textContent = textContent.replaceAll("sql server", "sqlserver");
		textContent = textContent.replaceAll("Sql server", "sqlserver");
		textContent = textContent.replaceAll("Sql Server", "sqlserver");
		textContent = textContent.replaceAll("sql Server", "sqlserver");
		textContent = textContent.replaceAll("SQL server", "sqlserver");
		textContent = textContent.replaceAll("SQL Server", "sqlserver");
		textContent = textContent.replaceAll("c #", "c#");
		return textContent;
	}
	
	/**
	 * 将特殊词汇进行替换
	 * */
	public static String replaceToken(String token) {
		if(token.equals("net")) {
			token = ".net";
		}
		if(token.equals("cpp")) {
			token = "c++";
		}
		if(token.equals("ccpp")) {
			token = "c c++";
		}
		if(token.equals("vcpp") || token.equals("visualcpp")) {
			token = "visualc++";
		}
		if(token.equals("pythoncpp")) {
			token = "python c++";
		}
		if(token.equals("qtcpp")) {
			token = "qt c++";
		}
		if(token.equals("javaccpp")) {
			token = "java c c++";
		}
		if (token.equals("j2") || token.equals("ee") || token.equals("javaee")) {
			token = "j2ee";
		}
		if (token.equals("se") || token.equals("javase")) {
			token = "j2se";
		}
		if (token.equals("me") || token.equals("javame")) {
			token = "j2me";
		}
		if (token.equals("es")) {
			token = "elasticsearch";
		}
		if (token.equals("js") || token.equals("javascrip")) {
			token = "javascript";
		}
		if (token.equals("go")) {
			token = "golang";
		}
		if(token.equals("reduce")) {
			token = "mapreduce";
		}
		if(token.equals("map")) {
			return "";
		}
		if(token.equals("rest")) {
			return "restful";
		}
		if (token.indexOf("js") == token.length() - 2
				&& token.length() > 2 ) {
			token = token.substring(0, token.length() - 2);
		}
		return token;
	}
	
	/**
	 * 对token进行处理
	 * 如果不包含大写字母返回原值，否则返回小写形式
	 * 去除c以外的其他单个字母和汉字
	 * 去除完全是数字的词
	 * 去除停用词和网址等特殊词
	 * @param token
	 * @return 如果出现以上各种情况，返回null，否则返回处理后的token
	 * */
	public static String normalizedToken(String token) {
		//如果不包含大写字母返回原值，否则返回小写形式
		token = lowerCase(token.trim());
			
		//去除c以外的其他单个字母和汉字
		if(token.length() == 1 && !token.equals("c") && !token.equals("r")) {
			return null;
		}
		
		//去除完全是数字的词
		if(token.matches("[0-9]+")) {
			return null;
		}
		
		token = replaceToken(token);
		
		
		token = token.replaceAll("\r", "");
		token = token.replaceAll("\n", "");
		token = token.replaceAll("\t", "");
		token = token.trim();
		
		//去除停用词和网址等特殊词
		if(isStopWords(token)
				|| token.length() == 0
				|| token.contains("-")
				|| token.contains("@")
				|| token.contains("COM")
				|| token.contains("com")
				|| token.contains("CN")
				|| token.contains("cn")
				|| token.contains("WWW")
				|| token.contains("www")) {
			return null;
		}
		
		if (token.equals("职位")
				|| token.equals("职业")
				|| token.equals("岗位")
				|| token.equals("职责")
				|| token.equals("描述")
				|| token.equals("要求")
				|| token.equals("专业")
				|| token.equals("毕业生")
				|| token.equals("学历")
				|| token.equals("本科")
				|| token.equals("专科")
				|| token.equals("能力")
				|| token.equals("基础")
				) {
			return null;
		}
		return token;
	}	
	
	/**
	 * 判断是否为给定词性
	 * @param token
	 * */
	public static boolean isGivenPos(String token) {
		return token.equals("gi")
				|| token.equals("n")
				|| token.equals("ng")
				|| token.equals("nx");
	}
	
	/**
	 * 对文本进行分词
	 * */
	public static String[] segmentation(
			String content,
			boolean isNormalized,
			boolean isSave,
			String outputPath) {
		
		FileOutput fo = new FileOutput();
		if (isSave) {
			fo = new FileOutput(outputPath);
		}
		
		Segment segmenter = HanLP.newSegment();
		segmenter.enablePartOfSpeechTagging(true);	
		
		content = preStopWord(content.trim());
		List<Term> termList = segmenter.seg(content);
		
		String [] temp = new String[termList.size()];
		int index = 0;
		Iterator it = termList.iterator();
		while (it.hasNext()) {
			String [] term = it.next().toString().split("/");
			if (isNormalized) {
				term[0] = normalizedToken(term[0]);
				if (term[0] == null
						|| term[0].length() == 0
						|| term[0].equals("")) {
					continue;
				}
			}
			temp[index ++] = term[0];
		}
		
		String [] ret = new String[index];
		for (int i = 0; i < index; i ++) {
			ret[i] = temp[i];
			if (isSave) {
				try {
					fo.t3.write(ret[i] + " ");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (isSave) {
			fo.closeOutput();
		}
		
		return ret;
	}
	
	/**
	 * 对文本进行分词
	 * @param inputPath 输入文件路径
	 * @param outputPath 输出文件路径
	 * @param indices 需要分词的域的索引
	 * */
	public static void segmentationForFeature(
			String inputPath,
			String inputSeperator,
			String outputPathTFIDF,
			String outputPathPos,
			String outputPathLoc,
			String outputSeperator,
			int[] indices) {
		if (indices == null || indices.length == 0) {
			System.out.println("info : no indices specified");
			return;
		}
		
		FileInput fi = new FileInput(inputPath);
		FileOutput foToken = new FileOutput(outputPathTFIDF);
		FileOutput foPos = new FileOutput(outputPathPos);
		FileOutput foLoc = new FileOutput(outputPathLoc);
		loadStopword(null);
		
		Segment segmenter = HanLP.newSegment();
		segmenter.enablePartOfSpeechTagging(true);	
		
		int counter = 0;
		String line = new String();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String content = new String();
				String [] fields = line.split(inputSeperator);
				if (fields.length - 1 < indices[indices.length - 1]) {
					continue;
				}
				
				foToken.t3.write(fields[0] + outputSeperator);
				foPos.t3.write(fields[0] + outputSeperator);
				foLoc.t3.write(fields[0] + outputSeperator);
				
				boolean [] flag = new boolean [fields.length];
				for (int i = 1; i < fields.length; i ++) {
					flag[i] = false;
				}
				for (int i = 0; i < indices.length; i ++) {
					flag[indices[i]] = true;
				}
				for (int i = 1; i < flag.length; i ++) {
					if (!flag[i]) {
						foToken.t3.write(fields[i] + outputSeperator);
					}
				}
				
				for (int i = 1; i < fields.length; i ++) {
					if (fields[i] == null
							|| fields[i].length() == 0
							|| fields[i].equals("")) {
						continue;
					}
					if (flag[i]) {
						content += fields[i];
					}
				}
				
				content = preStopWord(content.trim());
				List<Term> termList = segmenter.seg(content);
				
				ArrayList<String> token = new ArrayList<String>();
				ArrayList<String> pos = new ArrayList<String>();
				
				Iterator it = termList.iterator();
				while (it.hasNext()) {
					String [] term = it.next().toString().split("/");
					term[0] = normalizedToken(term[0]);
					if (term[0] == null
							|| term[0].length() == 0
							|| term[0].equals("")) {
						continue;
					}
					token.add(term[0]);
					pos.add(term[1]);
				}
				
				flag = new boolean[token.size()];
				for (int i = 0; i < flag.length; i ++) {
					flag[i] = false;
				}
				
				for (int i = 0; i < token.size(); i ++) {
					if (pos.get(i).equals("gi")) {
						flag[i] = true;
						continue;
					}
					if (pos.get(i).equals("v")) {
						if (i + 1 < token.size()
								&& token.get(i + 1) != null
								&& isGivenPos(pos.get(i + 1))
								) {
							while (i + 1 < token.size()
									&& token.get(i + 1) != null
									&& isGivenPos(pos.get(i + 1))
									) {
								flag[i + 1] = true;
								i ++;
							}
						} else if (i + 2 < token.size()
								&& token.get(i + 2) != null
								&& isGivenPos(pos.get(i + 2))
								) {
							while (i + 2 < token.size()
									&& token.get(i + 2) != null
									&& isGivenPos(pos.get(i + 2))
									) {
								flag[i + 2] = true;
								i ++;
							}
						} else if (i + 3 < token.size()
								&& token.get(i + 3) != null
								&& isGivenPos(pos.get(i + 3))
								) {
							while (i + 3 < token.size()
									&& token.get(i + 3) != null
									&& isGivenPos(pos.get(i + 3))
									) {
								flag[i + 3] = true;
								i ++;
							}
						}
					}
				}
				
				for (int i = 0; i < token.size(); i ++) {
					foToken.t3.write(token.get(i) + outputSeperator);
					if (flag[i]) {
						foPos.t3.write(token.get(i) + outputSeperator);
						foLoc.t3.write(token.get(i) 
								+ "/" + String.valueOf(i) + outputSeperator);
					}
				}
				
				foToken.t3.newLine();
				foPos.t3.newLine();
				foLoc.t3.newLine();
				
				if (++ counter % 1000 == 0) {
					System.out.println(counter + " results");
				}
			}
			System.out.println(counter + " results");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		foToken.closeOutput();
		foPos.closeOutput();
		foLoc.closeOutput();
		fi.closeInput();
	}
	
	/**
	 * 对文本进行分词
	 * @param inputPath 输入文件路径
	 * @param outputPath 输出文件路径
	 * @param indices 需要分词的域的索引
	 * */
	public static void segmentationForGBDT(
			String inputPath,
			String inputSeperator,
			String outputPath,
			String outputSeperator) {		
		FileInput fi = new FileInput(inputPath);
		FileOutput fo = new FileOutput(outputPath);
		loadStopword(null);
		
		Segment segmenter = HanLP.newSegment();
		segmenter.enablePartOfSpeechTagging(true);	
		
		int counter = 0;
		String line = new String();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String content = new String();
				String [] fields = line.split(inputSeperator);
				
				boolean flag = true;
				for (int i = 0; i < fields.length; i ++) {
					if (fields[i] == null
							|| fields[i].length() == 0
							|| fields[i].equals("")) {
						flag = false;
						break;
					}
				}
				if (!flag) {
					continue;
				}
				
				for (int i = 0; i < fields.length; i ++) {
					content = preStopWord(fields[i].trim());
					List<Term> termList = segmenter.seg(content);
					
					Iterator it = termList.iterator();
					while (it.hasNext()) {
						String [] term = it.next().toString().split("/");
						term[0] = normalizedToken(term[0]);
						if (term[0] == null
								|| term[0].length() == 0
								|| term[0].equals("")) {
							continue;
						}
						fo.t3.write(term[0] + " ");
					}
					if (i != fields.length - 1) {
						fo.t3.write(outputSeperator);
					}
				}
				fo.t3.newLine();
				
				if (++ counter % 1000 == 0) {
					System.out.println(counter + " results");
				}
			}
			System.out.println(counter + " results");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fo.closeOutput();
		fi.closeInput();
	}

	public static void main(String [] args) {
		System.out.println(
				CRFDependencyParser
				.compute("了解Jquery等Javascript开发架构和框架"));
	}
}
