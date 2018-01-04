package aaa.helpers.openl.model;

import aaa.utils.excel.bind.ExcelTableColumnElement;

public class OpenLTest {
	@ExcelTableColumnElement(isPrimaryKey = true)
	private int policy;

	@ExcelTableColumnElement(name = "Total Premium")
	private int totalPremium;

	public int getPolicy() {
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
