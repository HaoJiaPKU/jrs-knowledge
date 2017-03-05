package cn.edu.pku.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

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
	
	public static void extractFromRegularExp (
			String inputPath,
			String inputSeperator,
			String outputPath,
			String outputSeperator,
			int[] indices
			) {
		if (indices == null || indices.length == 0) {
			System.out.println("info : no indices specified");
			return;
		}
		
		FileInput fi = new FileInput(inputPath);
		FileOutput fo = new FileOutput(outputPath);
		
		String line = new String();
		try {
			while ((line = fi.reader.readLine()) != null) {
				String content = new String();
				String [] fields = line.split(inputSeperator);
				if (fields.length - 1 < indices[indices.length - 1]) {
					continue;
				}
				
				fo.t3.write(fields[0] + outputSeperator);
				
				boolean [] flag = new boolean [fields.length];
				for (int i = 1; i < fields.length; i ++) {
					flag[i] = false;
				}
				for (int i = 0; i < indices.length; i ++) {
					flag[indices[i]] = true;
				}
				for (int i = 1; i < flag.length; i ++) {
					if (!flag[i]) {
						fo.t3.write(fields[i] + outputSeperator);
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
				
				for (int i = 0; i < expressions.length; i ++) {
					Pattern pattern = Pattern.compile(expressions[i]);
					Matcher matcher = pattern.matcher(line);
					while(matcher.find()) {
						fo.t3.write(matcher.group().toString());
					}
				}
				fo.t3.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
		fi.closeInput();
	}
	
	public static void main(String [] args) {
		
		String line = "编程语言编程语言是一种采用一类可编程的存储"
				+ "存储类别的理解,c语言是一门通用计算机编程语言";
		line = line.toLowerCase();
		String[] expressions = {
							"编程语言[^，|；|。|！|,|;|.|!|\\s|((?!编程语言).)]*?是[一]*[[种|类|门|个|款]+][^，|；|。|！|,|;|.|!|\\s]*存储",
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
