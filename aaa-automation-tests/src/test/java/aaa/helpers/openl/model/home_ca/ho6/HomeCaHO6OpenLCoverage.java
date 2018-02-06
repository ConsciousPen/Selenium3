package aaa.helpers.openl.model.home_ca.ho6;

import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;

public class HomeCaHO6OpenLCoverage extends HomeCaOpenLCoverage {
	private Integer deductibleAmount;

	public Integer getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(Integer deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}

	@Override
	public String toString() {
		return "HomeCaHO6OpenLCoverage{" +
				"deductibleAmount=" + deductibleAmount +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
