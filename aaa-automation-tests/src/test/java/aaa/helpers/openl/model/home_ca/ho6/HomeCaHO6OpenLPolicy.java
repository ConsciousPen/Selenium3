package aaa.helpers.openl.model.home_ca.ho6;

import static aaa.helpers.openl.model.OpenLFile.*;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class HomeCaHO6OpenLPolicy extends HomeCaOpenLPolicy<HomeCaHO6OpenLForm> {
	@ExcelTableElement(sheetName = DWELLING_SHEET_NAME, headerRowIndex = DWELLING_HEADER_ROW_NUMBER)
	private List<HomeCaHO6OpenLDwelling> dwelling;

	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowIndex = HomeCaHO6OpenLFile.FORM_HEADER_ROW_NUMBER)
	private List<HomeCaHO6OpenLForm> forms;

	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME, headerRowIndex = COVERAGE_HEADER_ROW_NUMBER)
	private List<HomeCaHO6OpenLCoverage> coverages;

	private Double covALimit;
	private Double covELimit;
	private Double deductible;
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

	@Override
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

	public Double getCovALimit() {
		return covALimit;
	}

	public void setCovALimit(Double covALimit) {
		this.covALimit = covALimit;
	}

	public Double getCovELimit() {
		return covELimit;
	}

	public void setCovELimit(Double covELimit) {
		this.covELimit = covELimit;
	}

	public Double getDeductible() {
		return deductible;
	}

	public void setDeductible(Double deductible) {
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
