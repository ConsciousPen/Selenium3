package aaa.helpers.openl.model.auto_ca.select;

import aaa.helpers.openl.model.AutoOpenLCoverage;

public class AutoCaSelectOpenLCoverage extends AutoOpenLCoverage {
	private Integer limitCode;

	public Integer getLimitCode() {
		return limitCode;
	}

	public void setLimitCode(Integer limitCode) {
		this.limitCode = limitCode;
	}

	@Override
	public String toString() {
		return "AutoCaSelectOpenLCoverage{" +
				"additionalLimitAmount=" + additionalLimitAmount +
				", coverageCD='" + coverageCD + '\'' +
				", deductible='" + deductible + '\'' +
				", limitCode=" + limitCode +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
