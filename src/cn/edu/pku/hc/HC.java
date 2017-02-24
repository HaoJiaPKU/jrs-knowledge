package cn.edu.pku.hc;

public class HC {

	public static void run(
			String inputPath,
			String inputSeperator,
			String outputPath,
			String modelPath
			) {
		HierarchicalClustering hierarchicalClustering
    		= new HierarchicalClustering(
    				inputPath,
    				inputSeperator,
    				outputPath);  
		hierarchicalClustering.saveCluster(
				modelPath + "hcluster.txt",
				modelPath + "hclusterSeq.txt",
				modelPath + "hclusterNonSeq.txt",
	    		hierarchicalClustering.hcluster(), 0);  
	}
}
