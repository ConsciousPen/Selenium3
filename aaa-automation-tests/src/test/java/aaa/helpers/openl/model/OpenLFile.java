package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public abstract class OpenLFile<P> {
	@ExcelTransient
	public static final int POLICY_HEADER_ROW_NUMBER = 4;

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
	public static final int TESTS_HEADER_ROW_NUMBER = 5;

	@ExcelTransient
	public static final String POLICY_SHEET_NAME = "Batch- PolicyAZ";

	@ExcelTransient
	public static final String CAPPINGDETAILS_SHEET_NAME = "Batch- CappingDetails";

	@ExcelTransient
	public static final String VEHICLE_SHEET_NAME = "Batch- VehicleAZ";

	@ExcelTransient
	public static final String ADDRESS_SHEET_NAME = "Batch- Address";

	@ExcelTransient
	public static final String DRIVER_SHEET_NAME = "Batch- DriverAZ";

	@ExcelTransient
	public static final String COVERAGE_SHEET_NAME = "Batch- CoverageAZ";

	@ExcelTransient
	public static final String TESTS_SHEET_NAME = "Tests";

	@ExcelTableElement(sheetName = "Tests", headerRowNumber = TESTS_HEADER_ROW_NUMBER)
	protected List<OpenLTest> tests;

	public List<OpenLTest> getTests() {
		return tests;
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
