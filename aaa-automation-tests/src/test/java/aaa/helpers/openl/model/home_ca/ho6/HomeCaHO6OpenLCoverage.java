package aaa.helpers.openl.model.home_ca.ho6;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class HomeCaHO6OpenLCoverage extends HomeCaOpenLCoverage {
	private Double deductibleAmount;

	public Double getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(Double deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}
}
