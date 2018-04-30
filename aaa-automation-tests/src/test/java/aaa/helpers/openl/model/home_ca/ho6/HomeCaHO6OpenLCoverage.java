package aaa.helpers.openl.model.home_ca.ho6;

import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;

public class HomeCaHO6OpenLCoverage extends HomeCaOpenLCoverage {
	private Double deductibleAmount;

	public Double getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(Double deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}

	@Override
	public String toString() {
		return "HomeCaHO6OpenLCoverage{" +
				"deductibleAmount=" + deductibleAmount +
				", limitAmount=" + limitAmount +
				", number=" + number +
				", coverageCd='" + coverageCd + '\'' +
				", limit='" + limit + '\'' +
				'}';
	}
}
