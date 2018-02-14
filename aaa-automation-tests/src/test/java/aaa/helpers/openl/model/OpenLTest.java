package aaa.helpers.openl.model;

import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public class OpenLTest {
	@ExcelTableColumnElement(isPrimaryKey = true)
	private Integer policy;

	@ExcelTableColumnElement(name = "Total Premium")
	private Integer totalPremium;

	public Integer getPolicy() {
		return policy;
	}

	public void setPolicy(int policy) {
		this.policy = policy;
	}

	public Integer getTotalPremium() {
		return totalPremium;
	}

	public void setTotalPremium(int totalPremium) {
		this.totalPremium = totalPremium;
	}

	@Override
	public String toString() {
		return "OpenLTest{" +
				"policy=" + policy +
				", totalPremium=" + totalPremium +
				'}';
	}
}
