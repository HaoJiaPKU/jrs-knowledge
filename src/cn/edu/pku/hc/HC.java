package cn.edu.pku.hc;

import java.util.ArrayList;

public class HC {

	public static void run(
			String inputPath,
			String inputSeperator,
			String textModelPath,
			String textDistPath,
			String modelPath
			) {
		Model model = new Model(
    				inputPath,
    				inputSeperator);
		ArrayList<Cluster> clusters = model.train(textDistPath);
		model.printClusterAsText(
				textModelPath,
				clusters,
	    		0);
		model.save(modelPath);
	}
	
	public static void testDeserialization(
			String modelPath,
			String textModelPath,
			String textDistPath
			) {
		Model model = new Model();
		model.load(modelPath);
		model.printClusterAsText(
				textModelPath,
	    		model.train(textDistPath),
	    		0);
		model.save(modelPath);
	}
}
