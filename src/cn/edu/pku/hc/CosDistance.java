package cn.edu.pku.hc;

import java.util.List;

public class CosDistance {

	public static double getDistance(List<Double> a, List<Double> b) {
		double dotM = 0, sqA = 0, sqB = 0;
//		System.out.println(a.size());
		for (int i = 0; i < a.size(); i ++) {
			dotM += a.get(i) * b.get(i);
			sqA += a.get(i) * a.get(i);
			sqB += b.get(i) * b.get(i);
		}
		sqA = Math.sqrt(sqA);
		sqB = Math.sqrt(sqB);
		return dotM / (sqA * sqB);
	}
	
}
