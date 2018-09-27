package aaa.helpers.openl.model;

import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class AutoOpenLCoverage extends OpenLCoverage {
	protected Double additionalLimitAmount;
	protected String deductible;

	public Double getAdditionalLimitAmount() {
		return additionalLimitAmount;
	}

	public void setAdditionalLimitAmount(Double additionalLimitAmount) {
		this.additionalLimitAmount = additionalLimitAmount;
	}

	public void setAdditionalLimitAmount(Integer additionalLimitAmount) {
		this.additionalLimitAmount = additionalLimitAmount.doubleValue();
	}

	public String getDeductible() {
		return deductible;
	}

	public void setDeductible(String deductible) {
		this.deductible = deductible;
	}
}
