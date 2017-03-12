package cn.edu.pku.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import cn.edu.pku.conf.DatabaseConf;
import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.conf.ZhilianConf;
import cn.edu.pku.filter.RegularExp;
import cn.edu.pku.gbdt.DataSet;
import cn.edu.pku.gbdt.GBDT;
import cn.edu.pku.gbdt.Model;
import cn.edu.pku.gbdt.Tree;
import cn.edu.pku.gbdt.Util;
import cn.edu.pku.object.AbstractObj;
import cn.edu.pku.util.FileDirs;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.util.HanLPSegmenter;
import cn.edu.pku.util.TimeUtil;


public class GBDTPipeline {
	
	public static void main(String [] args) {
		String[] sources = {ZhilianConf.getSource()};
		String[] date = {TimeUtil.getDate(DatabaseConf.getExpiredate())};
		String[] fields = {
					"pos_title",
					"pos_description"
					};
		int[] indices = {1};
		int dataSize = 20000;
		String seperator = "@@@@@@";
		String tabSeperator = "	";
		
		FileDirs.makeDirs(FilterConf.GBDTPath);
		//统计行业
//		AbstractObj.feildsToConf(FilterConf.GBDTPath
//				+ "/" + "industry.conf",
//				sources,
//				date,
//				"com_industry",
//				null,
//				null);
		
		//选出数量前10位的行业
		FilterConf.readFieldFromConf(
				FilterConf.GBDTPath + "/" + "industry.conf",
				10);
		
		//创建目录
		for (int i = 0; i < FilterConf.fields.length; i ++) {
			FileDirs.makeDirs(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i]);
		}
		FileDirs.makeDirs(FilterConf.GBDTPath + "/gbdt.model");
		
		//For Test
//		FilterConf.fieldDirs[0] = "互联网or电子商务";
//		FilterConf.fields[0] = "互联网/电子商务";
		
		//对行业逐一计算
		for (int i = 0; i < FilterConf.fieldDirs.length; i ++) {
			System.out.println(FilterConf.fieldDirs[i] + " 数据处理中...");
			
			AbstractObj.feildsToText(
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.txt",
					"@@@@@@",
					sources,
					date,
					"com_industry",
					FilterConf.fields[i],
					fields,
					dataSize
					);
			System.out.println("提取数据完成");
			
			HanLPSegmenter.loadStopword(null);
			RegularExp.extractRegularExpFromText(
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.txt",
					seperator,
				 	FilterConf.GBDTPath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "reg.text.txt",
				 	FilterConf.GBDTPath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "stata.hyp.txt",
				 	seperator,
					indices);
			System.out.println("正则匹配完成");
			
			HanLPSegmenter.segmentationForGBDT(
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "reg.text.txt",
					seperator,
					true,
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "dup.tokens.txt",
					seperator
					);
			System.out.println("分词完成");
			
			GBDTProcessor.removeDuplicateData(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "dup.tokens.txt",
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tmp.tokens.txt");
			System.out.println("去重完成");
			
			GBDTProcessor.init();
			GBDTProcessor.loadFeatureAsDict(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "stata.pos.txt",
					tabSeperator,
					Integer.MAX_VALUE);
			GBDTProcessor.extractFeature(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tmp.tokens.txt",
					seperator,
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "pos.txt",
					seperator);
			System.out.println("提取特征完成");
			
			GBDTProcessor.loadFeatureAsArray(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "stata.pos.txt",
					tabSeperator,
					100);//特征数量
			GBDTProcessor.textToVector(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "pos.txt",
					seperator,
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "vec.csv",
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "stata.position.num.txt",
					15);//15个类
			System.out.println("向量表示完成");
			
			//GBDT训练
			GBDT.run(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "vec.csv",
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "gbdt.status.txt",
					FilterConf.GBDTPath + "/gbdt.model/"
							+ FilterConf.fieldDirs[i] + ".gbdt.modelUI.json",
					FilterConf.GBDTPath + "/gbdt.model/"
							+ FilterConf.fieldDirs[i] + ".gbdt.model.json",
					20,//迭代次数
					0.5,//采样率
					0.1,//学习速率
					3,//决策树深度
					-1);
			System.out.println("GBDT训练完成");
			
//			//测试模型反序列化
//			GBDT.testDeserialization(FilterConf.GBDTPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "vec.csv",
//					FilterConf.GBDTPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "model.json");
			
			System.out.println();
		}
	}
}
