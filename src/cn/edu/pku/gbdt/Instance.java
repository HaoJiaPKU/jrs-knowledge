package cn.edu.pku.gbdt;

import java.util.HashMap;
import java.util.HashSet;

public class Instance {
	
	public HashMap<String, Double> numTypeFeature;
	public HashMap<String, String> strTypeFeature;
	
	public HashMap<String, Double> getNumTypeFeature() {
		return numTypeFeature;
	}

	public void setNumTypeFeature(HashMap<String, Double> numTypeFeature) {
		this.numTypeFeature = numTypeFeature;
	}

	public HashMap<String, String> getStrTypeFeature() {
		return strTypeFeature;
	}

	public void setStrTypeFeature(HashMap<String, String> strTypeFeature) {
		this.strTypeFeature = strTypeFeature;
	}

	public Instance() {
		super();
		numTypeFeature = new HashMap<String, Double>();
		strTypeFeature = new HashMap<String, String>();
	}

	public Instance(HashMap<String, Double> numTypeFeature, HashMap<String, String> strTypeFeature) {
		super();
		this.numTypeFeature = (HashMap<String, Double>) numTypeFeature.clone();
		this.strTypeFeature = (HashMap<String, String>) strTypeFeature.clone();
	}
	
	//这里的token就是featureName
	public Instance makeInstance(String[] tokens, Model model) {
		Instance instance = new Instance();
		for (int i = 0; i < tokens.length; i ++) {
			if (!isFeature(tokens[i], model)) {
				continue;
			}
			if (isRealTypeValue(tokens[i], model)) {
				if (instance.numTypeFeature.containsKey(tokens[i])) {
					instance.numTypeFeature.put(
							tokens[i], instance.numTypeFeature.get(tokens[i]) + 1.0);
				} else {
					instance.numTypeFeature.put(tokens[i], 1.0);
				}
			} else {
				//TODO
			}
		}
		for (String feature : model.getNumTypeFeature().keySet()) {
			if (isRealTypeValue(feature, model)) {
				if (!instance.numTypeFeature.containsKey(feature)) {
					instance.numTypeFeature.put(feature, 0.0);
				}
			} else {
				if (!instance.strTypeFeature.containsKey(feature)) {
					instance.strTypeFeature.put(feature, "");
				}
			}
		}
		return instance;
	}
	
	public boolean isFeature(String name, Model model) {
		return model.getNumTypeFeature().containsKey(name)
				|| model.getStrTypeFeature().containsKey(name);
	}
	
	public boolean isRealTypeValue(String name, Model model) {
		return model.getStrTypeFeature().get(name).size() == 0;
	}

}
