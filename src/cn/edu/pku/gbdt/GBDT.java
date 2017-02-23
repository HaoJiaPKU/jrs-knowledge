package cn.edu.pku.gbdt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import cn.edu.pku.conf.FilterConf;
import cn.edu.pku.util.FileOutput;

public class GBDT {

	public static void run(
			String dataPath,
			String statusPath,
			String modelPath,
			int maxIter,
			double sampleRate,
			double learnRate,
			int maxDepth,
			int splitPoints
			) {
		//GBDT训练
		DataSet dataset = new DataSet(dataPath);
		dataset.describe();
		Model model = new Model(maxIter, sampleRate, learnRate, maxDepth, splitPoints);
		HashSet<Integer> trainData = Util.sample(dataset.getInstanceIdset(), dataset.instances.size() * 2 / 3);
		HashSet<Integer> testData = Util.minusSet(dataset.getInstanceIdset(), trainData);
		model.train(dataset, trainData, statusPath, testData);
		
		//GBDT模型保存
		FileOutput fo = new FileOutput(modelPath);
		ArrayList<Tree> trees = new ArrayList<Tree>();
		for (Integer id : model.trees.keySet()) {
			HashMap<String, Tree> t = model.trees.get(id);
			for (String label : t.keySet()) {
				trees.add(t.get(label));
			}
		}
		
		//GBDT模型序列化
		ObjectMapper om = new ObjectMapper();
		try {
			fo.t3.write("{\"trees\":[" + "\n");
			for (int j = 0; j < trees.size() - 1; j++)
				fo.t3.write(om.writeValueAsString(trees.get(j)) + ",\n");
			fo.t3.write(om.writeValueAsString(trees.get(trees.size() - 1)) + "]}");
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fo.closeOutput();
	}
	
	public static void main(String[] args) {
		GBDT.run("../GBDT/data/adult.data.csv",
				"stat.txt",
				"model.json",
				10,
				0.3,
				0.1,
				10,
				-1);
	}
}
