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
		
		FileDirs.makeDirs(FilterConf.GBDTPath);
		//统计行业
		AbstractObj.feildsToConf(FilterConf.GBDTPath
				+ "/" + "industry.conf",
				sources,
				date,
				"com_industry",
				null,
				null);
		
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
//		FilterConf.fieldDirs[0] = "计算机软件";
//		FilterConf.fields[0] = "计算机软件";
		
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
			System.out.println("提取数据结束");
			
			RegularExp.extractRegularExpFromText(
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.txt", seperator,
				 	FilterConf.GBDTPath
				 	+ "/" + FilterConf.fieldDirs[i] + "/" + "text.reg.txt", seperator,
					indices);
			System.out.println("正则匹配结束");
			
			HanLPSegmenter.segmentationForGBDT(
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "text.reg.txt", seperator,
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.dup.txt", seperator
					);
			System.out.println("分词结束");
			
			GBDTProcessor.removeDuplicateData(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.dup.txt",
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.tmp.txt");
			System.out.println("去重结束");
			
			GBDTProcessor.init();
			GBDTProcessor.loadFeatureAsDict(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.stata.txt", "	", Integer.MAX_VALUE);
			GBDTProcessor.extractFeature(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.tmp.txt", seperator,
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.txt", seperator);
			System.out.println("提取特征结束");
			
			GBDTProcessor.loadFeatureAsArray(FilterConf.FeaturePath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.pos.stata.txt", "	", 100);//特征数量
			GBDTProcessor.textToVector(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "tokens.txt", seperator,
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "vec.csv",
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "position.num.stata.txt", 10);//15个类
			System.out.println("向量表示结束");
			
			//GBDT训练
			GBDT.run(FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "vec.csv",
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/" + "status.txt",
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/gbdt.model/"
							+ FilterConf.fieldDirs[i] + ".gbdt.modelUI.json",
					FilterConf.GBDTPath
					+ "/" + FilterConf.fieldDirs[i] + "/gbdt.model/"
							+ FilterConf.fieldDirs[i] + ".gbdt.model.json",
					20,//迭代次数
					0.5,
					0.1,
					3,
					-1);
			System.out.println("GBDT训练结束");
			
//			//测试模型反序列化
//			GBDT.testDeserialization(FilterConf.GBDTPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "vec.csv",
//					FilterConf.GBDTPath
//					+ "/" + FilterConf.fieldDirs[i] + "/" + "model.json");
			
			System.out.println();
		}
	}
}
