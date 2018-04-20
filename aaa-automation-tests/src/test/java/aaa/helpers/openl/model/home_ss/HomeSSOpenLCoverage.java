package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLCoverage;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = HomeSSOpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class HomeSSOpenLCoverage extends OpenLCoverage {
	private Integer id;

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelTableColumnElement(name = "code")
	private String coverageCd;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String getCoverageCd() {
		return coverageCd;
	}

	@Override
	public void setCoverageCd(String coverageCd) {
		this.coverageCd = coverageCd;
	}

	@Override
	public String toString() {
		return "HomeSSOpenLCoverage{" +
				"id=" + id +
				", code='" + coverageCd + '\'' +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
