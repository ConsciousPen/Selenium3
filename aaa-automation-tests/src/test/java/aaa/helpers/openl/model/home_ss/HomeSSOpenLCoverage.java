package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLCoverage;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

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
	public String toString() {
		return "HomeSSOpenLCoverage{" +
				"id=" + id +
				", code='" + coverageCd + '\'' +
				", number=" + number +
				", limit='" + limit + '\'' +
				'}';
	}
}
