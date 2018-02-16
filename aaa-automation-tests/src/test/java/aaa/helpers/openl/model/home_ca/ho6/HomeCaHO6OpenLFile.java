package aaa.helpers.openl.model.home_ca.ho6;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class HomeCaHO6OpenLFile extends HomeCaOpenLFile<HomeCaHO6OpenLPolicy> {
	@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowIndex = POLICY_HEADER_ROW_NUMBER)
	protected List<HomeCaHO6OpenLPolicy> policies;

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelTransient
	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME, headerRowIndex = COVERAGE_HEADER_ROW_NUMBER)
	private List<HomeCaHO6OpenLCoverage> coverages;

	@ExcelTransient
	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowIndex = FORM_HEADER_ROW_NUMBER)
	private List<HomeCaOpenLForm> forms;

	@ExcelTransient
	@ExcelTableElement(sheetName = DWELLING_SHEET_NAME, headerRowIndex = DWELLING_HEADER_ROW_NUMBER)
	private List<HomeCaHO6OpenLDwelling> dwelling;

	public List<HomeCaOpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeCaOpenLForm> forms) {
		this.forms = new ArrayList<>(forms);
	}

	public List<HomeCaHO6OpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<HomeCaHO6OpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public List<HomeCaHO6OpenLDwelling> getDwellings() {
		return new ArrayList<>(dwelling);
	}

	public void setDwellings(List<HomeCaHO6OpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

	@Override
	public List<HomeCaHO6OpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<HomeCaHO6OpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	@Override
	public String toString() {
		return "HomeCaDP3OpenLFile{" +
				"policies=" + policies +
				", forms=" + forms +
				", coverages=" + coverages +
				", address=" + address +
				", dwelling=" + dwelling +
				", tests=" + tests +
				'}';
	}
}
