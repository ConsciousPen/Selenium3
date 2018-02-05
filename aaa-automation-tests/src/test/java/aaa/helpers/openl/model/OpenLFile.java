package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public abstract class OpenLFile<P extends OpenLPolicy> {
	@ExcelTransient
	public static final int POLICY_HEADER_ROW_NUMBER = 4;

	@ExcelTransient
	public static final int FORM_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	public static final int CAPPINGDETAILS_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	public static final int VEHICLE_HEADER_ROW_NUMBER = 4;

	@ExcelTransient
	public static final int ADDRESS_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	public static final int DRIVER_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	public static final int COVERAGE_HEADER_ROW_NUMBER = 3;

	@ExcelTransient
	public static final int DWELLING_HEADER_ROW_NUMBER = 4;

	@ExcelTransient
	public static final int TESTS_HEADER_ROW_NUMBER = 5;

	@ExcelTransient
	public static final String POLICY_SHEET_NAME = "Batch- Policy";

	@ExcelTransient
	public static final String FORM_SHEET_NAME = "Batch- Form";

	@ExcelTransient
	public static final String CAPPINGDETAILS_SHEET_NAME = "Batch- CappingDetails";

	@ExcelTransient
	public static final String VEHICLE_SHEET_NAME = "Batch- Vehicle";

	@ExcelTransient
	public static final String ADDRESS_SHEET_NAME = "Batch- Address";

	@ExcelTransient
	public static final String DRIVER_SHEET_NAME = "Batch- Driver";

	@ExcelTransient
	public static final String COVERAGE_SHEET_NAME = "Batch- Coverage";

	@ExcelTransient
	public static final String DWELLING_SHEET_NAME = "Batch- Dwelling";

	@ExcelTransient
	public static final String TESTS_SHEET_NAME = "Tests";

	@ExcelTransient
	public static final String PRIMARY_KEY_COLUMN_NAME = "_PK_";

	@ExcelTableElement(sheetName = TESTS_SHEET_NAME, headerRowNumber = TESTS_HEADER_ROW_NUMBER)
	@ExcelTransient
	protected List<OpenLTest> tests;

	public List<OpenLTest> getTests() {
		return new ArrayList<>(tests);
	}

	public void setTests(List<OpenLTest> tests) {
		this.tests = new ArrayList<>(tests);
	}

	public abstract List<P> getPolicies();

	@Override
	public String toString() {
		return "OpenLFile{" +
				"tests=" + tests +
				'}';
	}
}
