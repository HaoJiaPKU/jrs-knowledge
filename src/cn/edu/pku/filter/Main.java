package cn.edu.pku.filter;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.HanLPOccurrence;
import cn.edu.pku.util.HanLPSegmenter;

public class Main {

	public static void main(String [] args) {
		int[] indices = {3};
		String [] tokens = {
						"熟悉",
						"熟练",
						"精通",
						"了解",
						"掌握"
						};
		double [] thres = {
		//	0, 0, 0, 0, 0,
						8.5,
						2.5,
						3.5,
						8.5,
						8.5
						};
		HanLPSegmenter.segmentation(
					FilterConf.ProcessingPath + "text", "	",
					FilterConf.ProcessingPath + "tokens",
					FilterConf.ProcessingPath + "tokens.pos",
					FilterConf.ProcessingPath + "tokens.pos.loc", " ",
					indices
					);
		System.out.println("分词结束");
		HanLPOccurrence.extractFormTokens(
					FilterConf.ProcessingPath + "tokens",
					FilterConf.ProcessingPath + "tokens.occ"
					);
		System.out.println("共现关系提取结束");
		Pattern.calculate(tokens, thres);
		System.out.println("计算结束");
	}
}
