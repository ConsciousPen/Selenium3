package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_DEDUCTIBLE_SHEET_NAME, headerRowIndex = HomeSSOpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class OpenLCoverageDeductible {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private String coverageDeductible;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getCoverageDeductible() {
		return coverageDeductible;
	}

	public void setCoverageDeductible(String coverageDeductible) {
		this.coverageDeductible = coverageDeductible;
	}
}
