package aaa.helpers.openl.model;

import java.util.Comparator;
import aaa.helpers.openl.annotation.MatchingField;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class OpenLForm implements Comparable<OpenLForm> {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	protected Integer number;

	@MatchingField
	@RequiredField
	protected String formCode;

	@MatchingField
	protected Double limit;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public Double getLimit() {
		return limit;
	}

	public void setLimit(Double limit) {
		this.limit = limit;
	}

	@Override
	public int compareTo(OpenLForm otherForm) {
		return Comparator
				.comparing(OpenLForm::getFormCode, Comparator.nullsFirst(Comparator.naturalOrder()))
				.thenComparing(OpenLForm::getLimit, Comparator.nullsFirst(Comparator.naturalOrder()))
				.compare(this, otherForm);
	}
}
