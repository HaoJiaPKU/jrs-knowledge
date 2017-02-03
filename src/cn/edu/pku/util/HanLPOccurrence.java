/*
 * <summary></summary>
 * <author>Hankcs</author>
 * <email>me@hankcs.com</email>
 * <create-date>2016-05-28 AM9:44</create-date>
 *
 * <copyright file="DemoOccurrence.java" company="码农场">
 * Copyright (c) 2008-2016, 码农场. All Right Reserved, http://www.hankcs.com/
 * This source is subject to Hankcs. Please contact Hankcs to get more information.
 * </copyright>
 */
package cn.edu.pku.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.occurrence.Occurrence;
import com.hankcs.hanlp.corpus.occurrence.PairFrequency;
import com.hankcs.hanlp.corpus.occurrence.TermFrequency;
import com.hankcs.hanlp.corpus.occurrence.TriaFrequency;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 演示词共现统计
 *
 * @author hankcs
 */
public class HanLPOccurrence
{
	public static void demo1() {
		Occurrence occurrence = new Occurrence();
        occurrence.addAll("在计算机音视频和图形图像技术等二维信息算法处理方面目前比较先进的视频处理算法");
        occurrence.compute();

        Set<Map.Entry<String, TermFrequency>> uniGram = occurrence.getUniGram();
        for (Map.Entry<String, TermFrequency> entry : uniGram)
        {
            TermFrequency termFrequency = entry.getValue();
            System.out.println(termFrequency);
        }

        Set<Map.Entry<String, PairFrequency>> biGram = occurrence.getBiGram();
        for (Map.Entry<String, PairFrequency> entry : biGram)
        {
            PairFrequency pairFrequency = entry.getValue();
            if (pairFrequency.isRight()) {
                System.out.println(pairFrequency);
            }
        }

        Set<Map.Entry<String, TriaFrequency>> triGram = occurrence.getTriGram();
        for (Map.Entry<String, TriaFrequency> entry : triGram)
        {
            TriaFrequency triaFrequency = entry.getValue();
            if (triaFrequency.isRight())
                System.out.println(triaFrequency);
        }
	}
	
	public static void demo2() {
		Occurrence occurrence = new Occurrence();
        occurrence.addAll("在计算机音视频和图形图像技术等二维信息算法处理方面目前比较先进的视频处理算法");
        occurrence.compute();

        System.out.println(occurrence);
        System.out.println(occurrence.getPhraseByScore());
	}
	
	public static void extractFormTokens(String inputPath, String outputPath) {
		FileInput fi = new FileInput(inputPath);
    	FileOutput fo = new FileOutput(outputPath);
    	
    	String line = new String ();
    	String content = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				content += line;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
		
		Occurrence occurrence = new Occurrence();
        occurrence.addAll(content);
        occurrence.compute();
        
        Set<Map.Entry<String, PairFrequency>> biGram = occurrence.getBiGram();
        try {
	        for (Map.Entry<String, PairFrequency> entry : biGram)
	        {
	            PairFrequency pairFrequency = entry.getValue();
	            if (pairFrequency.isRight()) {
					fo.t3.write(pairFrequency.toString());
					fo.t3.newLine();
	            }
	        }
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        fo.closeOutput();
	}
	
	public static void filter(String inputPath, String outputPath) {
		FileInput fi = new FileInput(inputPath);
    	FileOutput fo = new FileOutput(outputPath);
    	
    	String line = new String ();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] term = line.trim().split(" ");
				double score = Double.parseDouble(
						term[5].substring(term[5].indexOf("=") + 1));
				if (score >= 10.0) {
					fo.t3.write(line.trim());
					fo.t3.newLine();
				}
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
//    	demo2();
//    	extractFormTokens("../processing/tokens",
//    					"../processing/tokens.occ");
    	filter("../processing/tokens.pos.occ",
				"../processing/tokens.pos.occ.thres-10.0");
    }
}
