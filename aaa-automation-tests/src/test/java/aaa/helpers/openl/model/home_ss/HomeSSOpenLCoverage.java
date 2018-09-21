package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.annotation.MatchingField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = HomeSSOpenLFile.COVERAGE_HEADER_ROW_NUMBER)
public class HomeSSOpenLCoverage implements Comparable<HomeSSOpenLCoverage> {
	@ExcelTransient
	private Integer id; // IDs in some files are too big for Integer type (e.g. see "ORTests-20171023.xls" file) therefore it's skipped

	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	@MatchingField
	private String code;

	private String limit;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int compareTo(HomeSSOpenLCoverage otherCoverage) {
		return this.getCode().compareTo(otherCoverage.getCode());
	}
}
