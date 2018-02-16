package aaa.helpers.openl.model;

public class AutoOpenLCoverage extends OpenLCoverage {
	protected Integer additionalLimitAmount;
	protected String deductible;

	public Integer getAdditionalLimitAmount() {
		return additionalLimitAmount;
	}

	public void setAdditionalLimitAmount(Integer additionalLimitAmount) {
		this.additionalLimitAmount = additionalLimitAmount;
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
				", coverageCd='" + coverageCd + '\'' +
				", deductible='" + deductible + '\'' +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
