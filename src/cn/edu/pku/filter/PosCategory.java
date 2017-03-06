package cn.edu.pku.filter;

import java.io.IOException;
import java.util.HashMap;

import cn.edu.pku.conf.DatabaseConf;
import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.conf.ZhilianConf;
import cn.edu.pku.object.AbstractObj;
import cn.edu.pku.pipeline.FeatureProcessor;
import cn.edu.pku.util.FileDirs;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.util.HanLPOccurrence;
import cn.edu.pku.util.HanLPSegmenter;
import cn.edu.pku.util.TimeUtil;

public class PosCategory {
	
	public static void main(String [] args) {
		FileDirs.makeDirs(FilterConf.FeaturePath);
		FilterConf.readFieldFromConf(
				FilterConf.FeaturePath + "/" + "category.conf",
				100);
		
		for (int i = 0; i < FilterConf.fields.length; i ++) {
			FileDirs.makeDirs(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i]);
		}
		
		String[] sources = {ZhilianConf.getSource()};
		String[] date = {TimeUtil.getDate(DatabaseConf.getExpiredate())};
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
					};
		
		for (int i = 0; i < FilterConf.fields.length; i ++) {
			System.out.println(FilterConf.fields[i] + " 数据处理中...");
			AbstractObj.feildsToText(
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.txt",
					"	",
					sources,
					date,
					"pos_category",
					FilterConf.fields[i],
					fields,
					15000
					);
			System.out.println("提取数据结束");
			
			RegularExp.extractRegularExpFromText(
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.txt", "	",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "text.reg.txt", "	",
					indices);
			System.out.println("正则匹配结束");
			
			HanLPSegmenter.segmentationForFeature(
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.reg.txt", "	",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.dup",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.dup",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.loc.dup", " ",
					indices
					);
			System.out.println("分词结束");
			
			FeatureProcessor.removeDuplicateData(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.dup",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.tmp");
			FeatureProcessor.removeDuplicateData(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.dup",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.tmp");
			FeatureProcessor.removeDuplicateData(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.loc.dup",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.loc.tmp");
			System.out.println("去重结束");
			
			Statistic.init();
			Statistic.load(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.tmp", " ", 20);
			Statistic.saveToFile(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.stata.txt", "	");
			FeatureProcessor.removeLongTailWord(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.tmp",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.txt",
				 	false);
			Statistic.clear();
			
			Statistic.init();
			Statistic.load(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.tmp", " ", 20);
			Statistic.saveToFile(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.stata.txt", "	");
			FeatureProcessor.removeLongTailWord(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.tmp",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt",
				 	false);
			Statistic.clear();
			System.out.println("统计提取结束");
			
			HanLPOccurrence.extractFormTokens(
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.txt",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.occ.txt"
					);
			HanLPOccurrence.extractFormTokens(
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.occ.txt"
					);
			System.out.println("共现关系提取结束");
			
			//对每个“熟练”等词提取
			PatternOcc.init();
			for (int j = 0; j < tokens.length; j ++) {
				PatternOcc.loadDictPos(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt", " ");
				PatternOcc.loadDictOcc(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.occ.txt", " ",
					tokens[j], thresTf[j], thresScore[j]);
				PatternOcc.getCandidate(150);
				PatternOcc.clear();
			}
			PatternOcc.saveToFile(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.through.occ.txt", " ");
			
			//对每个“熟练”等词提取
			PatternOcc.init();
			for (int j = 0; j < tokens.length; j ++) {
				PatternOcc.loadDictPos(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt", " ");
				PatternOcc.loadDictOcc(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.occ.txt", " ",
					tokens[j], thresTf[j], thresScore[j]);
				PatternOcc.getCandidate(200);
				PatternOcc.clear();
			}
			PatternOcc.saveToFile(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.through.occ.txt", " ");
			System.out.println("模式提取结束");
			
		}
		
		Combination.mergeFile(
				FilterConf.FeaturePath,
				FilterConf.fieldDirs,
				"tokens.through.occ.txt",
				FilterConf.FeaturePath + "/" + "merge.dat");

		Segregator.init();
		Segregator.makeDict(FilterConf.FeaturePath + "/" + "merge.dat", " ");
		Segregator.saveToFile(FilterConf.FeaturePath + "/" + "segregat.txt", "	", 2);
		Segregator.clear();
		
		Tfidf.init();
		Tfidf.calculate(FilterConf.FeaturePath + "/" + "merge.dat", 1);
		Tfidf.saveToFile(FilterConf.FeaturePath + "/" + "result.txt", "	");
		Tfidf.saveToHtml(FilterConf.FeaturePath + "/" + "result.html", " ");
		Tfidf.clear();
		
		System.out.println("词汇归类结束");
	}
}
