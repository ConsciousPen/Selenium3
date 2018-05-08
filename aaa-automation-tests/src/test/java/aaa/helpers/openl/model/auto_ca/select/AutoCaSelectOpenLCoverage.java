package aaa.helpers.openl.model.auto_ca.select;

import aaa.helpers.openl.model.AutoOpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class AutoCaSelectOpenLCoverage extends AutoOpenLCoverage {
	private Integer limitCode;

	public Integer getLimitCode() {
		return limitCode;
	}

	public void setLimitCode(Integer limitCode) {
		this.limitCode = limitCode;
	}

	@Override
	public String toString() {
		return "AutoCaSelectOpenLCoverage{" +
				"additionalLimitAmount=" + additionalLimitAmount +
				", coverageCd='" + coverageCd + '\'' +
				", deductible='" + deductible + '\'' +
				", limitCode=" + limitCode +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
