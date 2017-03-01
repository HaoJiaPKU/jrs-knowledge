package cn.edu.pku.gbdt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import cn.edu.pku.util.FileInput;

public class DataSet {
	
	public HashMap<Integer, Instance> instances;
	public HashMap<String, Integer> fieldNames;
	public HashMap<String, HashSet<Double>> numTypeFeature;
	public HashMap<String, HashSet<String>> strTypeFeature;
	
	
	public HashMap<Integer, Instance> getInstances() {
		return instances;
	}

	public void setInstances(HashMap<Integer, Instance> instances) {
		this.instances = instances;
	}

	public HashMap<String, Integer> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(HashMap<String, Integer> fieldNames) {
		this.fieldNames = fieldNames;
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

	public DataSet (String fileName) {
		int lineCnt = 0;
		instances = new HashMap<Integer, Instance>();
		numTypeFeature = new HashMap<String, HashSet<Double>>();
		FileInput fi = new FileInput(fileName);
		String line = new String ();
		try {
			while((line = fi.reader.readLine()) != null) {
				if (line.equals("\n")) {
					continue;
				}
				//更换数据之后需要更改****************
//				line = line.substring(0, line.length() - 1);
				String[] fields = line.split(",");
				//更换数据之后需要更改****************
				//表头
				if (lineCnt == 0) {
//					System.out.println("第一行表头");
					fieldNames = new HashMap<String, Integer>();
					for (int i = 0; i < fields.length; i ++) {
						fieldNames.put(fields[i], i);
					}
				} else {
					if (fields.length != fieldNames.size()) {
						System.out.println("wrong field number: line " + lineCnt);
						continue;
					}
					if (lineCnt == 1) {
						strTypeFeature = new HashMap<String, HashSet<String>>();
						for (String fieldName : fieldNames.keySet()) {
							HashSet<String> valueSet = new HashSet<String>();
							try {
								Double.parseDouble(fields[fieldNames.get(fieldName)]);
								numTypeFeature.put(
										fieldName, new HashSet<Double>());
							} catch (Exception e) {
								valueSet.add(fields[fieldNames.get(fieldName)]);
							}
							strTypeFeature.put(fieldName, (HashSet<String>) valueSet.clone());
						}
					}
					instances.put(lineCnt, makeInstance(fields));
				}
				lineCnt ++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		fi.closeInput();
	}
	
	public Instance makeInstance(String[] fields) {
		Instance instance = new Instance();
		for (String fieldName : fieldNames.keySet()) {
			if (isRealTypeField(fieldName)) {
				try {
					double value = Double.parseDouble(fields[fieldNames.get(fieldName)]);
					instance.numTypeFeature.put(fieldName, value);
					HashSet<Double> t = numTypeFeature.get(fieldName);
					t.add(value);
					numTypeFeature.put(fieldName, t);
				} catch (Exception e) {
					System.out.println("value type conflict");
				}
			} else {
				instance.strTypeFeature.put(fieldName, fields[fieldNames.get(fieldName)]);
				HashSet<String> t = strTypeFeature.get(fieldName);
				t.add(fields[fieldNames.get(fieldName)]);
				strTypeFeature.put(fieldName, t);
			}
		}
		return instance;
	}
	
	public void describe() {
		String info = "feature:";
		for (String fieldName : fieldNames.keySet()) {
			info += " " + fieldName;
		}
		info += "\ndatasize: " + instances.size() + "\n";
		for (String fieldName : fieldNames.keySet()) {
			info += "\ndescription for field:" + fieldName + "\n";
			if (isRealTypeField(fieldName)) {
				HashSet<Double> t = numTypeFeature.get(fieldName);
				info += "real value, distinct values number:" + t.size();
				double maxValue = -0x7fffffff, minValue = 0x7777777;
				for (Double num : t) {
					if (maxValue < num) {
						maxValue = num;
					}
					if (minValue > num) {
						minValue = num;
					}
				}
				info += "\nrange [" + String.valueOf(minValue) + ", "
						+ String.valueOf(maxValue) + "]\n";
			} else {
				HashSet<String> t = strTypeFeature.get(fieldName);
				info += "enum type, distinct values number:" + t.size();
				info += "\nvalue [";
				for (String value : t) {
					info += value + ", ";
				}
				info += "]\n";
			}
		}
		System.out.println(info);
	}
	
	public boolean isRealTypeField(String name) {
		if (!fieldNames.containsKey(name)) {
			System.out.println("field name not in the dictionary");
		}
		return strTypeFeature.get(name).size() == 0;
	}
	
	public HashSet<Integer> getInstanceIdset() {
		HashSet<Integer> res = new HashSet<Integer>();
		for (Integer id : instances.keySet()) {
			res.add(id);
		}
		return res;
	}
	
	public int getLabelSize() {
		return strTypeFeature.get("label").size();
	}
	
	public HashSet<String> getLabelValueset() {
		return strTypeFeature.get("label");
	}
	
	public Instance getInstance(int id) {
		return instances.get(id);
	}
	
	public HashSet<String> getAttribute() {
		HashSet<String> ret = new HashSet<String>();
		for (String fieldName : fieldNames.keySet()) {
			if (!fieldName.equals("label")) {
				ret.add(fieldName);
			}
		}
		return ret;
	}
}

