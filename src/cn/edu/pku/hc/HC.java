package cn.edu.pku.hc;

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
		model.printClusterAsText(
				textModelPath,
	    		model.train(textDistPath),
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
