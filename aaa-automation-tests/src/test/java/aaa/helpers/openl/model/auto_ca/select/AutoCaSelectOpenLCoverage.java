package aaa.helpers.openl.model.auto_ca.select;

import aaa.helpers.openl.model.OpenLCoverage;

public class AutoCaSelectOpenLCoverage extends OpenLCoverage {
	private Integer additionalLimitAmount;
	private String coverageCD;
	private String deductible;
	private Integer limitCode;

	public Integer getAdditionalLimitAmount() {
		return additionalLimitAmount;
	}

	public void setAdditionalLimitAmount(Integer additionalLimitAmount) {
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
