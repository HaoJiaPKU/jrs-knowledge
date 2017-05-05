package cn.edu.pku.gbdt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.edu.pku.util.FileInput;
import cn.edu.pku.util.FileOutput;

public class Model {

	public int maxIter;
	public double sampleRate;
	public double learnRate;
	public int maxDepth;
	public int splitPoints;
	public HashSet<String> labelValueset;
	public HashMap<String, HashSet<Double>> numTypeFeature;
	public HashMap<String, HashSet<String>> strTypeFeature;
	public HashMap<Integer, HashMap<String, Tree>> trees;
	
	
	public int getMaxIter() {
		return maxIter;
	}

	public void setMaxIter(int maxIter) {
		this.maxIter = maxIter;
	}

	public double getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(double sampleRate) {
		this.sampleRate = sampleRate;
	}

	public double getLearnRate() {
		return learnRate;
	}

	public void setLearnRate(double learnRate) {
		this.learnRate = learnRate;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public int getSplitPoints() {
		return splitPoints;
	}

	public void setSplitPoints(int splitPoints) {
		this.splitPoints = splitPoints;
	}

	public HashSet<String> getLabelValueset() {
		return labelValueset;
	}

	public void setLabelValueset(HashSet<String> labelValueset) {
		this.labelValueset = labelValueset;
	}

	public HashMap<String, HashSet<Double>> getNumTypeFeature() {
		return numTypeFeature;
	}

	public void setNumTypeFeature(HashMap<String, HashSet<Double>> numTypeFeature) {
		this.numTypeFeature = numTypeFeature;
	}

	public HashMap<String, HashSet<String>> getStrTypeFeature() {
		return strTypeFeature;
	}

	public void setStrTypeFeature(HashMap<String, HashSet<String>> strTypeFeature) {
		this.strTypeFeature = strTypeFeature;
	}

	public HashMap<Integer, HashMap<String, Tree>> getTrees() {
		return trees;
	}

	public void setTrees(HashMap<Integer, HashMap<String, Tree>> trees) {
		this.trees = trees;
	}

	public Model() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Model(int maxIter, double sampleRate, double learnRate,
			int maxDepth, int splitPoints,
			HashMap<String, HashSet<Double>> numTypeFeature,
			HashMap<String, HashSet<String>> strTypeFeature,
			HashSet<String> labelValueset) {
		super();
		this.maxIter = maxIter;
		this.sampleRate = sampleRate;
		this.learnRate = learnRate;
		this.maxDepth = maxDepth;
		this.splitPoints = splitPoints;
		this.numTypeFeature = (HashMap<String, HashSet<Double>>) numTypeFeature.clone();
		this.strTypeFeature = (HashMap<String, HashSet<String>>) strTypeFeature.clone();
		this.labelValueset = (HashSet<String>) labelValueset.clone();
		this.trees = new HashMap<Integer, HashMap<String, Tree>>();
	}
	
	public void initialize(HashMap<Integer, HashMap<String, Double>> f, DataSet dataset) {
		for (Integer id : dataset.getInstanceIdset()) {
			HashMap<String, Double> t = new HashMap<String, Double>();
			for (String label : labelValueset) {
				t.put(label, 0.0);
			}
			f.put(id, t);
		}
	}
	
	public HashMap<Integer, HashMap<String, Double>> computeResidual(
			DataSet dataset, HashSet<Integer> subset,
			HashMap<Integer, HashMap<String, Double>> f) {
		HashMap<Integer, HashMap<String, Double>> residual
			= new HashMap<Integer, HashMap<String, Double>>();
		for (Integer id : subset) {
			HashMap<String, Double> residualId = new HashMap<String, Double>();
			double pSum = 0.0;
			for (String label : labelValueset) {
				pSum += Math.exp(f.get(id).get(label));
			}
			for (String label : labelValueset) {
				double p = Math.exp(f.get(id).get(label)) / pSum;
				double y = 0.0;
				if (dataset.getInstance(id).strTypeFeature.get("label").equals(label)) {
					y = 1.0;
				}
				residualId.put(label, y - p);
			}
			residual.put(id, (HashMap<String, Double>) residualId.clone());
		}
		return residual;
	}
	
	public void updataFValue(HashMap<Integer, HashMap<String, Double>> f, Tree tree,
			HashSet<LeafNode> leafNodes, HashSet<Integer> subset, DataSet dataset,
			String label) {
		Set<Integer> dataIdset = dataset.getInstanceIdset();
		for (LeafNode node : leafNodes) {
			for (Integer id : node.getIdset()) {
				double t = f.get(id).get(label) + learnRate * node.getPredictValue();
				HashMap<String, Double> tt = f.get(id);
				tt.put(label, t);
				f.put(id, tt);
			}
		}
		for (Integer id : dataIdset) {
			if (!subset.contains(id)) {
				double t = f.get(id).get(label) + learnRate * tree.getPredictValue(dataset.getInstance(id));
				HashMap<String, Double> tt = f.get(id);
				tt.put(label, t);
				f.put(id, tt);
			}
		}
	}
	
	public HashMap<String, Double> computeInstanceFValue(Instance instance) {
		HashMap<String, Double> fValue = new HashMap<String, Double> ();
		for (String label : labelValueset) {
			fValue.put(label, 0.0);
		}
		for (Integer iter : trees.keySet()) {
			for (String label : labelValueset) {
				Tree tree = trees.get(iter).get(label);
				double t = fValue.get(label) + learnRate * tree.getPredictValue(instance);
				fValue.put(label, t);
			}
		}
		return fValue;
	}
	
	public double computeLoss(DataSet dataset, HashSet<Integer> subset,
			HashMap<Integer, HashMap<String, Double>> f) {
		double loss = 0.0;
		for (Integer id : subset) {
			Instance instance = dataset.getInstance(id);
			HashMap<String, Double> fValues;
			if (f.containsKey(id)) {
				fValues = f.get(id);
			} else {
				fValues = computeInstanceFValue(instance);
			}
			HashMap<String, Double> expValues = new HashMap<String, Double>();
			double sumExpValues = 0.0;
			for (String label : fValues.keySet()) {
				double t = Math.exp(fValues.get(label));
				expValues.put(label, t);
				sumExpValues += t;
			}
			HashMap<String, Double> probs = new HashMap<String, Double>();
			for (String label : fValues.keySet()) {
				probs.put(label, Math.exp(fValues.get(label)) / sumExpValues);
			}
			loss -= Math.log(probs.get(instance.strTypeFeature.get("label")));
		}
		return loss / (double) subset.size();
	}
	
	public void train(DataSet dataset, HashSet<Integer> trainData,
			String outputPath, HashSet<Integer> testData) {
		FileOutput fo = new FileOutput(outputPath);
		HashMap<Integer, HashMap<String, Double>> f = new HashMap<Integer, HashMap<String, Double>>();
		initialize(f, dataset);
		for (int iter = 0; iter < maxIter; iter ++) {
			HashSet<Integer> subset = (HashSet<Integer>) trainData.clone();
			if (sampleRate > 0 && sampleRate < 1) {
				subset = Util.sample(subset, (int)(subset.size() * sampleRate));
			}
			trees.put(iter, new HashMap<String, Tree>());
			HashMap<Integer, HashMap<String, Double>> residual
				= computeResidual(dataset, subset, f);
			for (String label : labelValueset) {
				HashSet<LeafNode> leafNodes = new HashSet<LeafNode>();
				HashMap<Integer, Double> targets = new HashMap<Integer, Double> ();
				for (Integer id : subset) {
					targets.put(id, residual.get(id).get(label));
				}
				Tree tree = Util.makeDecisionTree(dataset, subset, targets, 0, leafNodes, maxDepth, splitPoints);
				HashMap<String, Tree> t = trees.get(iter);
				t.put(label, tree);
				trees.put(iter, t);
				updataFValue(f, tree, leafNodes, subset, dataset, label);
			}
			double accuracy[] = new double[6];
			if (testData != null && testData.size() != 0.0) {
				accuracy = test(dataset, testData, 5);
			}
			double trainLoss = computeLoss(dataset, trainData, f);
			double testLoss = computeLoss(dataset, testData, f);
			String cri = new String();
			for (int i = 0; i < 5; i ++) {
				cri += "accuracy" + String.valueOf(i) + "	" + String.valueOf(accuracy[i]) + "	";
			}
			cri += "average train loss	" + trainLoss
					+ "	average test loss	" + testLoss
					+ "	average risk	" + String.valueOf(accuracy[5]);
			System.out.println(cri);
			System.out.println((iter + 1) + "th iteration completed");
			System.out.println();
			try {
				fo.t3.write(cri);
				fo.t3.newLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		fo.closeOutput();
	}
	
	public double[] test(DataSet dataset, HashSet<Integer> testData, int predictNum) {
		double rightPrediction[] = new double[predictNum + 1];
		for (int i = 0; i < predictNum + 1; i ++) {
			rightPrediction[i] = 0.0;
		}
		double risk = 0.0;
		for (Integer id : testData) {
			Instance instance = dataset.getInstance(id);
			HashMap<String, Double> probs = predict(instance);
			String predictLabel[] = predictLabel(probs, predictNum);
			double singleRisk = 0.0;
			for (String label : probs.keySet()) {
				if (label.equals(instance.strTypeFeature.get("label"))) {
					singleRisk += (1.0 - probs.get(label));
				} else {
					singleRisk += probs.get(label);
				}
			}
			risk += singleRisk / (double) probs.size();
			for (int i = 0; i < predictNum; i ++) {
				if (instance.strTypeFeature.get("label").equals(predictLabel[i])) {
					for (int j = i; j < predictNum; j ++) {
						rightPrediction[j] += 1.0;
					}
					break;
				}
			}
		}
		for (int i = 0; i < predictNum; i ++) {
			rightPrediction[i] /= (double) testData.size();
		}
		rightPrediction[predictNum] = risk / (double) testData.size();
		return rightPrediction;
	}
	
	public HashMap<String, Double> predict(Instance instance) {
		HashMap<String, Double> fValue = computeInstanceFValue(instance);
		HashMap<String, Double> expValues = new HashMap<String, Double> ();
		double expSum = 0.0;
		for (String label : fValue.keySet()) {
			double t = Math.exp(fValue.get(label));
			expValues.put(label, t);
			expSum += t;
		}
		HashMap<String, Double> probs = new HashMap<String, Double>();
		for (String label : expValues.keySet()) {
			probs.put(label, expValues.get(label) / expSum);
		}
		return probs;
	}
	
	public String[] predictLabel(HashMap<String, Double> probs, int labelNum) {
		Map.Entry[] set = Util.getSortedHashMapByDoubleValueDec(probs);
		
		String predictLabel[] = new String[labelNum];
		for (int i = 0; i < labelNum; i ++) {
			predictLabel[i] = set[i].getKey().toString();
		}
		return predictLabel;
	}
	
	
	public void save(String modelDisplay, String modelReuse) {
		FileOutput fo1 = new FileOutput(modelDisplay);
		FileOutput fo2 = new FileOutput(modelReuse);
		ArrayList<Tree> treeList = new ArrayList<Tree>();
		for (Integer id : trees.keySet()) {
			HashMap<String, Tree> t = trees.get(id);
			for (String label : t.keySet()) {
				treeList.add(t.get(label));
			}
		}
		
		ObjectMapper om = new ObjectMapper();
		try {
			fo1.t3.write("{\"trees\":[" + "\n");
			for (int j = 0; j < treeList.size() - 1; j++)
				fo1.t3.write(om.writeValueAsString(treeList.get(j)) + ",\n");
			fo1.t3.write(om.writeValueAsString(treeList.get(treeList.size() - 1)) + "]}");
		
			fo2.t3.write(om.writeValueAsString(this));
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
		fo1.closeOutput();
		fo2.closeOutput();
	}

	public void load(String modelPath) {
		FileInput fi = new FileInput(modelPath);
		ObjectMapper om = new ObjectMapper();
		try {
			Model model = om.readValue(fi.reader.readLine(), Model.class);
			this.maxIter = model.maxIter;
			this.sampleRate = model.sampleRate;
			this.learnRate = model.learnRate;
			this.maxDepth = model.maxDepth;
			this.splitPoints = model.splitPoints;
			this.numTypeFeature = (HashMap<String, HashSet<Double>>) model.numTypeFeature.clone();
			this.strTypeFeature = (HashMap<String, HashSet<String>>) model.strTypeFeature.clone();
			this.labelValueset = (HashSet<String>) model.labelValueset.clone();
			this.trees = (HashMap<Integer, HashMap<String, Tree>>) model.trees.clone();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
}
