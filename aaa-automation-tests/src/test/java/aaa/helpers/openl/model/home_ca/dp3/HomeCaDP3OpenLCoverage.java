package aaa.helpers.openl.model.home_ca.dp3;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class HomeCaDP3OpenLCoverage extends HomeCaOpenLCoverage {
	private Integer deductibleAmount;

	public Integer getDeductibleAmount() {
		return deductibleAmount;
	}

	public void setDeductibleAmount(Integer deductibleAmount) {
		this.deductibleAmount = deductibleAmount;
	}

	@Override
	public String toString() {
		return "HomeCaDP3OpenLCoverage{" +
				"deductibleAmount=" + deductibleAmount +
				", limitAmount=" + limitAmount +
				", number=" + number +
				", coverageCd='" + coverageCd + '\'' +
				", limit='" + limit + '\'' +
				'}';
	}
}
