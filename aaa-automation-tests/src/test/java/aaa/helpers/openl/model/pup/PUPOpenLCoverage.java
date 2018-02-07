package aaa.helpers.openl.model.pup;

import aaa.helpers.openl.model.OpenLCoverage;

public class PUPOpenLCoverage extends OpenLCoverage {
	private String coverageCD;
	private Integer limitAmount;

	public String getCoverageCD() {
		return coverageCD;
	}

	public void setCoverageCD(String coverageCD) {
		this.coverageCD = coverageCD;
	}

	public Integer getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Integer limitAmount) {
		this.limitAmount = limitAmount;
	}

	@Override
	public String toString() {
		return "PUPOpenLCoverage{" +
				"coverageCD='" + coverageCD + '\'' +
				", limitAmount=" + limitAmount +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
