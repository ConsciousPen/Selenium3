package aaa.helpers.openl.model.pup;

import static aaa.helpers.openl.model.pup.PUPOpenLFile.PUP_POLICY_SHEET_NAME;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.mock.model.address.AddressReferenceMock;
import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;
import aaa.helpers.mock.model.property_risk_reports.RetrievePropertyRiskReportsMock;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.mock_generator.MockGenerator;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_generator.PUPTestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;
import toolkit.datax.TestData;

@ExcelTableElement(sheetName = PUP_POLICY_SHEET_NAME, headerRowIndex = PUPOpenLFile.POLICY_HEADER_ROW_NUMBER)
public class PUPOpenLPolicy extends OpenLPolicy {

	@RequiredField
	private List<PUPOpenLCoverage> coverages;

	@RequiredField
	private PUPOpenLDwelling dwelling;

	@RequiredField
	private List<PUPOpenLRiskItem> riskItems;

	@RequiredField
	private String autoTier;
	private Boolean businessPursuitsInd;
	private Integer daycareChildrenCount;
	private Boolean dropDownInd;

	@RequiredField
	private LocalDate effectiveDate;

	@RequiredField
	private String homeTier;

	private Boolean incidentalFarmingInd;

	@RequiredField
	private Integer numOfAccidents;

	private Integer numOfAddlResidences;
	private Integer numOfNanoVehicles;
	private Integer numOfSeniorOps;

	@RequiredField
	private Integer numOfViolations;

	private Integer numOfYouthfulOps;
	private Boolean permittedOccupancyInd;

	@RequiredField
	private Integer rentalUnitsCount;

	private String signature;

	@ExcelTransient
	private Integer term;

	public List<PUPOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<PUPOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public PUPOpenLDwelling getDwelling() {
		return dwelling;
	}

	public void setDwelling(PUPOpenLDwelling dwelling) {
		this.dwelling = dwelling;
	}

	public List<PUPOpenLRiskItem> getRiskItems() {
		return new ArrayList<>(riskItems);
	}

	public void setRiskItems(List<PUPOpenLRiskItem> riskItems) {
		this.riskItems = new ArrayList<>(riskItems);
	}

	public String getAutoTier() {
		return autoTier;
	}

	public void setAutoTier(String autoTier) {
		this.autoTier = autoTier;
	}

	public Boolean getBusinessPursuitsInd() {
		return businessPursuitsInd;
	}

	public void setBusinessPursuitsInd(Boolean businessPursuitsInd) {
		this.businessPursuitsInd = businessPursuitsInd;
	}

	public Integer getDaycareChildrenCount() {
		return daycareChildrenCount;
	}

	public void setDaycareChildrenCount(Integer daycareChildrenCount) {
		this.daycareChildrenCount = daycareChildrenCount;
	}

	public Boolean getDropDownInd() {
		return dropDownInd;
	}

	public void setDropDownInd(Boolean dropDownInd) {
		this.dropDownInd = dropDownInd;
	}

	@Override
	public LocalDate getEffectiveDate() {
		return effectiveDate;
	}

	@Override
	public PUPTestDataGenerator getTestDataGenerator(TestData baseTestData) {
		return new PUPTestDataGenerator(this.getState(), baseTestData);
	}

	@Override
	public MocksCollection getRequiredMocks() {
		MocksCollection requiredMocks = new MocksCollection();
		MockGenerator mockGenerator = new MockGenerator();

		if (!mockGenerator.isPropertyClassificationMockPresent()) {
			RetrievePropertyClassificationMock propertyClassificationMock = mockGenerator.getRetrievePropertyClassificationMock();
			requiredMocks.add(propertyClassificationMock);
		}

		if (!mockGenerator.isPropertyRiskReportsMockPresent()) {
			RetrievePropertyRiskReportsMock propertyRiskReportsMockData = mockGenerator.getRetrievePropertyRiskReportsMock();
			requiredMocks.add(propertyRiskReportsMockData);
		}

		if (!mockGenerator.isAddressReferenceMockPresent(getDwelling().getAddress().getZipCode(), getState())) {
			AddressReferenceMock addressReferenceMock = mockGenerator.getAddressReferenceMock(getDwelling().getAddress().getZipCode(), getState());
			requiredMocks.add(addressReferenceMock);
		}

		return requiredMocks;
	}

	@Override
	public Map<String, String> getFilteredOpenLFieldsMap() {
		return removeOpenLFields(super.getFilteredOpenLFieldsMap(),
				//do not affect rating:
				"policy.dwelling.viciousDogCount",
				"policy.dwelling.address.county");
	}

	public void setEffectiveDate(LocalDate effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getHomeTier() {
		return homeTier;
	}

	public void setHomeTier(String homeTier) {
		this.homeTier = homeTier;
	}

	public Boolean getIncidentalFarmingInd() {
		return incidentalFarmingInd;
	}

	public void setIncidentalFarmingInd(Boolean incidentalFarmingInd) {
		this.incidentalFarmingInd = incidentalFarmingInd;
	}

	public Integer getNumOfAccidents() {
		return numOfAccidents;
	}

	public void setNumOfAccidents(Integer numOfAccidents) {
		this.numOfAccidents = numOfAccidents;
	}

	public Integer getNumOfAddlResidences() {
		return numOfAddlResidences;
	}

	public void setNumOfAddlResidences(Integer numOfAddlResidences) {
		this.numOfAddlResidences = numOfAddlResidences;
	}

	public Integer getNumOfNanoVehicles() {
		return numOfNanoVehicles;
	}

	public void setNumOfNanoVehicles(Integer numOfNanoVehicles) {
		this.numOfNanoVehicles = numOfNanoVehicles;
	}

	public Integer getNumOfSeniorOps() {
		return numOfSeniorOps;
	}

	public void setNumOfSeniorOps(Integer numOfSeniorOps) {
		this.numOfSeniorOps = numOfSeniorOps;
	}

	public Integer getNumOfViolations() {
		return numOfViolations;
	}

	public void setNumOfViolations(Integer numOfViolations) {
		this.numOfViolations = numOfViolations;
	}

	public Integer getNumOfYouthfulOps() {
		return numOfYouthfulOps;
	}

	public void setNumOfYouthfulOps(Integer numOfYouthfulOps) {
		this.numOfYouthfulOps = numOfYouthfulOps;
	}

	public Boolean getPermittedOccupancyInd() {
		return permittedOccupancyInd;
	}

	public void setPermittedOccupancyInd(Boolean permittedOccupancyInd) {
		this.permittedOccupancyInd = permittedOccupancyInd;
	}

	public Integer getRentalUnitsCount() {
		return rentalUnitsCount;
	}

	public void setRentalUnitsCount(Integer rentalUnitsCount) {
		this.rentalUnitsCount = rentalUnitsCount;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	@Override
	public PolicyType getTestPolicyType() {
		return PolicyType.PUP;
	}

	@Override
	public Integer getTerm() {
		return term != null ? term : 12;
	}

	public void setTerm(Integer term) {
		this.term = term;
	}

	@Override
	public String getUnderwriterCode() {
		return null;
	}

	@Override
	public boolean isLegacyConvPolicy() {
		return false;
	}

	@Override
	public boolean isCappedPolicy() {
		return false;
	}

	@Override
	public boolean isNewRenPasCappedPolicy() {
		return !isLegacyConvPolicy() && isCappedPolicy();
	}
}
