package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLTest;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = HomeSSOpenLFile.TESTS_SHEET_NAME, headerRowIndex = OpenLFile.TESTS_HEADER_ROW_NUMBER)
public class OpenLFinalTest extends OpenLTest {

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelColumnElement(name = "p", isPrimaryKey = true)
	private Integer policy;

	@Override
	public Integer getPolicy() {
		return policy;
	}

	@Override
	public void setPolicy(int policy) {
		this.policy = policy;
	}

	@Override
	public String toString() {
		return "OpenLFinalTest{" +
				"policy=" + policy +
				", policy=" + policy +
				", state='" + state + '\'' +
				", premiums=" + premiums +
				", totalPremium=" + totalPremium +
				'}';
	}
}
