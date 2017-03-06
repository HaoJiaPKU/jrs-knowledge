package cn.edu.pku.pipeline;

import java.io.IOException;
import java.util.Set;

import cn.edu.pku.conf.DatabaseConf;
import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.conf.ZhilianConf;
import cn.edu.pku.filter.Combination;
import cn.edu.pku.filter.PatternOcc;
import cn.edu.pku.filter.RegularExp;
import cn.edu.pku.filter.Statistic;
import cn.edu.pku.hc.HC;
import cn.edu.pku.object.AbstractObj;
import cn.edu.pku.util.FileDirs;
import cn.edu.pku.util.HanLPOccurrence;
import cn.edu.pku.util.HanLPSegmenter;
import cn.edu.pku.util.TimeUtil;
import cn.edu.pku.w2v.W2V;
import cn.edu.pku.w2v.Word2Vec;
import cn.edu.pku.w2v.WordEntry;

public class FeaturePipeline {
	
	public static void main(String [] args) {
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
		int dataSize = 20000;
		
		FileDirs.makeDirs(FilterConf.FeaturePath);
		//统计行业
		AbstractObj.feildsToConf(FilterConf.FeaturePath
				+ "/" + "industry.conf",
				sources,
				date,
				"com_industry",
				null, null);
		
		//选出数量前10位的行业
		FilterConf.readFieldFromConf(
				FilterConf.FeaturePath + "/" + "industry.conf",
				10);
		
		//创建目录
		for (int i = 0; i < FilterConf.fields.length; i ++) {
			FileDirs.makeDirs(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i]);
		}
		
		//For Test
//		FilterConf.fieldDirs[0] = "计算机软件";
//		FilterConf.fields[0] = "计算机软件";
		
		//对行业逐一计算
		for (int i = 0; i < FilterConf.fieldDirs.length; i ++) {
			System.out.println(FilterConf.fieldDirs[i] + " 数据处理中...");
			
			AbstractObj.feildsToText(
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.txt",
					"	",
					sources,
					date,
					"com_industry",
					FilterConf.fields[i],
					fields,
					dataSize
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
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.txt", "	",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.dup.txt",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.dup.txt",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.loc.dup.txt", " ",
					indices
					);
			System.out.println("分词结束");
			
			FeatureProcessor.removeDuplicateData(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.dup.txt",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.tmp.txt");
			FeatureProcessor.removeDuplicateData(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.dup.txt",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.tmp.txt");
			FeatureProcessor.removeDuplicateData(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.loc.dup.txt",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.loc.tmp.txt");
			System.out.println("去重结束");
			
			Statistic.init();
			Statistic.load(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.tmp.txt", " ", 30);
			Statistic.saveToFile(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.stata.txt", "	");
			FeatureProcessor.removeLongTailWord(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.tmp.txt",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.txt",
				 	false);
			Statistic.clear();
			
			Statistic.init();
			Statistic.load(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.tmp.txt", " ", 20);
			Statistic.saveToFile(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.stata.txt", "	");
			FeatureProcessor.removeLongTailWord(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.tmp.txt",
				 	FilterConf.FeaturePath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt",
				 	false);
			Statistic.clear();
			System.out.println("统计提取结束");
			
			/**
//			HanLPOccurrence.extractFormTokens(
//					FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.txt",
//					FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.occ.txt"
//					);
//			HanLPOccurrence.extractFormTokens(
//					FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt",
//					FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.occ.txt"
//					);
//			System.out.println("共现关系提取结束");
//			
//			//对每个"熟练"等词提取
//			PatternOcc.init();
//			for (int j = 0; j < tokens.length; j ++) {
//				PatternOcc.loadDictPos(FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt", " ");
//				PatternOcc.loadDictOcc(FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.occ.txt", " ",
//					tokens[j], thresTf[j], thresScore[j]);
//				PatternOcc.getCandidate(150);
//				PatternOcc.clear();
//			}
//			PatternOcc.saveToFile(FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.through.occ.txt", " ");
//			System.out.println("模式提取结束");
			*/
			
			//训练w2v模型
			W2V.run(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.txt",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "w2v.model");
			FeatureProcessor.init();
			FeatureProcessor.loadDictPos(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt", " ");
			FeatureProcessor.saveToFile(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "w2v.model",
					FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "w2v.vec.txt", "	");
			FeatureProcessor.clear();
			System.out.println("w2v训练结束");
			
			Word2Vec w2v = new Word2Vec();
			try {
				w2v.loadJavaModel(FilterConf.FeaturePath
						+ "/" + FilterConf.fieldDirs[i] + "/" + "w2v.model");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			//层次聚类
//			HC.run(FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "w2v.vec.txt", "	",
//					FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/",
//					FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "hc.model.json");
			
//			//测试模型反序列化
//			HC.testDeserialization(FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "model.json",
//					FilterConf.FeaturePath
//					+ "/" + FilterConf.fieldDirs[i] + "/");
			
			System.out.println();
		}
		
		
//		Combination.mergeFile(
//				FilterConf.FeaturePath,
//				FilterConf.fieldDirs,
//				"tokens.through.occ.txt",
//				FilterConf.FeaturePath + "/" + "all.txt");
//		Combination.mergeFile(
//				FilterConf.FeaturePath,
//				FilterConf.fieldDirs,
//				"tokens.txt",
//				FilterConf.FeaturePath + "/" + "all.tokens.txt");
//		Combination.mergeFile(
//				FilterConf.FeaturePath,
//				FilterConf.fieldDirs,
//				"tokens.pos.txt",
//				FilterConf.FeaturePath + "/" + "all.tokens.pos.txt");
		
		/**
		Segregator.init();
		Segregator.makeDict(FilterConf.FeaturePath + "/" + "merge.txt", " ");
		Segregator.saveToFile(FilterConf.FeaturePath + "/" + "segregat.txt", "	", 2);
		Segregator.clear();
		
		Tfidf.init();
		Tfidf.calculate(FilterConf.FeaturePath + "/" + "merge.txt", 1);
		Tfidf.saveToFile(FilterConf.FeaturePath + "/" + "result.txt", "	");
		Tfidf.saveToHtml(FilterConf.FeaturePath + "/" + "result.html", " ");
		Tfidf.clear();
		
		System.out.println("词汇归类结束");
		*/
	}
}
