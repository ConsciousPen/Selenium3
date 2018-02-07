package aaa.helpers.openl.model;

public class AutoOpenLCoverage extends OpenLCoverage {
	protected Integer additionalLimitAmount;
	protected String coverageCD;
	protected String deductible;

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
		return "AutoOpenLCoverage{" +
				"additionalLimitAmount=" + additionalLimitAmount +
				", coverageCD='" + coverageCD + '\'' +
				", deductible='" + deductible + '\'' +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
