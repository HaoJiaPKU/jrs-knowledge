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

public class IndustryFilter {
	
	public static void main(String [] args) {
		String processingDir = "../processingDir";
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
					"pos_description"
					};
		int[] indices = {1};
		String [] tokens = {
					"熟练掌握",
					"熟悉",
					"熟练",
					"精通",
					"了解",
					"掌握",
					"使用",
					"运用",
					
					"参与",
					"负责",
					"能够",
					"具有",
					"具备"
					};
		int [] thresTf = {
					2, 2, 2, 2, 2, 2, 
					2, 2, 2, 2, 2, 2,
					2, 2, 2, 2, 2, 2,
							};
		double [] thresScore = {
					-100, -100, -100, -100, -100, -100,
					-100, -100, -100, -100, -100, -100,
					-100, -100, -100, -100, -100, -100,
//					8.5,
//					2.5,
//					3.5,
//					8.5,
//					8.5
					};
		
		for (int i = 0; i < industries.length; i ++) {
			System.out.println(industries[i] + " 数据处理中...");
//			AbstractObj.feildsToText(processingDir
//				 	+ "/" + industryDirs[i] + "/" + "text.txt",
//					"	",
//					sources,
//					date,
//					new String [] {industries[i]},
//					fields,
//					15000
//					);
//			System.out.println("提取数据结束");
			
//			RegularExp.extractFromRegularExp(
//					processingDir
//				 	+ "/" + industryDirs[i] + "/" + "text.txt", "	",
//				 	processingDir
//				 	+ "/" + industryDirs[i] + "/" + "text.reg.txt", "	",
//					indices);
//			System.out.println("正则匹配结束");
			
//			HanLPSegmenter.segmentation(
//					processingDir
//				 	+ "/" + industryDirs[i] + "/" + "text.reg.txt", "	",
//					processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.dup",
//					processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.dup",
//					processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.loc.dup", " ",
//					indices
//					);
//			System.out.println("分词结束");
//			
//			HanLPSegmenter.removeDuplicateData(processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.dup",
//				 	processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.tmp");
//			HanLPSegmenter.removeDuplicateData(processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.dup",
//				 	processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.tmp");
//			HanLPSegmenter.removeDuplicateData(processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.loc.dup",
//				 	processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.loc.tmp");
//			System.out.println("去重结束");
			
//			Statistic.init();
//			Statistic.load(processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.tmp", " ", 20);
//			Statistic.saveToFile(processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.stata.txt", "	");
//			HanLPSegmenter.removeLongTailWord(processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.tmp",
//				 	processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.txt",
//				 	false);
//			Statistic.clear();
//			
//			Statistic.init();
//			Statistic.load(processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.pos.tmp", " ", 20);
//			Statistic.saveToFile(processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.pos.stata.txt", "	");
//			HanLPSegmenter.removeLongTailWord(processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.tmp",
//				 	processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.pos.txt",
//				 	false);
//			Statistic.clear();
//			System.out.println("统计提取结束");
			
//			HanLPOccurrence.extractFormTokens(
//					processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.txt",
//					processingDir
//				 	+ "/" + industryDirs[i] + "/" + "tokens.occ.txt"
//					);
//			HanLPOccurrence.extractFormTokens(
//					processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.pos.txt",
//					processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.pos.occ.txt"
//					);
//			System.out.println("共现关系提取结束");
			
			//对每个“熟练”等词提取
			PatternOcc.init();
			for (int j = 0; j < tokens.length; j ++) {
				PatternOcc.loadDictPos(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.pos.txt", " ");
				PatternOcc.loadDictOcc(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.occ.txt", " ",
					tokens[j], thresTf[j], thresScore[j]);
				PatternOcc.getCandidate(150);
				PatternOcc.clear();
			}
			PatternOcc.saveToFile(processingDir
					+ "/" + industryDirs[i] + "/" + "tokens.through.occ.txt", " ");
			
//			//对每个“熟练”等词提取
//			PatternOcc.init();
//			for (int j = 0; j < tokens.length; j ++) {
//				PatternOcc.loadDictPos(processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.pos.txt", " ");
//				PatternOcc.loadDictOcc(processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.pos.occ.txt", " ",
//					tokens[j], thresTf[j], thresScore[j]);
//				PatternOcc.getCandidate(200);
//				PatternOcc.clear();
//			}
//			PatternOcc.saveToFile(processingDir
//					+ "/" + industryDirs[i] + "/" + "tokens.pos.through.occ.txt", " ");
			System.out.println("模式提取结束");
			
		}
		
		Combination.mergeFile(processingDir, industries, industryDirs,
				"tokens.through.occ.txt", processingDir + "/" + "merge.dat");

		Segregator.init();
		Segregator.makeDict(processingDir + "/" + "merge.dat", " ");
		Segregator.saveToFile(processingDir + "/" + "segregat.txt", "	", 3);
		Segregator.clear();
		
		Tfidf.init();
		Tfidf.calculate(processingDir + "/" + "merge.dat", 1);
		Tfidf.saveToFile(processingDir + "/" + "result.txt", "	");
		Tfidf.saveToHtml(processingDir + "/" + "result.html", " ");
		Tfidf.clear();
		
		System.out.println("行业词汇归类结束");
	}
}
