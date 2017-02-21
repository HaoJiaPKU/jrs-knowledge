package cn.edu.pku.filter;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class CoOccurrence {

	public static HashMap<String, Integer> dictOcc
		= new HashMap<String, Integer>();
	public static HashMap<String, Integer> dict
		= new HashMap<String, Integer>();
	
	public static void init() {
		dictOcc.clear();
		dict.clear();
	}
	
	public static void clear() {
		dictOcc.clear();
		dict.clear();
	}
	
}
