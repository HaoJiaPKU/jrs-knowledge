package cn.edu.pku.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.pku.hyp.Hyponymy;
import cn.edu.pku.hyp.HyponymyObj;
import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;
import cn.edu.pku.util.HanLPSegmenter;

public class RegularExp {

	public static String [] expressions = {
			"熟练.+?[，|；|。|！|,|;|.|!]",
			"熟悉.+?[，|；|。|！|,|;|.|!]",
			"掌握.+?[，|；|。|！|,|;|.|!]",
			"精通.+?[，|；|。|！|,|;|.|!]",
			"了解.+?[，|；|。|！|,|;|.|!]",
			"负责.+?[，|；|。|！|,|;|.|!]",
			"参与.+?[，|；|。|！|,|;|.|!]",
			"能够.+?[，|；|。|！|,|;|.|!]",
			"具有.+?[[经验|优先|能力]+][，|；|。|！|,|;|.|!]",
			"具备.+?[[经验|优先|能力]+][，|；|。|！|,|;|.|!]",
			"有.+?[[经验|优先|能力]+][，|；|。|！|,|;|.|!]",
//			"开发.+?[，|；|。|！|,|;|.|!]",
//			"设计.+?[，|；|。|！|,|;|.|!]",
//			"实现.+?[，|；|。|！|,|;|.|!]",
//			"完成.+?[，|；|。|！|,|;|.|!]",
//			"撰写.+?[，|；|。|！|,|;|.|!]",
//			"控制.+?[，|；|。|！|,|;|.|!]",
//			"学习.+?[，|；|。|！|,|;|.|!]",
			};
	
	public static void extractRegularExpFromText (
			String inputPath,
			String inputSeperator,
			String outputRegPath,
			String outputStataHypPath,
			String outputSeperator,
			int[] indices
			) {
		if (indices == null || indices.length == 0) {
			System.out.println("info : no indices specified");
			return;
		}
		
		HashMap<String, Integer> dict
			= new HashMap<String, Integer> ();
		
		FileInput fi = new FileInput(inputPath);
		FileOutput foReg = new FileOutput(outputRegPath);
		FileOutput foStataHyp = new FileOutput(outputStataHypPath);
		
		String line = new String();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String content = new String();
				String [] fields = line.split(inputSeperator);
				if (fields.length - 1 < indices[indices.length - 1]) {
					continue;
				}
				
				foReg.t3.write(fields[0] + outputSeperator);
				
				boolean [] flag = new boolean [fields.length];
				for (int i = 1; i < fields.length; i ++) {
					flag[i] = false;
				}
				for (int i = 0; i < indices.length; i ++) {
					flag[indices[i]] = true;
				}
				for (int i = 1; i < flag.length; i ++) {
					if (!flag[i]) {
						foReg.t3.write(fields[i] + outputSeperator);
					}
				}
				
				for (int i = 1; i < fields.length; i ++) {
					if (fields[i] == null
							|| fields[i].length() == 0
							|| fields[i].equals("")) {
						continue;
					}
					if (flag[i]) {
						content += fields[i];
					}
				}
				
				String exp = "等.+?[，|。|；|\\s]";
				Pattern p = Pattern.compile(exp);
				Matcher m;
			
				for (int i = 0; i < expressions.length; i ++) {
					Pattern pattern = Pattern.compile(expressions[i]);
					Matcher matcher = pattern.matcher(content);
					while(matcher.find()) {
						String text = matcher.group().toString();
						foReg.t3.write(text);
						m = p.matcher(text);
						while (m.find()) {
							String subText = m.group().toString();
							String [] tokens = HanLPSegmenter
									.segmentation(subText, true, false, true, null);
							if (tokens.length > 0) {
								String t = tokens[tokens.length - 1];
								if (dict.containsKey(t)) {
									dict.put(t, dict.get(t) + 1);
								} else {
									dict.put(t, 1);
								}
							}
						}
					}
				}
				foReg.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			for (String t : dict.keySet()) {
				if (dict.get(t) >= 10) {
					foStataHyp.t3.write(t +  "	" + dict.get(t));
					foStataHyp.t3.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		foReg.closeOutput();
		foStataHyp.closeOutput();
		fi.closeInput();
	}
	
	public static void extractHyponymyPairFromRegText (
			String inputPath,
			String inputPosDictPath,
			String inputSeperator,
			String outputHypPath,
			String outputSeperator,
			int[] indices
			) {
		if (indices == null || indices.length == 0) {
			System.out.println("info : no indices specified");
			return;
		}
		
		HashSet<String> posDict
			= new HashSet<String> ();
		HashMap<HyponymyObj, Integer> hypDict
			= new HashMap<HyponymyObj, Integer> ();
		Hyponymy hyp = new Hyponymy();
		
		FileInput fi = new FileInput(inputPath);
		FileInput fiPosDict = new FileInput(inputPosDictPath);
		
		String line = new String();
		try {
			while ((line = fiPosDict.reader.readLine()) != null) {
				String tokens[] = line.trim().split(inputSeperator);
				posDict.add(tokens[0]);
			}
		} catch (Exception e) {
			
		}
		
		line = new String();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String content = new String();
				String [] fields = line.split(inputSeperator);
				if (fields.length - 1 < indices[indices.length - 1]) {
					continue;
				}
				
				boolean [] flag = new boolean [fields.length];
				for (int i = 1; i < fields.length; i ++) {
					flag[i] = false;
				}
				for (int i = 0; i < indices.length; i ++) {
					flag[indices[i]] = true;
				}
				
				for (int i = 1; i < fields.length; i ++) {
					if (fields[i] == null
							|| fields[i].length() == 0
							|| fields[i].equals("")) {
						continue;
					}
					if (flag[i]) {
						content += fields[i];
					}
				}
				
				String exp = "等.+?[，|。|；|\\s]";
				Pattern p = Pattern.compile(exp);
				Matcher m;
			
				for (int i = 0; i < expressions.length; i ++) {
					Pattern pattern = Pattern.compile(expressions[i]);
					Matcher matcher = pattern.matcher(content);
					String tempLine = line;
					while(matcher.find()) {
						String text = matcher.group().toString();
						m = p.matcher(text);
						while (m.find()) {
							String subText = m.group().toString();
							String [] tokens = HanLPSegmenter
									.segmentation(subText, true, false, true, null);
							if (tokens.length > 0) {
								String t = tokens[tokens.length - 1];
								if (!posDict.contains(t)) {
									continue;
								}
								
								while (tempLine.indexOf(" ") < tempLine.indexOf(subText)) {
									tempLine = tempLine.substring(tempLine.indexOf(" ") + 1);									
								}
								tokens = HanLPSegmenter
										.segmentation(
												tempLine.substring(0, tempLine.indexOf(subText))
												, true, false, true, null);
								for (int j = 0; j < tokens.length; j ++) {
									if (!posDict.contains(tokens[j])) {
										continue;
									}
									HyponymyObj hypobj = new HyponymyObj(t, tokens[j], "");
									if (hypDict.containsKey(hypobj)) {
										hypDict.put(hypobj, hypDict.get(hypobj) + 1);
									} else {
										hypDict.put(hypobj, 1);
									}
								}
							}							
							tempLine = tempLine.substring(
									(tempLine.indexOf(subText) + subText.length()));
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (HyponymyObj obj : hypDict.keySet()) {
			if (hypDict.get(obj) > 10) {
				hyp.hypDict.add(obj);
			}
		}
		
		hyp.save(outputHypPath);
		fi.closeInput();
	}
	
	public static void main(String [] args) {
		
		String line = "数据库，是创建在关系模型基础上的数据库，借助于集合代数等数学概念和方法来处理数据库中的数据。";
		line = line.toLowerCase();
		String[] expressions = {
							"数据库.*?是.*?数据",
							};
		for (int i = 0; i < expressions.length; i ++) {
			Pattern pattern = Pattern.compile(expressions[i]);
			Matcher matcher = pattern.matcher(line);
			System.out.println("exp:" + expressions[i]);
			while(matcher.find()) {
				System.out.println(matcher.group());
			}
		}
	}
}
