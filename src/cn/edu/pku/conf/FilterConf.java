package cn.edu.pku.conf;

import java.io.IOException;

import cn.edu.pku.util.FileInput;

public class FilterConf {

	public static final String ConceptPath = "../proConcept";
	public static final String GBDTPath = "../proGBDT";
	public static String [] fields;
	public static String [] fieldDirs;
	
	public static void readFieldFromConf (String inputPath, int num) {
		fields = new String [num];
		fieldDirs = new String [num];
		FileInput fi = new FileInput(inputPath);
		String line = new String ();
		int counter = 0;
		try {
			while ((line = fi.reader.readLine()) != null) {
				String [] tokens = line.trim().split("	");
				fields[counter] = tokens[0].trim();
//				System.out.println(fields[counter]);
				fieldDirs[counter] = tokens[0]
						.trim()
						.replaceAll("/", "or");
//				System.out.println(fieldDirs[counter]);
				counter ++;
				if (counter >= num) {
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public static void main(String[] args) {
		readFieldFromConf(ConceptPath + "category.conf", 100);
	}
}
