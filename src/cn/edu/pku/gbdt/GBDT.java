package cn.edu.pku.gbdt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.FileOutput;

public class GBDT {

	public static void run(
			String dataPath,
			String statusPath,
			String modelDisplay,
			String modelReuse,
			int maxIter,
			double sampleRate,
			double learnRate,
			int maxDepth,
			int splitPoints
			) {
		//GBDT训练
		DataSet dataset = new DataSet(dataPath);
		dataset.describe();
		Model model = new Model(maxIter, sampleRate, learnRate, maxDepth, splitPoints,
				dataset.getNumTypeFeature(),
				dataset.getStrTypeFeature(),
				dataset.getLabelValueset());
		HashSet<Integer> trainData = Util.sample(dataset.getInstanceIdset(), dataset.instances.size() * 2 / 3);
		HashSet<Integer> testData = Util.minusSet(dataset.getInstanceIdset(), trainData);
		model.train(dataset, trainData, statusPath, testData);
		model.save(modelDisplay, modelReuse);
	}
	
	public static void testDeserialization(
			String dataPath,
			String modelPath
			) {
		DataSet dataset = new DataSet(dataPath);
		HashSet<Integer> trainData = Util.sample(dataset.getInstanceIdset(), dataset.instances.size() * 2 / 3);
		HashSet<Integer> testData = Util.minusSet(dataset.getInstanceIdset(), trainData);
		Model model = new Model();
		model.load(modelPath);
		System.out.println(model.test(dataset, testData, 5));
	}
	
	public static void main(String[] args) {
		GBDT.run("../GBDT/data/adult.data.csv",
				"stat.txt",
				"model.json",
				"model2.json",
				10,
				0.3,
				0.1,
				10,
				-1);
		
	}
}
