package aaa.helpers.openl.model.home_ca.ho6;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class HomeCaHO6OpenLFile extends HomeCaOpenLFile<HomeCaHO6OpenLPolicy> {
	@ExcelTransient
	public static final int FORM_HEADER_ROW_NUMBER = 4;

	private List<HomeCaHO6OpenLPolicy> policies;

	@ExcelTransient
	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	private List<HomeCaHO6OpenLCoverage> coverages;
	@ExcelTransient
	private List<HomeCaHO6OpenLForm> forms;
	@ExcelTransient
	private List<HomeCaHO6OpenLDwelling> dwelling;

	public List<HomeCaHO6OpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeCaHO6OpenLForm> forms) {
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
