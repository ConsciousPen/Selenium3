package aaa.helpers.openl.model.home_ca.ho3;

import static aaa.helpers.openl.model.OpenLFile.DWELLING_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.DWELLING_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.FORM_SHEET_NAME;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.utils.excel.bind.ExcelTableElement;

public class HomeCaHO3OpenLPolicy extends HomeCaOpenLPolicy {
	@ExcelTableElement(sheetName = DWELLING_SHEET_NAME, headerRowNumber = DWELLING_HEADER_ROW_NUMBER)
	private List<HomeCaHO3OpenLDwelling> dwelling;

	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowNumber = HomeCaHO3OpenLFile.FORM_HEADER_ROW_NUMBER)
	private List<HomeCaHO3OpenLForm> forms;

	@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowNumber = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
	private List<HomeCaOpenLCoverage> coverages;

	private Integer covALimit;
	private Integer covELimit;
	private Boolean hasEmployeeDiscount;
	private Boolean hasMultiPolicyDiscount;
	private Boolean hasPolicySupportingForm;
	private Boolean hasSeniorDiscount;

	public List<HomeCaHO3OpenLDwelling> getDwellings() {
		return new ArrayList<>(dwelling);
	}

	public void setDwellings(List<HomeCaHO3OpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

	public List<HomeCaHO3OpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeCaHO3OpenLForm> forms) {
		this.forms = new ArrayList<>(forms);
	}

	public List<HomeCaOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<HomeCaOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public Integer getCovALimit() {
		return covALimit;
	}

	public void setCovALimit(Integer covALimit) {
		this.covALimit = covALimit;
	}

	public Integer getCovELimit() {
		return covELimit;
	}

	public void setCovELimit(Integer covELimit) {
		this.covELimit = covELimit;
	}

	public Boolean getHasEmployeeDiscount() {
		return hasEmployeeDiscount;
	}

	public void setHasEmployeeDiscount(Boolean hasEmployeeDiscount) {
		this.hasEmployeeDiscount = hasEmployeeDiscount;
	}

	public Boolean getHasMultiPolicyDiscount() {
		return hasMultiPolicyDiscount;
	}

	public void setHasMultiPolicyDiscount(Boolean hasMultiPolicyDiscount) {
		this.hasMultiPolicyDiscount = hasMultiPolicyDiscount;
	}

	public Boolean getHasPolicySupportingForm() {
		return hasPolicySupportingForm;
	}

	public void setHasPolicySupportingForm(Boolean hasPolicySupportingForm) {
		this.hasPolicySupportingForm = hasPolicySupportingForm;
	}

	public Boolean getHasSeniorDiscount() {
		return hasSeniorDiscount;
	}

	public void setHasSeniorDiscount(Boolean hasSeniorDiscount) {
		this.hasSeniorDiscount = hasSeniorDiscount;
	}

	@Override
	public String toString() {
		return "HomeCaHO3OpenLPolicy{" +
				"dwelling=" + dwelling +
				", forms=" + forms +
				", covALimit=" + covALimit +
				", covELimit=" + covELimit +
				", hasEmployeeDiscount=" + hasEmployeeDiscount +
				", hasMultiPolicyDiscount=" + hasMultiPolicyDiscount +
				", hasPolicySupportingForm=" + hasPolicySupportingForm +
				", hasSeniorDiscount=" + hasSeniorDiscount +
				", coverages=" + coverages +
				", claimPoints=" + claimPoints +
				", covCLimit=" + covCLimit +
				", expClaimPoints=" + expClaimPoints +
				", isAaaMember=" + isAaaMember +
				", yearsOfPriorInsurance=" + yearsOfPriorInsurance +
				", yearsWithCsaa=" + yearsWithCsaa +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
