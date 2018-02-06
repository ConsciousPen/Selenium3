package aaa.helpers.openl.model.home_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public class HomeSSOpenLFile extends OpenLFile<HomeSSOpenLPolicy> {
	@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowNumber = POLICY_HEADER_ROW_NUMBER)
	private List<HomeSSOpenLPolicy> policies;

	@ExcelTransient
	@ExcelTableElement(sheetName = NAMED_INSURED_SHEET_NAME, headerRowNumber = NAMED_INSURED_HEADER_ROW_NUMBER)
	private List<OpenLNamedInsured> policyNamedInsured;

	@ExcelTransient
	@ExcelTableElement(sheetName = CONSTRUCTION_INFO_SHEET_NAME, headerRowNumber = CONSTRUCTION_INFO_HEADER_ROW_NUMBER)
	private List<OpenLConstructionInfo> policyConstructionInfo;

	@ExcelTransient
	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME, headerRowNumber = COVERAGE_HEADER_ROW_NUMBER)
	private List<HomeSSOpenLCoverage> coverages;

	@ExcelTransient
	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowNumber = FORM_HEADER_ROW_NUMBER)
	private List<HomeSSOpenLForm> forms;

	@ExcelTransient
	@ExcelTableElement(sheetName = LOSS_INFORMATION_SHEET_NAME, headerRowNumber = LOSS_INFORMATION_HEADER_ROW_NUMBER)
	private List<OpenLLossInformation> policyLossInformation;

	@ExcelTransient
	@ExcelTableElement(sheetName = DWELLING_RATING_INFO_SHEET_NAME, headerRowNumber = DWELLING_RATING_INFO_HEADER_ROW_NUMBER)
	private List<OpenLDwellingRatingInfo> policyDwellingRatingInfo;

	@ExcelTransient
	@ExcelTableElement(sheetName = OpenLFile.CAPPINGDETAILS_SHEET_NAME, headerRowNumber = OpenLFile.CAPPINGDETAILS_HEADER_ROW_NUMBER)
	private List<OpenLCappingDetails> cappingDetails;

	@ExcelTransient
	@ExcelTableElement(sheetName = ADDRESS_SHEET_NAME, headerRowNumber = ADDRESS_HEADER_ROW_NUMBER)
	private List<HomeSSOpenLAddress> policyAddress;

	@ExcelTransient
	@ExcelTableElement(sheetName = COVERAGE_DEDUCTIBLE_SHEET_NAME, headerRowNumber = COVERAGE_HEADER_ROW_NUMBER)
	private List<OpenLCoverageDeductible> policyCoverageDeductible;

	@ExcelTransient
	@ExcelTableElement(sheetName = DISCOUNT_INFORMATION_SHEET_NAME, headerRowNumber = DISCOUNT_INFORMATION_HEADER_ROW_NUMBER)
	private List<OpenLDiscountInformation> policyDiscountInformation;

	@ExcelTransient
	@ExcelTableElement(sheetName = RISK_METER_DATA_SHEET_NAME, headerRowNumber = RISK_METER_DATA_HEADER_ROW_NUMBER)
	private List<OpenLRiskMeterData> riskMeterData; // NJ Specific

	@Override
	public List<HomeSSOpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<HomeSSOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	public List<OpenLNamedInsured> getPolicyNamedInsured() {
		return new ArrayList<>(policyNamedInsured);
	}

	public void setPolicyNamedInsured(List<OpenLNamedInsured> policyNamedInsured) {
		this.policyNamedInsured = new ArrayList<>(policyNamedInsured);
	}

	public List<OpenLConstructionInfo> getPolicyConstructionInfo() {
		return new ArrayList<>(policyConstructionInfo);
	}

	public void setPolicyConstructionInfo(List<OpenLConstructionInfo> policyConstructionInfo) {
		this.policyConstructionInfo = new ArrayList<>(policyConstructionInfo);
	}

	public List<HomeSSOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<HomeSSOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public List<HomeSSOpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeSSOpenLForm> forms) {
		this.forms = new ArrayList<>(forms);
	}

	public List<OpenLLossInformation> getPolicyLossInformation() {
		return new ArrayList<>(policyLossInformation);
	}

	public void setPolicyLossInformation(List<OpenLLossInformation> policyLossInformation) {
		this.policyLossInformation = new ArrayList<>(policyLossInformation);
	}

	public List<OpenLDwellingRatingInfo> getPolicyDwellingRatingInfo() {
		return new ArrayList<>(policyDwellingRatingInfo);
	}

	public void setPolicyDwellingRatingInfo(List<OpenLDwellingRatingInfo> policyDwellingRatingInfo) {
		this.policyDwellingRatingInfo = new ArrayList<>(policyDwellingRatingInfo);
	}

	public List<OpenLCappingDetails> getCappingDetails() {
		return new ArrayList<>(cappingDetails);
	}

	public void setCappingDetails(List<OpenLCappingDetails> cappingDetails) {
		this.cappingDetails = new ArrayList<>(cappingDetails);
	}

	public List<HomeSSOpenLAddress> getPolicyAddress() {
		return new ArrayList<>(policyAddress);
	}

	public void setPolicyAddress(List<HomeSSOpenLAddress> policyAddress) {
		this.policyAddress = new ArrayList<>(policyAddress);
	}

	public List<OpenLCoverageDeductible> getPolicyCoverageDeductible() {
		return new ArrayList<>(policyCoverageDeductible);
	}

	public void setPolicyCoverageDeductible(List<OpenLCoverageDeductible> policyCoverageDeductible) {
		this.policyCoverageDeductible = new ArrayList<>(policyCoverageDeductible);
	}

	public List<OpenLDiscountInformation> getPolicyDiscountInformation() {
		return new ArrayList<>(policyDiscountInformation);
	}

	public void setPolicyDiscountInformation(List<OpenLDiscountInformation> policyDiscountInformation) {
		this.policyDiscountInformation = new ArrayList<>(policyDiscountInformation);
	}

	public List<OpenLRiskMeterData> getRiskMeterData() {
		return new ArrayList<>(riskMeterData);
	}

	public void setRiskMeterData(List<OpenLRiskMeterData> riskMeterData) {
		this.riskMeterData = new ArrayList<>(riskMeterData);
	}

	@Override
	public String toString() {
		return "HomeSSOpenLFile{" +
				"policies=" + policies +
				", policyNamedInsured=" + policyNamedInsured +
				", policyConstructionInfo=" + policyConstructionInfo +
				", coverages=" + coverages +
				", forms=" + forms +
				", policyLossInformation=" + policyLossInformation +
				", policyDwellingRatingInfo=" + policyDwellingRatingInfo +
				", cappingDetails=" + cappingDetails +
				", policyAddress=" + policyAddress +
				", policyCoverageDeductible=" + policyCoverageDeductible +
				", policyDiscountInformation=" + policyDiscountInformation +
				", riskMeterData=" + riskMeterData +
				", tests=" + tests +
				'}';
	}
}
