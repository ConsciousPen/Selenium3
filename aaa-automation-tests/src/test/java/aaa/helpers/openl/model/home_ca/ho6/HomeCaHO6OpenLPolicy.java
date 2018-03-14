package aaa.helpers.openl.model.home_ca.ho6;

import static aaa.helpers.openl.model.OpenLFile.DWELLING_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.DWELLING_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.FORM_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.FORM_SHEET_NAME;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class HomeCaHO6OpenLPolicy extends HomeCaOpenLPolicy {
	@ExcelTableElement(sheetName = DWELLING_SHEET_NAME, headerRowIndex = DWELLING_HEADER_ROW_NUMBER)
	private List<HomeCaHO6OpenLDwelling> dwelling;

	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowIndex = FORM_HEADER_ROW_NUMBER)
	private List<HomeCaOpenLForm> forms;

	@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
	private List<HomeCaHO6OpenLCoverage> coverages;

	private Integer covALimit;
	private Integer covELimit;
	private Integer deductible;
	private Boolean hasEmployeeDiscount;
	private Integer ageOfOldestInsured;
	private Boolean hasPolicySupportingForm;
	private Boolean hasSeniorDiscount;
	private Boolean hasAutoPolicy;
	private Boolean hasCeaPolicy;
	private Boolean isRented;
	private String occupation;

	public List<HomeCaHO6OpenLDwelling> getDwellings() {
		return new ArrayList<>(dwelling);
	}

	public void setDwellings(List<HomeCaHO6OpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

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

	public Integer getDeductible() {
		return deductible;
	}

	public void setDeductible(Integer deductible) {
		this.deductible = deductible;
	}

	public Boolean getHasEmployeeDiscount() {
		return hasEmployeeDiscount;
	}

	public void setHasEmployeeDiscount(Boolean hasEmployeeDiscount) {
		this.hasEmployeeDiscount = hasEmployeeDiscount;
	}

	public Integer getAgeOfOldestInsured() {
		return ageOfOldestInsured;
	}

	public void setAgeOfOldestInsured(Integer ageOfOldestInsured) {
		this.ageOfOldestInsured = ageOfOldestInsured;
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

	public Boolean getHasAutoPolicy() {
		return hasAutoPolicy;
	}

	public void setHasAutoPolicy(Boolean hasAutoPolicy) {
		this.hasAutoPolicy = hasAutoPolicy;
	}

	public Boolean getHasCeaPolicy() {
		return hasCeaPolicy;
	}

	public void setHasCeaPolicy(Boolean hasCeaPolicy) {
		this.hasCeaPolicy = hasCeaPolicy;
	}

	public Boolean getRented() {
		return isRented;
	}

	public void setRented(Boolean rented) {
		isRented = rented;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	@Override
	public String toString() {
		return "HomeCaHO6OpenLPolicy{" +
				"dwelling=" + dwelling +
				", forms=" + forms +
				", covALimit=" + covALimit +
				", covELimit=" + covELimit +
				", deductible=" + deductible +
				", hasEmployeeDiscount=" + hasEmployeeDiscount +
				", ageOfOldestInsured=" + ageOfOldestInsured +
				", hasPolicySupportingForm=" + hasPolicySupportingForm +
				", hasSeniorDiscount=" + hasSeniorDiscount +
				", hasAutoPolicy=" + hasAutoPolicy +
				", hasCeaPolicy=" + hasCeaPolicy +
				", isRented=" + isRented +
				", occupation='" + occupation + '\'' +
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
