package cn.edu.pku.filter;

import java.io.IOException;
import java.util.HashMap;

import cn.edu.pku.conf.DatabaseConf;
import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.conf.ZhilianConf;
import cn.edu.pku.object.AbstractObj;
import cn.edu.pku.util.FileDirs;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.util.HanLPOccurrence;
import cn.edu.pku.util.HanLPSegmenter;
import cn.edu.pku.util.TimeUtil;

public class Main {
	
	public static void main(String [] args) {
		String processingDir = "../processingDir2";
		FileDirs.makeDirs(processingDir);
		String[] industryDirs = {
					"计算机软件",
					"互联网&电子商务",
					"IT服务(系统&数据&维护)",
					"计算机硬件",
							
					"基金&证券&期货&投资",
					"保险",
					"银行",
							
					"医药&生物工程",
							
					"房地产&建筑&建材&工程",
							
					"媒体&出版&影视&文化传播",
							
					"专业服务&咨询(财会&法律&人力资源等)"
					};
		for (int i = 0; i < industryDirs.length; i ++) {
			FileDirs.makeDirs(processingDir + "/" + industryDirs[i]);
		}
		
		String[] sources = {ZhilianConf.getSource()};
		String[] date = {TimeUtil.getDate(DatabaseConf.getExpiredate())};
		String[] industries = {
					"计算机软件",
					"互联网/电子商务",
					"IT服务(系统/数据/维护)",
					"计算机硬件",
					
					"基金/证券/期货/投资",
					"保险",
					"银行",
					
					"医药/生物工程",
					
					"房地产/建筑/建材/工程",
					
					"媒体/出版/影视/文化传播",
					
					"专业服务/咨询(财会/法律/人力资源等)"
					};
		String[] fields = {
					"id",
//					"pos_title",
//					"pos_category",
					"pos_description"
					};
		int[] indices = {1};
		String [] tokens = {
//					"技术",
				
					"熟悉",
					"熟练",
					"精通",
					"了解",
					"掌握",
				
//					"java",
//					"c",
//					"c++",
//					"php",
//					"python"
					
//					"参与",
//					"负责",
//					"能够",
//					"具有",
//					"具备"
					};
		double [] thres = {
					0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
//					8.5,
//					2.5,
//					3.5,
//					8.5,
//					8.5
					};
		
		for (int i = 0; i < industries.length; i ++) {
			System.out.println(industries[i] + " 提取数据");
			AbstractObj.FeildsToText(processingDir
				 	+ "/" + industryDirs[i] + "/" + "text",
					"	",
					sources,
					date,
					new String [] {industries[i]},
					fields,
					13000
					);
			
			RegularExp.extractFromRegularExp(
					processingDir
				 	+ "/" + industryDirs[i] + "/" + "text", "	",
				 	processingDir
				 	+ "/" + industryDirs[i] + "/" + "text.reg", "	",
					indices);
			System.out.println("正则匹配结束");
			
			HanLPSegmenter.segmentation(
					processingDir
				 	+ "/" + industryDirs[i] + "/" + "text.reg", "	",
					processingDir
				 	+ "/" + industryDirs[i] + "/" + "tokens",
					processingDir
				 	+ "/" + industryDirs[i] + "/" + "tokens.pos",
					processingDir
				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.loc", " ",
					indices
					);
			System.out.println("分词结束");
			
			HanLPOccurrence.extractFormTokens(
					processingDir
				 	+ "/" + industryDirs[i] + "/" + "tokens",
					processingDir
				 	+ "/" + industryDirs[i] + "/" + "tokens.occ"
					);
			HanLPOccurrence.extractFormTokens(
					processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.pos",
					processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.pos.occ"
					);
			System.out.println("共现关系提取结束");
			
			PatternOcc.init();
			for (int j = 0; j < tokens.length; j ++) {
				PatternOcc.loadDictPos(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.pos", " ");
				PatternOcc.loadDictOcc(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.occ", " ",
					tokens[j], thres[j]);
				PatternOcc.getCandidate(200);
				PatternOcc.clear();
			}
			PatternOcc.saveToFile(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.through.occ", " ");
			
			PatternOcc.init();
			for (int j = 0; j < tokens.length; j ++) {
				PatternOcc.loadDictPos(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.pos", " ");
				PatternOcc.loadDictOcc(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.pos.occ", " ",
					tokens[j], thres[j]);
				PatternOcc.getCandidate(200);
				PatternOcc.clear();
			}
			PatternOcc.saveToFile(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.pos.through.occ", " ");
			System.out.println("计算结束");
		}
		
		FileOutput fo = new FileOutput(processingDir + "/" + "merge.dat");
		for (int i = 0; i < industries.length; i ++) {
			FileInput fi = new FileInput(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.through.occ");
			try {
				fo.t3.write(industries[i]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String line = new String();
			String content = new String ();
			try {
				while ((line = fi.reader.readLine()) != null) {
					content += " " + line.trim();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fo.t3.write(content);
				fo.t3.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fi.closeInput();
		}
		fo.closeOutput();
		
		Tfidf.init();
		Tfidf.calculate(processingDir + "/" + "merge.dat", 1);
		Tfidf.saveToFile(processingDir + "/" + "result", "	");
		Tfidf.saveToHtml(processingDir + "/" + "result.html", " ");
	}
}
