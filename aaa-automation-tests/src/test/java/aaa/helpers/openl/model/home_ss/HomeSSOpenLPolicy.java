package aaa.helpers.openl.model.home_ss;

import static aaa.helpers.openl.model.OpenLFile.POLICY_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.POLICY_SHEET_NAME;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.gson.JsonElement;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.mock.model.address.AddressReferenceMock;
import aaa.helpers.mock.model.membership.RetrieveMembershipSummaryMock;
import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;
import aaa.helpers.mock.model.property_risk_reports.RetrievePropertyRiskReportsMock;
import aaa.helpers.openl.mock_generator.HomeSSMockGenerator;
import aaa.helpers.openl.mock_generator.MockGenerator;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_generator.HomeSSTestDataGenerator;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import toolkit.datax.TestData;

//import aaa.helpers.openl.testdata_builder.HomeSSHO4TestDataGenerator;

@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowIndex = POLICY_HEADER_ROW_NUMBER)
public class HomeSSOpenLPolicy extends OpenLPolicy {
	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelColumnElement(name = "id")
	private String policyNumber;

	private String policyType;
	private String level;
	private String prevLevel;
	private List<HomeSSOpenLCoverage> coverages;
	private HomeSSOpenLAddress policyAddress;
	private OpenLNamedInsured policyNamedInsured;
	private OpenLDwellingRatingInfo policyDwellingRatingInfo;
	private OpenLConstructionInfo policyConstructionInfo;
	private OpenLCoverageDeductible policyCoverageDeductible;
	private OpenLLossInformation policyLossInformation;
	private OpenLDiscountInformation policyDiscountInformation;
	private String profession; // OK specific ?
	private String transactionType;
	private LocalDate effectiveDate;
	private List<HomeSSOpenLForm> forms;
	private OpenLRiskMeterData riskMeterData; // NJ Specific
	private String chamberOfCommerce; // NJ specific ?
	private LocalDate previousEffectiveDate;
	private HomeSSOpneLCappingDetails cappingDetails;
	private Boolean isVariationRequest;
	private String riskState;
	private String policyId;
	private String lob;
	private String productCd;
	private Boolean ignorable;
	private String renewalCycle;
	private String policyVersion;
	private List<OpenLVariationType> paymentPlanVariations;

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
		return forms != null ? new ArrayList<>(forms) : Collections.emptyList();
	}

	public void setForms(List<HomeSSOpenLForm> forms) {
		this.forms = forms != null ? new ArrayList<>(forms) : Collections.emptyList();
	}

	public HomeSSOpenLAddress getPolicyAddress() {
		return policyAddress;
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

	public LocalDate getPreviousEffectiveDate() {
		return previousEffectiveDate;
	}

	public void setPreviousEffectiveDate(LocalDate previousEffectiveDate) {
		this.previousEffectiveDate = previousEffectiveDate;
	}

	public String getRiskState() {
		return riskState;
	}

	public void setRiskState(String riskState) {
		this.riskState = riskState;
	}

	public String getPolicyId() {
		return policyId;
	}

	public void setPolicyId(String policyId) {
		this.policyId = policyId;
	}

	public String getLob() {
		return lob;
	}

	public void setLob(String lob) {
		this.lob = lob;
	}

	public String getProductCd() {
		return productCd;
	}

	public void setProductCd(String productCd) {
		this.productCd = productCd;
	}

	public Boolean getIgnorable() {
		return ignorable;
	}

	public void setIgnorable(Boolean ignorable) {
		this.ignorable = ignorable;
	}

	public String getRenewalCycle() {
		return renewalCycle;
	}

	public void setRenewalCycle(String renewalCycle) {
		this.renewalCycle = renewalCycle;
	}

	public String getPolicyVersion() {
		return policyVersion;
	}

	public void setPolicyVersion(String policyVersion) {
		this.policyVersion = policyVersion;
	}

	public List<OpenLVariationType> getPaymentPlanVariations() {
		return paymentPlanVariations;
	}

	public void setPaymentPlanVariations(List<OpenLVariationType> paymentPlanVariations) {
		this.paymentPlanVariations = paymentPlanVariations;
	}

	public void setPolicyAddressHomeSSOpenLAddress(HomeSSOpenLAddress policyAddress) {
		this.policyAddress = policyAddress;
	}

	public void setVariationRequest(Boolean isVariationRequest) {
		this.isVariationRequest = isVariationRequest;
	}

	@Override
	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@Override
	public HomeSSTestDataGenerator getTestDataGenerator(String state, TestData baseTestData) {
		return new HomeSSTestDataGenerator(state, baseTestData);
	}

	@Override
	public HomeSSOpenLPolicy createFrom(JsonElement jsonElement) {
		return new HomeSSOpenLPolicy();
	}

	@Override
	public MocksCollection getRequiredMocks() {
		MocksCollection requiredMocks = new MocksCollection();
		MockGenerator mockGenerator = new HomeSSMockGenerator();
		Integer memberPersistency = getPolicyDiscountInformation().getMemberPersistency();

		if (!mockGenerator.isMembershipSummaryMockPresent(getEffectiveDate(), memberPersistency)) {
			RetrieveMembershipSummaryMock membershipMock = mockGenerator.getRetrieveMembershipSummaryMock(getEffectiveDate(), memberPersistency);
			requiredMocks.add(membershipMock);
		}

		if (!mockGenerator.isPropertyClassificationMockPresent()) {
			RetrievePropertyClassificationMock propertyClassificationMock = mockGenerator.getRetrievePropertyClassificationMock();
			requiredMocks.add(propertyClassificationMock);
		}

		if (!mockGenerator.isPropertyRiskReportsMockPresent()) {
			RetrievePropertyRiskReportsMock propertyRiskReportsMockData = mockGenerator.getRetrievePropertyRiskReportsMock();
			requiredMocks.add(propertyRiskReportsMockData);
		}

		if (!mockGenerator.isAddressReferenceMockPresent(getPolicyAddress().getZip(), getState())) {
			AddressReferenceMock addressReferenceMock = mockGenerator.getAddressReferenceMock(getPolicyAddress().getZip(), getState());
			requiredMocks.add(addressReferenceMock);
		}

		return requiredMocks;
	}

	/*
	@Override
	public HomeSSHO4TestDataGenerator getTestDataGenerator(String state, TestData baseTestData) {
		return new HomeSSHO4TestDataGenerator(state, baseTestData);
	}
	*/

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
				"policyNumber='" + policyNumber + '\'' +
				", policyType='" + policyType + '\'' +
				", level='" + level + '\'' +
				", prevLevel='" + prevLevel + '\'' +
				", coverages=" + coverages +
				", policyAddress=" + policyAddress +
				", policyNamedInsured=" + policyNamedInsured +
				", policyDwellingRatingInfo=" + policyDwellingRatingInfo +
				", policyConstructionInfo=" + policyConstructionInfo +
				", policyCoverageDeductible=" + policyCoverageDeductible +
				", policyLossInformation=" + policyLossInformation +
				", policyDiscountInformation=" + policyDiscountInformation +
				", profession='" + profession + '\'' +
				", transactionType='" + transactionType + '\'' +
				", effectiveDate=" + effectiveDate +
				", forms=" + forms +
				", riskMeterData=" + riskMeterData +
				", chamberOfCommerce='" + chamberOfCommerce + '\'' +
				", previousEffectiveDate=" + previousEffectiveDate +
				", cappingDetails=" + cappingDetails +
				", isVariationRequest=" + isVariationRequest +
				", riskState='" + riskState + '\'' +
				", policyId='" + policyId + '\'' +
				", lob='" + lob + '\'' +
				", productCd='" + productCd + '\'' +
				", ignorable=" + ignorable +
				", renewalCycle='" + renewalCycle + '\'' +
				", policyVersion='" + policyVersion + '\'' +
				", paymentPlanVariations=" + paymentPlanVariations +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}

	public Boolean isVariationRequest() {
		return isVariationRequest;
	}
}
