package aaa.helpers.openl.model.auto_ss;

import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME + "AZ", headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class AutoSSOpenLCoverage extends AutoOpenLCoverage {
	private String glassDeductible;

	public String getGlassDeductible() {
		return glassDeductible;
	}

	public void setGlassDeductible(String glassDeductible) {
		this.glassDeductible = glassDeductible;
	}

	@Override
	public String toString() {
		return "AutoSSOpenLCoverage{" +
				"additionalLimitAmount=" + additionalLimitAmount +
				", coverageCd='" + coverageCd + '\'' +
				", deductible='" + deductible + '\'' +
				", glassDeductible='" + glassDeductible + '\'' +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
