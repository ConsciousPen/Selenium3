package aaa.helpers.openl.model.home_ca;

import aaa.helpers.openl.model.OpenLCoverage;

public class HomeCaOpenLCoverage extends OpenLCoverage {
	protected Double limitAmount;

	public Double getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Double limitAmount) {
		this.limitAmount = limitAmount;
	}

	@Override
	public String toString() {
		return "HomeCaOpenLCoverage{" +
				"coverageCd='" + coverageCd + '\'' +
				", limitAmount=" + limitAmount +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
