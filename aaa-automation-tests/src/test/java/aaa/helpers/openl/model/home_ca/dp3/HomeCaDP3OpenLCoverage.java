package aaa.helpers.openl.model.home_ca.dp3;

import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;

public class HomeCaDP3OpenLCoverage extends HomeCaOpenLCoverage {
	private Integer deductibleAmount;

	public Integer getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(Integer deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}

	@Override
	public String toString() {
		return "HomeCaDP3OpenLCoverage{" +
				"deductibleAmount=" + deductibleAmount +
				", limitAmount=" + limitAmount +
				", number=" + number +
				", coverageCd='" + coverageCd + '\'' +
				", limit='" + limit + '\'' +
				'}';
	}
}
