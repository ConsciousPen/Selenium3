package aaa.helpers.openl.model.home_ss;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class HomeSSOpenLFile extends OpenLFile<HomeSSOpenLPolicy> {
	@ExcelTransient
	public static final int COVERAGE_HEADER_ROW_NUMBER = 4;

	@ExcelTransient
	public static final String TESTS_SHEET_NAME = "FinalTest";

	@SuppressWarnings("FieldNameHidesFieldInSuperclass")
	protected List<OpenLFinalTest> tests;

	private List<HomeSSOpenLPolicy> policies;

	@ExcelTransient
	private List<HomeSSOpenLForm> forms;
	@ExcelTransient
	private List<OpenLCoverageDeductible> policyCoverageDeductible;
	@ExcelTransient
	private List<OpenLConstructionInfo> policyConstructionInfo;
	@ExcelTransient
	private List<HomeSSOpenLAddress> policyAddress;
	@ExcelTransient
	private List<OpenLNamedInsured> policyNamedInsured;
	@ExcelTransient
	private List<HomeSSOpneLCappingDetails> cappingDetails;
	@ExcelTransient
	private List<OpenLDiscountInformation> policyDiscountInformation;
	@ExcelTransient
	private List<OpenLDwellingRatingInfo> policyDwellingRatingInfo;
	@ExcelTransient
	private List<HomeSSOpenLCoverage> coverages;
	@ExcelTransient
	private List<OpenLLossInformation> policyLossInformation;
	@ExcelTransient
	private List<OpenLClaim> claims;
	@ExcelTransient
	private List<OpenLRiskMeterData> riskMeterData; // NJ Specific
	@ExcelTransient
	private List<OpenLVariationType> variationType;

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
		return riskMeterData != null ? new ArrayList<>(riskMeterData) : null;
	}

	public void setRiskMeterData(List<OpenLRiskMeterData> riskMeterData) {
		this.riskMeterData = new ArrayList<>(riskMeterData);
	}

	public List<OpenLVariationType> getVariationType() {
		return variationType != null ? new ArrayList<>(variationType) : null;
	}

	public void setVariationType(List<OpenLVariationType> variationType) {
		this.variationType = new ArrayList<>(variationType);
	}

	public List<OpenLClaim> getClaims() {
		return claims != null ? new ArrayList<>(claims) : null;
	}

	public void setClaims(List<OpenLClaim> claims) {
		this.claims = new ArrayList<>(claims);
	}

	@Override
	public List<OpenLFinalTest> getTests() {
		return Collections.unmodifiableList(tests);
	}

	@Override
	public List<HomeSSOpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<HomeSSOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	@Override
	public String toString() {
		return "HomeSSOpenLFile{" +
				"policies=" + policies +
				", forms=" + forms +
				", policyCoverageDeductible=" + policyCoverageDeductible +
				", policyConstructionInfo=" + policyConstructionInfo +
				", policyAddress=" + policyAddress +
				", policyNamedInsured=" + policyNamedInsured +
				", cappingDetails=" + cappingDetails +
				", policyDiscountInformation=" + policyDiscountInformation +
				", policyDwellingRatingInfo=" + policyDwellingRatingInfo +
				", coverages=" + coverages +
				", policyLossInformation=" + policyLossInformation +
				", claims=" + claims +
				", riskMeterData=" + riskMeterData +
				", variationType=" + variationType +
				", tests=" + tests +
				'}';
	}
}
