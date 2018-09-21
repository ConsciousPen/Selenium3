package aaa.helpers.openl.model.home_ca.ho6;

import static aaa.helpers.openl.model.OpenLFile.POLICY_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.POLICY_SHEET_NAME;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.HomeCaHO6TestDataGenerator;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import toolkit.datax.TestData;

@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowIndex = POLICY_HEADER_ROW_NUMBER)
public class HomeCaHO6OpenLPolicy extends HomeCaOpenLPolicy<HomeCaHO6OpenLForm, HomeCaHO6OpenLDwelling> {
	private HomeCaHO6OpenLDwelling dwelling;
	private List<HomeCaHO6OpenLForm> forms;
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

	@Override
	public HomeCaHO6OpenLDwelling getDwelling() {
		return dwelling;
	}

	public void setDwelling(HomeCaHO6OpenLDwelling dwelling) {
		this.dwelling = dwelling;
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
	public HomeCaHO6TestDataGenerator getTestDataGenerator(TestData baseTestData) {
		return new HomeCaHO6TestDataGenerator(this.getState(), baseTestData);
	}
}
