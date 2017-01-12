/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/7 19:25</create-date>
 *
 * <copyright file="DemoChineseNameRecoginiton.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014+ 上海林原信息科技有限公司. All Right Reserved+ http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package cn.edu.pku.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.summary.TextRankKeyword;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * 关键词提取
 * @author hankcs
 */
public class HanLPTextrank
{
	public static final int KeywordNum = 20;
    
	public static void extractFormText(String inputPath, String outputPath) {
		FileInput fi = new FileInput(inputPath);
    	FileOutput fo = new FileOutput(outputPath);
    	
    	String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] pieces = line.trim().split("	+");
				if (pieces.length <= 2) {
					continue;
				}
				List<String> keywordList = HanLP.extractKeyword(pieces[2], KeywordNum);
				Iterator it = keywordList.iterator();
				while (it.hasNext()) {
					fo.t3.write(it.next().toString() + " ");
				}
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
		fo.closeOutput();
	}
	
	public static void extractFormTokens(String inputPath, String outputPath) {
		FileInput fi = new FileInput(inputPath);
    	FileOutput fo = new FileOutput(outputPath);
    	
    	String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				List<String> keywordList = HanLP.extractKeyword(line.trim(), KeywordNum);
				
				Iterator it = keywordList.iterator();
				while (it.hasNext()) {
					fo.t3.write(it.next().toString() + " ");
				}
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
		fo.closeOutput();
	}
	
    public static void main(String[] args)
    {
//    	extractFormText("../processing/text", "../processing/text.textrank");
    	extractFormTokens("../processing/tokens", "../processing/tokens.textrank");
    	extractFormTokens("../processing/tokens.pos", "../processing/tokens.pos.textrank");
    }
}
