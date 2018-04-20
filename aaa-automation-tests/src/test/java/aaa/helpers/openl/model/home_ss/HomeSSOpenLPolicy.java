package aaa.helpers.openl.model.home_ss;

import static aaa.helpers.openl.model.OpenLFile.POLICY_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.POLICY_SHEET_NAME;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowIndex = POLICY_HEADER_ROW_NUMBER)
public class HomeSSOpenLPolicy extends OpenLPolicy {

	private List<HomeSSOpneLCappingDetails> cappingDetails;
	private List<HomeSSOpenLCoverage> coverages;
	private List<HomeSSOpenLForm> forms;
	private List<HomeSSOpenLAddress> policyAddress;
	private List<OpenLConstructionInfo> policyConstructionInfo;
	private List<OpenLCoverageDeductible> policyCoverageDeductible;
	private List<OpenLDiscountInformation> policyDiscountInformation;
	private List<OpenLDwellingRatingInfo> policyDwellingRatingInfo;
	private List<OpenLLossInformation> policyLossInformation;
	private List<OpenLNamedInsured> policyNamedInsured;
	private List<OpenLRiskMeterData> riskMeterData; // NJ Specific

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelTableColumnElement(name = "id")
	private String policyNumber;

	private LocalDateTime effectiveDate;
	private Boolean isVariationRequest;
	private String level;
	private String policyType;
	private String prevLevel;
	private String transactionType;
	private String chamberOfCommerce; // NJ specific ?
	private String profession; // OK specific ?

	public List<HomeSSOpneLCappingDetails> getCappingDetails() {
		return new ArrayList<>(cappingDetails);
	}

	public void setCappingDetails(List<HomeSSOpneLCappingDetails> cappingDetails) {
		this.cappingDetails = new ArrayList<>(cappingDetails);
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

	public List<HomeSSOpenLAddress> getPolicyAddress() {
		return new ArrayList<>(policyAddress);
	}

	public void setPolicyAddress(List<HomeSSOpenLAddress> policyAddress) {
		this.policyAddress = new ArrayList<>(policyAddress);
	}

	public List<OpenLConstructionInfo> getPolicyConstructionInfo() {
		return new ArrayList<>(policyConstructionInfo);
	}

	public void setPolicyConstructionInfo(List<OpenLConstructionInfo> policyConstructionInfo) {
		this.policyConstructionInfo = new ArrayList<>(policyConstructionInfo);
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

	public List<OpenLDwellingRatingInfo> getPolicyDwellingRatingInfo() {
		return new ArrayList<>(policyDwellingRatingInfo);
	}

	public void setPolicyDwellingRatingInfo(List<OpenLDwellingRatingInfo> policyDwellingRatingInfo) {
		this.policyDwellingRatingInfo = new ArrayList<>(policyDwellingRatingInfo);
	}

	public List<OpenLLossInformation> getPolicyLossInformation() {
		return new ArrayList<>(policyLossInformation);
	}

	public void setPolicyLossInformation(List<OpenLLossInformation> policyLossInformation) {
		this.policyLossInformation = new ArrayList<>(policyLossInformation);
	}

	public List<OpenLNamedInsured> getPolicyNamedInsured() {
		return new ArrayList<>(policyNamedInsured);
	}

	public void setPolicyNamedInsured(List<OpenLNamedInsured> policyNamedInsured) {
		this.policyNamedInsured = new ArrayList<>(policyNamedInsured);
	}

	public List<OpenLRiskMeterData> getRiskMeterData() {
		return new ArrayList<>(riskMeterData);
	}

	public void setRiskMeterData(List<OpenLRiskMeterData> riskMeterData) {
		this.riskMeterData = new ArrayList<>(riskMeterData);
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
	public LocalDateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDateTime effectiveDate) {
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
		Integer term = getCappingDetails().get(0).getTerm();
		//TODO-dchubkov: to be verified whether 12 is OK for default term or not
		return term != null ? term : 12;
	}

	@Override
	public String getUnderwriterCode() {
		return getCappingDetails().get(0).getUnderwriterCode();
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
