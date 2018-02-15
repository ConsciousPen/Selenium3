package aaa.helpers.openl.model.home_ca.dp3;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class HomeCaDP3OpenLFile extends HomeCaOpenLFile<HomeCaDP3OpenLPolicy> {
	@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowIndex = POLICY_HEADER_ROW_NUMBER)
	private List<HomeCaDP3OpenLPolicy> policies;

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelTransient
	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME, headerRowIndex = COVERAGE_HEADER_ROW_NUMBER)
	private List<HomeCaDP3OpenLCoverage> coverages;

	@ExcelTransient
	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowIndex = FORM_HEADER_ROW_NUMBER)
	private List<HomeCaDP3OpenLForm> forms;

	@ExcelTransient
	@ExcelTableElement(sheetName = DWELLING_SHEET_NAME, headerRowIndex = DWELLING_HEADER_ROW_NUMBER)
	private List<HomeCaDP3OpenLDwelling> dwelling;

	public List<HomeCaDP3OpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeCaDP3OpenLForm> forms) {
		this.forms = new ArrayList<>(forms);
	}

	public List<HomeCaDP3OpenLDwelling> getDwellings() {
		return new ArrayList<>(dwelling);
	}

	public void setDwellings(List<HomeCaDP3OpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

	public List<HomeCaDP3OpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<HomeCaDP3OpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	@Override
	public List<HomeCaDP3OpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<HomeCaDP3OpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	@Override
	public String toString() {
		return "HomeCaDP3OpenLFile{" +
				"policies=" + policies +
				", forms=" + forms +
				", coverages=" + coverages +
				", dwelling=" + dwelling +
				", address=" + address +
				", tests=" + tests +
				'}';
	}
}
