package aaa.helpers.openl.model.home_ss;

import static aaa.helpers.openl.model.OpenLFile.ADDRESS_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.ADDRESS_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.CONSTRUCTION_INFO_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.CONSTRUCTION_INFO_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.COVERAGE_DEDUCTIBLE_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.COVERAGE_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.COVERAGE_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.DISCOUNT_INFORMATION_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.DISCOUNT_INFORMATION_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.DWELLING_RATING_INFO_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.DWELLING_RATING_INFO_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.FORM_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.FORM_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.LOSS_INFORMATION_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.LOSS_INFORMATION_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.NAMED_INSURED_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.NAMED_INSURED_SHEET_NAME;
import static aaa.helpers.openl.model.OpenLFile.RISK_METER_DATA_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.RISK_METER_DATA_SHEET_NAME;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.OpenLCappingDetails;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class HomeSSOpenLPolicy extends OpenLPolicy {
	@ExcelTableElement(sheetName = OpenLFile.CAPPINGDETAILS_SHEET_NAME, headerRowIndex = OpenLFile.CAPPINGDETAILS_HEADER_ROW_NUMBER)
	private List<OpenLCappingDetails> cappingDetails;

	@ExcelTableElement(sheetName = COVERAGE_SHEET_NAME, headerRowIndex = COVERAGE_HEADER_ROW_NUMBER)
	private List<HomeSSOpenLCoverage> coverages;

	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowIndex = FORM_HEADER_ROW_NUMBER)
	private List<HomeSSOpenLForm> forms;

	@ExcelTableElement(sheetName = ADDRESS_SHEET_NAME, headerRowIndex = ADDRESS_HEADER_ROW_NUMBER)
	private List<HomeSSOpenLAddress> policyAddress;

	@ExcelTableElement(sheetName = CONSTRUCTION_INFO_SHEET_NAME, headerRowIndex = CONSTRUCTION_INFO_HEADER_ROW_NUMBER)
	private List<OpenLConstructionInfo> policyConstructionInfo;

	@ExcelTableElement(sheetName = COVERAGE_DEDUCTIBLE_SHEET_NAME, headerRowIndex = COVERAGE_HEADER_ROW_NUMBER)
	private List<OpenLCoverageDeductible> policyCoverageDeductible;

	@ExcelTableElement(sheetName = DISCOUNT_INFORMATION_SHEET_NAME, headerRowIndex = DISCOUNT_INFORMATION_HEADER_ROW_NUMBER)
	private List<OpenLDiscountInformation> policyDiscountInformation;

	@ExcelTableElement(sheetName = DWELLING_RATING_INFO_SHEET_NAME, headerRowIndex = DWELLING_RATING_INFO_HEADER_ROW_NUMBER)
	private List<OpenLDwellingRatingInfo> policyDwellingRatingInfo;

	@ExcelTableElement(sheetName = LOSS_INFORMATION_SHEET_NAME, headerRowIndex = LOSS_INFORMATION_HEADER_ROW_NUMBER)
	private List<OpenLLossInformation> policyLossInformation;

	@ExcelTableElement(sheetName = NAMED_INSURED_SHEET_NAME, headerRowIndex = NAMED_INSURED_HEADER_ROW_NUMBER)
	private List<OpenLNamedInsured> policyNamedInsured;

	@ExcelTableElement(sheetName = RISK_METER_DATA_SHEET_NAME, headerRowIndex = RISK_METER_DATA_HEADER_ROW_NUMBER)
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

	public List<OpenLCappingDetails> getCappingDetails() {
		return new ArrayList<>(cappingDetails);
	}

	public void setCappingDetails(List<OpenLCappingDetails> cappingDetails) {
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

	@Override
	public LocalDateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDateTime effectiveDate) {
		this.effectiveDate = effectiveDate;
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
	public String getPolicyNumber() {
		return policyNumber;
	}

	@Override
	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	@Override
	public Integer getTerm() {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException(String.format("Getting term for %s is not implemented", this.getClass().getSimpleName()));
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
