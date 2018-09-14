package aaa.helpers.openl.model.pup;

import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = PUPOpenLFile.PUP_COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class PUPOpenLCoverage extends OpenLCoverage {
	private Integer limitAmount;

	public Integer getLimitAmount() {
		return limitAmount;
	}

	public void setLimitAmount(Integer limitAmount) {
		this.limitAmount = limitAmount;
	}
}
