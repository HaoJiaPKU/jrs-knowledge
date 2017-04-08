package cn.edu.pku.pipeline;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.edu.pku.conf.DatabaseConf;
import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.conf.ZhilianConf;
import cn.edu.pku.filter.Combination;
import cn.edu.pku.filter.PatternOcc;
import cn.edu.pku.filter.RegularExp;
import cn.edu.pku.filter.Statistic;
import cn.edu.pku.hc.HC;
import cn.edu.pku.hyp.Hyponymy;
import cn.edu.pku.object.AbstractObj;
import cn.edu.pku.util.FileDirs;
import cn.edu.pku.util.HanLPOccurrence;
import cn.edu.pku.util.HanLPSegmenter;
import cn.edu.pku.util.TimeUtil;
import cn.edu.pku.w2v.W2V;
import cn.edu.pku.w2v.Word2Vec;
import cn.edu.pku.w2v.WordEntry;

public class ConceptPipeline {
	
	public static void main(String [] args) {
		String[] sources = {ZhilianConf.getSource()};
		String[] date = {TimeUtil.getDate(-150)};
		String[] fields = {
					"id",
					"pos_description"
					};
		int[] indices = {1};//需要分词的列
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
		String tabSeperator = "	",
				spaceSeperator = " ";
		
		FileDirs.makeDirs(FilterConf.ConceptPath);
//		//统计行业
//		AbstractObj.feildsToConf(FilterConf.ConceptPath
//				+ "/" + "industry.conf",
//				sources,
//				date,
//				"com_industry",
//				null, null);
		
		//选出数量前10位的行业
		FilterConf.readFieldFromConf(
				FilterConf.ConceptPath + "/" + "industry.conf",
				10);
		
		//创建目录
		for (int i = 0; i < FilterConf.fields.length; i ++) {
			FileDirs.makeDirs(FilterConf.ConceptPath
					+ "/" + FilterConf.fieldDirs[i]);
		}
		FileDirs.makeDirs(FilterConf.ConceptPath + "/knowledge");
		
		//For Test
		FilterConf.fieldDirs[0] = "互联网or电子商务";
		FilterConf.fields[0] = "互联网/电子商务";
		for (int i = 0; i < 1; i ++) {
			
		//对行业逐一计算
//		for (int i = 0; i < FilterConf.fieldDirs.length; i ++) {
			System.out.println(FilterConf.fieldDirs[i] + " 数据处理中...");
			
//			AbstractObj.feildsToText(
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "1.text.txt",//提取结果的存放路径
//					tabSeperator,//提取结果数据列之间的分隔符
//					sources,//数据来源
//					date,//数据时间段
//					"com_industry",//指定的key
//					FilterConf.fields[i],//指定key的value
//					fields,//要提取的数据域
//					dataSize//提取的数据量
//					);
//			System.out.println("提取数据完成");
//			
//			HanLPSegmenter.loadStopword(null);
//			RegularExp.extractRegularExpFromText(
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "1.text.txt",
//					tabSeperator,
//				 	FilterConf.ConceptPath
//				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "2.reg.text.txt",
//				 	FilterConf.ConceptPath
//				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "2.stata.hyp.txt",
//				 	tabSeperator,
//					indices);
//			System.out.println("正则匹配完成");
//			
//			HanLPSegmenter.segmentationForFeature(
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "2.reg.text.txt",
//					tabSeperator,
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "3.dup.tokens.txt",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "3.dup.pos.txt",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "3.dup.loc.txt",
//					spaceSeperator,
//					indices
//					);
//			System.out.println("分词完成");
//			
//			ConceptProcessor.removeDuplicateData(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "3.dup.tokens.txt",
//				 	FilterConf.ConceptPath
//				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "4.tmp.tokens.txt");
//			ConceptProcessor.removeDuplicateData(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "3.dup.pos.txt",
//				 	FilterConf.ConceptPath
//				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "4.tmp.pos.txt");
//			ConceptProcessor.removeDuplicateData(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "3.dup.loc.txt",
//				 	FilterConf.ConceptPath
//				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "4.tmp.loc.txt");
//			System.out.println("去重完成");
//			
//			Statistic.init();
//			Statistic.load(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "4.tmp.tokens.txt",
//					spaceSeperator,
//					30);//选出频次超过30的词汇
//			Statistic.saveToFile(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "5.stata.tokens.txt",
//					tabSeperator);
//			ConceptProcessor.removeLongTailWord(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "4.tmp.tokens.txt",
//				 	FilterConf.ConceptPath
//				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "5.tokens.txt",
//				 	false);
//			Statistic.clear();
//			
//			Statistic.init();
//			Statistic.load(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "4.tmp.pos.txt",
//					spaceSeperator,
//					20);//选出频次超过20的词汇
//			Statistic.saveToFile(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "5.stata.pos.txt", "	");
//			ConceptProcessor.removeLongTailWord(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "4.tmp.pos.txt",
//				 	FilterConf.ConceptPath
//				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "5.pos.txt",
//				 	false);
//			Statistic.clear();
//			System.out.println("统计提取完成");
//			
//			//训练w2v模型
//			W2V.run(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "5.tokens.txt",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "6.w2v.model");
//			ConceptProcessor.init();
//			ConceptProcessor.loadPosDict(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "5.stata.pos.txt",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "2.stata.hyp.txt",
//					tabSeperator);
//			ConceptProcessor.saveToFile(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "6.w2v.model",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "7.vec.w2v.txt",
//					tabSeperator);
//			ConceptProcessor.clear();
//			System.out.println("w2v训练完成");
//		
//			//层次聚类
//			HC.run(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "7.vec.w2v.txt",
//					tabSeperator,
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "8.hcluster.txt",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "8.hcluster.dis.txt",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "8.model.hc.json");

//			//提取上下位关系
//			Hyponymy hyponymy = new Hyponymy();
//			hyponymy.init(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "5.stata.pos.txt",
//					tabSeperator);
//			hyponymy.extractHyponymyPairFromHCModel(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "8.model.hc.json");
//			hyponymy.save(FilterConf.ConceptPath
//					+ "/knowledge/" + FilterConf.fieldDirs[i] + ".pos.know.json");
//			hyponymy.load(FilterConf.ConceptPath
//					+ "/knowledge/" + FilterConf.fieldDirs[i] + ".pos.know.json");
//			
//			//提取上下位关系
//			hyponymy = new Hyponymy();
//			hyponymy.init(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "2.stata.hyp.txt",
//					tabSeperator);
//			hyponymy.extractHyponymyPairFromHCModel(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "8.model.hc.json");
//			hyponymy.save(FilterConf.ConceptPath
//					+ "/knowledge/" + FilterConf.fieldDirs[i] + ".reg.know.json");
//			hyponymy.load(FilterConf.ConceptPath
//					+ "/knowledge/" + FilterConf.fieldDirs[i] + ".reg.know.json");
			
			//计算概念之间相似度
			Hyponymy hyp = new Hyponymy();
			hyp.load(FilterConf.ConceptPath
					+ "/knowledge/" + FilterConf.fieldDirs[i] + ".know.json");
			SimilarityProcessor sp = new SimilarityProcessor();
			Word2Vec w2v = new Word2Vec();
			try {
				w2v.loadJavaModel(FilterConf.ConceptPath
						+ "/" + FilterConf.fieldDirs[i] + "/" + "6.w2v.model");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sp.calculate(hyp, w2v);
			sp.save(FilterConf.ConceptPath
						+ "/" + FilterConf.fieldDirs[i] + "/" + "9.sim.json");
			
//			//测试模型反序列化
//			HC.testDeserialization(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "model.hc.json",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "hcluster.txt",
//					FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "hcluster.dis.txt");
			
			//提取上下位关系
//			Hyponymy hyponymy = new Hyponymy();
//			hyponymy.init(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.stata.txt", "	",
//				FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "token.pos.wiki.txt",
//				FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "token.pos.baidu.txt");
//			hyponymy.getExplainationFromWiki(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "token.pos.wiki.txt");
//			hyponymy.getExplainationFromBaidu(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "token.pos.baidu.txt");
//			hyponymy.analyzeHyponymyPair();
//			hyponymy.save(FilterConf.ConceptPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "hyp.txt");
//			System.out.println("上下位关系提取完成");
			
			/**
			HanLPOccurrence.extractFormTokens(
					FilterConf.ConceptPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.txt",
					FilterConf.ConceptPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.occ.txt"
					);
			HanLPOccurrence.extractFormTokens(
					FilterConf.ConceptPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt",
					FilterConf.ConceptPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.occ.txt"
					);
			System.out.println("共现关系提取完成");
			
			//对每个"熟练"等词提取
			PatternOcc.init();
			for (int j = 0; j < tokens.length; j ++) {
				PatternOcc.loadDictPos(FilterConf.ConceptPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.txt", " ");
				PatternOcc.loadDictOcc(FilterConf.ConceptPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.occ.txt", " ",
					tokens[j], thresTf[j], thresScore[j]);
				PatternOcc.getCandidate(150);
				PatternOcc.clear();
			}
			PatternOcc.saveToFile(FilterConf.ConceptPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.through.occ.txt", " ");
			System.out.println("模式提取完成");
			*/
			
			System.out.println();
		}
		
		
//		Combination.mergeFile(
//				FilterConf.ConceptPath,
//				FilterConf.fieldDirs,
//				"tokens.through.occ.txt",
//				FilterConf.ConceptPath + "/" + "all.text.txt");
//		Combination.mergeFile(
//				FilterConf.ConceptPath,
//				FilterConf.fieldDirs,
//				"tokens.txt",
//				FilterConf.ConceptPath + "/" + "all.tokens.txt");
//		Combination.mergeFile(
//				FilterConf.ConceptPath,
//				FilterConf.fieldDirs,
//				"tokens.pos.txt",
//				FilterConf.ConceptPath + "/" + "all.pos.txt");
		
		/**
		Segregator.init();
		Segregator.makeDict(FilterConf.ConceptPath + "/" + "merge.txt", " ");
		Segregator.saveToFile(FilterConf.ConceptPath + "/" + "segregat.txt", "	", 2);
		Segregator.clear();
		
		Tfidf.init();
		Tfidf.calculate(FilterConf.ConceptPath + "/" + "merge.txt", 1);
		Tfidf.saveToFile(FilterConf.ConceptPath + "/" + "result.txt", "	");
		Tfidf.saveToHtml(FilterConf.ConceptPath + "/" + "result.html", " ");
		Tfidf.clear();
		
		System.out.println("词汇归类完成");
		*/
	}
}
