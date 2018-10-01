package aaa.helpers.openl.model.home_ca;

import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class HomeCaOpenLCoverage extends OpenLCoverage {
	protected Double limitAmount;

	public Double getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Double limitAmount) {
		this.limitAmount = limitAmount;
	}
}
