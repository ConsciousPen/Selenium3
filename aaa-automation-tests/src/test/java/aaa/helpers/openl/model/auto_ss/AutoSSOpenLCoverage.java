package aaa.helpers.openl.model.auto_ss;

import aaa.helpers.openl.model.AutoOpenLCoverage;

public class AutoSSOpenLCoverage extends AutoOpenLCoverage {
	private String glassDeductible;

	public String getGlassDeductible() {
		return glassDeductible;
	}

	public void setGlassDeductible(String glassDeductible) {
		this.glassDeductible = glassDeductible;
	}

	@Override
	public String toString() {
		return "AutoSSOpenLCoverage{" +
				"additionalLimitAmount=" + additionalLimitAmount +
				", coverageCD='" + coverageCD + '\'' +
				", deductible='" + deductible + '\'' +
				", glassDeductible='" + glassDeductible + '\'' +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
