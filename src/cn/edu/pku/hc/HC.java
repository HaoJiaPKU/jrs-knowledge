package cn.edu.pku.hc;

public class HC {

	public static void run(
			String inputPath,
			String inputSeperator,
			String textModelPath,
			String modelPath
			) {
		Model model = new Model(
    				inputPath,
    				inputSeperator);  
		model.printClusterAsText(
				textModelPath + "hcluster.txt",
				textModelPath + "hclusterSeq.txt",
				textModelPath + "hclusterNonSeq.txt",
	    		model.train(textModelPath + "hclusterDis.txt"), 0);
		model.save(modelPath);
	}
	
	public static void testDeserialization(
			String modelPath,
			String textModelPath
			) {
		Model model = new Model();
		model.load(modelPath);
		model.printClusterAsText(
				textModelPath + "hcluster.txt",
				textModelPath + "hclusterSeq.txt",
				textModelPath + "hclusterNonSeq.txt",
	    		model.train(textModelPath + "hclusterDis.txt"), 0);
		model.save(modelPath);
	}
}
