package cn.edu.pku.util;

import java.io.File;

public class FileDirs {

	public static void makeDirs(String path) {
		File file = new File(path);
		if (!file.exists() && !file.isDirectory()) {
			file.mkdirs();    
		}
	}
}
