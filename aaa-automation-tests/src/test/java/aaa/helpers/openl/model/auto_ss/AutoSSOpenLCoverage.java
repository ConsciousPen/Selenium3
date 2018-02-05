package aaa.helpers.openl.model.auto_ss;

import aaa.helpers.openl.model.OpenLCoverage;

public class AutoSSOpenLCoverage extends OpenLCoverage {
	private Integer additionalLimitAmount;
	private String coverageCD;
	private String deductible;
	private String glassDeductible;

	public Integer getAdditionalLimitAmount() {
		return additionalLimitAmount;
	}

	public void setAdditionalLimitAmount(int additionalLimitAmount) {
		this.additionalLimitAmount = additionalLimitAmount;
	}

	public String getCoverageCD() {
		return coverageCD;
	}

	public void setCoverageCD(String coverageCD) {
		this.coverageCD = coverageCD;
	}

	public String getDeductible() {
		return deductible;
	}

	public void setDeductible(String deductible) {
		this.deductible = deductible;
	}

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
