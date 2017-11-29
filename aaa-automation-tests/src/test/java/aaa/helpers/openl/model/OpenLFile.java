package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import aaa.utils.excel.bind.ExcelTableElement;

public abstract class OpenLFile<P> {
	@ExcelTableElement(sheetName = "Tests")
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
