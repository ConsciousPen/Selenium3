package aaa.helpers.openl.model.home_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class HomeSSOpenLFile extends OpenLFile<HomeSSOpenLPolicy> {
	@ExcelTransient
	public static final int COVERAGE_HEADER_ROW_NUMBER = 4;

	@ExcelTransient
	public static final String TESTS_SHEET_NAME = "FinalTest";

	private List<HomeSSOpenLPolicy> policies;
	private List<OpenLNamedInsured> policyNamedInsured;
	private List<OpenLConstructionInfo> policyConstructionInfo;
	private List<HomeSSOpenLCoverage> coverages;
	private List<HomeSSOpenLForm> forms;
	private List<OpenLLossInformation> policyLossInformation;
	private List<OpenLDwellingRatingInfo> policyDwellingRatingInfo;
	private List<HomeSSOpneLCappingDetails> cappingDetails;
	private List<HomeSSOpenLAddress> policyAddress;
	private List<OpenLCoverageDeductible> policyCoverageDeductible;
	private List<OpenLDiscountInformation> policyDiscountInformation;
	private List<OpenLRiskMeterData> riskMeterData; // NJ Specific

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

	public void setCappingDetails(List<HomeSSOpneLCappingDetails> cappingDetails) {
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
	public List<HomeSSOpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<HomeSSOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	@Override
	public String getTestsSheetName() {
		return TESTS_SHEET_NAME;
	}

	@Override
	public String getTestsPolicyHeaderColumnName() {
		return "p";
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
