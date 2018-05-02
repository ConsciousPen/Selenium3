package aaa.helpers.openl.model.home_ss;

import static aaa.helpers.openl.model.OpenLFile.POLICY_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.POLICY_SHEET_NAME;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowIndex = POLICY_HEADER_ROW_NUMBER)
public class HomeSSOpenLPolicy extends OpenLPolicy {

	private HomeSSOpneLCappingDetails cappingDetails;
	private List<HomeSSOpenLCoverage> coverages;
	private List<HomeSSOpenLForm> forms;
	private HomeSSOpenLAddress policyAddress;
	private OpenLConstructionInfo policyConstructionInfo;
	private OpenLCoverageDeductible policyCoverageDeductible;
	private OpenLDiscountInformation policyDiscountInformation;
	private OpenLDwellingRatingInfo policyDwellingRatingInfo;
	private OpenLLossInformation policyLossInformation;
	private OpenLNamedInsured policyNamedInsured;
	private OpenLRiskMeterData riskMeterData; // NJ Specific

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelColumnElement(name = "id")
	private String policyNumber;

	private LocalDate effectiveDate;
	private Boolean isVariationRequest;
	private String level;
	private String policyType;
	private String prevLevel;
	private String transactionType;
	private String chamberOfCommerce; // NJ specific ?
	private String profession; // OK specific ?

	public HomeSSOpneLCappingDetails getCappingDetails() {
		return cappingDetails;
	}

	public void setCappingDetails(HomeSSOpneLCappingDetails cappingDetails) {
		this.cappingDetails = cappingDetails;
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

	public HomeSSOpenLAddress getPolicyAddress() {
		return policyAddress;
	}

	public void setPolicyAddressHomeSSOpenLAddress(HomeSSOpenLAddress policyAddress) {
		this.policyAddress = policyAddress;
	}

	public OpenLConstructionInfo getPolicyConstructionInfo() {
		return policyConstructionInfo;
	}

	public void setPolicyConstructionInfo(OpenLConstructionInfo policyConstructionInfo) {
		this.policyConstructionInfo = policyConstructionInfo;
	}

	public OpenLCoverageDeductible getPolicyCoverageDeductible() {
		return policyCoverageDeductible;
	}

	public void setPolicyCoverageDeductible(OpenLCoverageDeductible policyCoverageDeductible) {
		this.policyCoverageDeductible = policyCoverageDeductible;
	}

	public OpenLDiscountInformation getPolicyDiscountInformation() {
		return policyDiscountInformation;
	}

	public void setPolicyDiscountInformation(OpenLDiscountInformation policyDiscountInformation) {
		this.policyDiscountInformation = policyDiscountInformation;
	}

	public OpenLDwellingRatingInfo getPolicyDwellingRatingInfo() {
		return policyDwellingRatingInfo;
	}

	public void setPolicyDwellingRatingInfo(OpenLDwellingRatingInfo policyDwellingRatingInfo) {
		this.policyDwellingRatingInfo = policyDwellingRatingInfo;
	}

	public OpenLLossInformation getPolicyLossInformation() {
		return policyLossInformation;
	}

	public void setPolicyLossInformation(OpenLLossInformation policyLossInformation) {
		this.policyLossInformation = policyLossInformation;
	}

	public OpenLNamedInsured getPolicyNamedInsured() {
		return policyNamedInsured;
	}

	public void setPolicyNamedInsured(OpenLNamedInsured policyNamedInsured) {
		this.policyNamedInsured = policyNamedInsured;
	}

	public OpenLRiskMeterData getRiskMeterData() {
		return riskMeterData;
	}

	public void setRiskMeterData(OpenLRiskMeterData riskMeterData) {
		this.riskMeterData = riskMeterData;
	}

	public Boolean getVariationRequest() {
		return isVariationRequest;
	}

	public void setVariationRequest(Boolean variationRequest) {
		isVariationRequest = variationRequest;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getPolicyType() {
		return policyType;
	}

	public void setPolicyType(String policyType) {
		this.policyType = policyType;
	}

	public String getPrevLevel() {
		return prevLevel;
	}

	public void setPrevLevel(String prevLevel) {
		this.prevLevel = prevLevel;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public String getChamberOfCommerce() {
		return chamberOfCommerce;
	}

	public void setChamberOfCommerce(String chamberOfCommerce) {
		this.chamberOfCommerce = chamberOfCommerce;
	}

	public String getProfession() {
		return profession;
	}

	public void setProfession(String profession) {
		this.profession = profession;
	}

	@Override
	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public String getPolicyNumber() {
		return policyNumber;
	}

	@Override
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	@Override
	public Integer getTerm() {
		Integer term = getCappingDetails().getTerm();
		//TODO-dchubkov: to be verified whether 12 is OK for default term or not
		return term != null ? term : 12;
	}

	@Override
	public String getUnderwriterCode() {
		return getCappingDetails().getUnderwriterCode();
	}

	@Override
	public String toString() {
		return "HomeSSOpenLPolicy{" +
				"cappingDetails=" + cappingDetails +
				", coverages=" + coverages +
				", forms=" + forms +
				", policyAddress=" + policyAddress +
				", policyConstructionInfo=" + policyConstructionInfo +
				", policyCoverageDeductible=" + policyCoverageDeductible +
				", policyDiscountInformation=" + policyDiscountInformation +
				", policyDwellingRatingInfo=" + policyDwellingRatingInfo +
				", policyLossInformation=" + policyLossInformation +
				", policyNamedInsured=" + policyNamedInsured +
				", riskMeterData=" + riskMeterData +
				", policyNumber='" + policyNumber + '\'' +
				", effectiveDate=" + effectiveDate +
				", isVariationRequest=" + isVariationRequest +
				", level='" + level + '\'' +
				", policyType='" + policyType + '\'' +
				", prevLevel='" + prevLevel + '\'' +
				", transactionType='" + transactionType + '\'' +
				", chamberOfCommerce='" + chamberOfCommerce + '\'' +
				", profession='" + profession + '\'' +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
