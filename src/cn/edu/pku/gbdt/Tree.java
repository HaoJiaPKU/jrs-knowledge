package cn.edu.pku.gbdt;

public class Tree {

	public LeafNode leafNode;
	public Tree leftTree;
	public Tree rightTree;
	public String splitFeature;
	public double numConditionValue;
	public String strConditionValue;
	public boolean isRealValueFeature;
	
	
	
	public LeafNode getLeafNode() {
		return leafNode;
	}

	public void setLeafNode(LeafNode leafNode) {
		this.leafNode = leafNode;
	}

	public Tree getLeftTree() {
		return leftTree;
	}

	public void setLeftTree(Tree leftTree) {
		this.leftTree = leftTree;
	}

	public Tree getRightTree() {
		return rightTree;
	}

	public void setRightTree(Tree rightTree) {
		this.rightTree = rightTree;
	}

	public String getSplitFeature() {
		return splitFeature;
	}

	public void setSplitFeature(String splitFeature) {
		this.splitFeature = splitFeature;
	}

	public double getNumConditionValue() {
		return numConditionValue;
	}

	public void setNumConditionValue(double numConditionValue) {
		this.numConditionValue = numConditionValue;
	}

	public String getStrConditionValue() {
		return strConditionValue;
	}

	public void setStrConditionValue(String strConditionValue) {
		this.strConditionValue = strConditionValue;
	}

	public boolean isRealValueFeature() {
		return isRealValueFeature;
	}

	public void setRealValueFeature(boolean isRealValueFeature) {
		this.isRealValueFeature = isRealValueFeature;
	}

	public Tree() {
		super();
		leafNode = null;
		leftTree = null;
		rightTree = null;
		splitFeature = null;
		numConditionValue = 0;
		strConditionValue = null;
		isRealValueFeature = true;
	}
	
	public double getPredictValue(Instance instance) {
		if (leafNode != null) {
			return leafNode.getPredictValue();
		}
		if (splitFeature == null) {
			System.out.println("the tree is null");
		}
//		System.out.println(splitFeature);
//		System.out.println(numConditionValue);
//		System.out.println(strConditionValue);
//		System.out.println(instance == null);
//		System.out.println(instance.numTypeFeature == null);
//		System.out.println(instance.numTypeFeature.get(splitFeature));
		if (isRealValueFeature && instance.numTypeFeature.get(splitFeature) < numConditionValue) {
			return leftTree.getPredictValue(instance);
		} else if (!isRealValueFeature && instance.strTypeFeature.get(splitFeature).equals(strConditionValue)) {
			return leftTree.getPredictValue(instance);
		}
		return rightTree.getPredictValue(instance);
	}
	
	public String describe() {
		if (leftTree == null || rightTree == null) {
			return leafNode.describe();
		}
		String leftInfo = leftTree.describe();
		String rightInfo = rightTree.describe();
		return "{split feature:" + splitFeature
				+ ", split value:" + numConditionValue + "," + strConditionValue
				+ "[left tree:" + leftInfo + "," + "right tree:" + rightInfo + "]}";
	}
}