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
		
		String line = "股权投资经理5名。 二、岗位职责 1.寻找和挖掘潜在投资项目； "
				+ "2.项目尽职调查、估值分析，撰写投资可行性研究报告； "
				+ "3.谈判交易结构、投资条款、协议文本等，履行投资决策程序； "
				+ "4.负责投资项目的投后管理、投资退出等； "
				+ "5.完成领导交办的其他工作。 "
				+ "三、任职条件 1.金融、会计、法律等相关专业，全日制本科及以上学历； "
				+ "2.三年以上股权投资工作经验； "
				+ "3.熟练掌握股权投资相关政策法规，具备相关专业知识和实际业务操作经验； "
				+ "4.具备较强的沟通协调、商务谈判和文字表达能力； "
				+ "5.有国内知名PE/VC公司工作经验者、具有股权投资成功经历者优先。 "
				+ "6.工资面议";
		String[] expressions = {
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
