package aaa.helpers.openl.model.auto_ca.choice;

import aaa.helpers.openl.model.OpenLCoverage;

public class AutoCaChoiceOpenLCoverage extends OpenLCoverage {
	private Integer additionalLimitAmount;
	private String coverageCD;
	private String deductible;

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

	@Override
	public String toString() {
		return "AutoCaChoiceOpenLCoverage{" +
				"additionalLimitAmount=" + additionalLimitAmount +
				", coverageCD='" + coverageCD + '\'' +
				", deductible='" + deductible + '\'' +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
