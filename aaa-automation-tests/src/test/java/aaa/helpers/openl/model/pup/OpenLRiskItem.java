package aaa.helpers.openl.model.pup;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public class OpenLRiskItem {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private String riskItemCategoryCd;
	private String riskItemCd;
	private Integer riskItemCount;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getRiskItemCategoryCd() {
		return riskItemCategoryCd;
	}

	public void setRiskItemCategoryCd(String riskItemCategoryCd) {
		this.riskItemCategoryCd = riskItemCategoryCd;
	}

	public String getRiskItemCd() {
		return riskItemCd;
	}

	public void setRiskItemCd(String riskItemCd) {
		this.riskItemCd = riskItemCd;
	}

	public Integer getRiskItemCount() {
		return riskItemCount;
	}

	public void setRiskItemCount(Integer riskItemCount) {
		this.riskItemCount = riskItemCount;
	}

	@Override
	public String toString() {
		return "OpenLRiskItem{" +
				"number=" + number +
				", riskItemCategoryCd='" + riskItemCategoryCd + '\'' +
				", riskItemCd='" + riskItemCd + '\'' +
				", riskItemCount=" + riskItemCount +
				'}';
	}
}
