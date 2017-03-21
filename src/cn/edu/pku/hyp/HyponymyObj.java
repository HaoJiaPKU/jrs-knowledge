package cn.edu.pku.hyp;

public class HyponymyObj {

	public String hypernym;
	public String hyponym;
	public String explaination;
	
	public String getHypernym() {
		return hypernym;
	}

	public void setHypernym(String hypernym) {
		this.hypernym = hypernym;
	}

	public String getHyponym() {
		return hyponym;
	}

	public void setHyponym(String hyponym) {
		this.hyponym = hyponym;
	}

	public String getExplaination() {
		return explaination;
	}

	public void setExplaination(String explaination) {
		this.explaination = explaination;
	}

	public HyponymyObj() {
		// TODO Auto-generated constructor stub
	}

	public HyponymyObj(String hypernym, String hyponym, String explaination) {
		this.hypernym = hypernym;
		this.hyponym = hyponym;
		this.explaination = explaination;
	}

	public HyponymyObj(HyponymyObj hyp) {
		this.hypernym = hyp.hypernym;
		this.hyponym = hyp.hyponym;
		this.explaination = hyp.explaination;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((explaination == null) ? 0 : explaination.hashCode());
		result = prime * result + ((hypernym == null) ? 0 : hypernym.hashCode());
		result = prime * result + ((hyponym == null) ? 0 : hyponym.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HyponymyObj other = (HyponymyObj) obj;
		if (explaination == null) {
			if (other.explaination != null)
				return false;
		} else if (!explaination.equals(other.explaination))
			return false;
		if (hypernym == null) {
			if (other.hypernym != null)
				return false;
		} else if (!hypernym.equals(other.hypernym))
			return false;
		if (hyponym == null) {
			if (other.hyponym != null)
				return false;
		} else if (!hyponym.equals(other.hyponym))
			return false;
		return true;
	}	
	
}
