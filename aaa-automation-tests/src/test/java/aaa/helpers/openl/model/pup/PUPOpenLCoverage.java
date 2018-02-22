package aaa.helpers.openl.model.pup;

import aaa.helpers.openl.model.OpenLCoverage;

public class PUPOpenLCoverage extends OpenLCoverage {
	private Integer limitAmount;

	public Integer getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Integer limitAmount) {
		this.limitAmount = limitAmount;
	}

	@Override
	public String toString() {
		return "PUPOpenLCoverage{" +
				"coverageCD='" + coverageCd + '\'' +
				", limitAmount=" + limitAmount +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
