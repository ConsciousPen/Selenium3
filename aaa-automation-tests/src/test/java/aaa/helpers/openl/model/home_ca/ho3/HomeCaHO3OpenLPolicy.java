package aaa.helpers.openl.model.home_ca.ho3;

import static aaa.helpers.openl.model.OpenLFile.POLICY_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.POLICY_SHEET_NAME;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLCoverage;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.HomeCaHO3TestDataGenerator;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import toolkit.datax.TestData;

@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowIndex = POLICY_HEADER_ROW_NUMBER)
public class HomeCaHO3OpenLPolicy extends HomeCaOpenLPolicy<HomeCaHO3OpenLForm, HomeCaHO3OpenLDwelling> {

	private HomeCaHO3OpenLDwelling dwelling;
	private List<HomeCaHO3OpenLForm> forms;
	private List<HomeCaOpenLCoverage> coverages;

	private Double covALimit;
	private Double covELimit;
	private Boolean hasEmployeeDiscount;
	private Boolean hasMultiPolicyDiscount;
	private Boolean hasPolicySupportingForm;
	private Boolean hasSeniorDiscount;

	@Override
	public HomeCaHO3OpenLDwelling getDwelling() {
		return dwelling;
	}

	public void setDwelling(HomeCaHO3OpenLDwelling dwelling) {
		this.dwelling = dwelling;
	}

	@Override
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
	public HomeCaHO3TestDataGenerator getTestDataGenerator(TestData baseTestData) {
		return new HomeCaHO3TestDataGenerator(this.getState(), baseTestData);
	}
}
