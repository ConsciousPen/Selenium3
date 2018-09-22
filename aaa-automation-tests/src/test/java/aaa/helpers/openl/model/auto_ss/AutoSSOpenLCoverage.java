package aaa.helpers.openl.model.auto_ss;

import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(containsSheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class AutoSSOpenLCoverage extends AutoOpenLCoverage {
	private String glassDeductible;

	public String getGlassDeductible() {
		return glassDeductible;
	}

	public void setGlassDeductible(String glassDeductible) {
		this.glassDeductible = glassDeductible;
	}
}
