package aaa.helpers.openl.model.home_ca;

import aaa.helpers.openl.model.OpenLCoverage;

public class HomeCaOpenLCoverage extends OpenLCoverage {
	protected String coverageCd;
	protected Integer limitAmount;

	public String getCoverageCd() {
		return coverageCd;
	}

	public void setCoverageCd(String coverageCD) {
		this.coverageCd = coverageCD;
	}


	public Integer getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Integer limitAmount) {
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
