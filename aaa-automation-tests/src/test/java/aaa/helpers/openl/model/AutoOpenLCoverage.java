package aaa.helpers.openl.model;

import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
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
