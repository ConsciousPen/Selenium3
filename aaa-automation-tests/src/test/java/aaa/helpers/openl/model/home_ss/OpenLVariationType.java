package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.VARIATION_TYPE_SHEET_NAME, headerRowIndex = OpenLFile.VARIATION_TYPE_HEADER_ROW_NUMBER)
public class OpenLVariationType {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private String variationId;
	private Double variationLimit;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getVariationId() {
		return variationId;
	}

	public void setVariationId(String variationId) {
		this.variationId = variationId;
	}

	public Double getVariationLimit() {
		return variationLimit;
	}

	public void setVariationLimit(Double variationLimit) {
		this.variationLimit = variationLimit;
	}
}
