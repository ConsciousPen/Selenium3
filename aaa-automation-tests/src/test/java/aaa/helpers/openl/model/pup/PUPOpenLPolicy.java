package aaa.helpers.openl.model.pup;

import static aaa.helpers.openl.model.pup.PUPOpenLFile.PUP_POLICY_SHEET_NAME;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.mock.MocksCollection;
import aaa.helpers.mock.model.property_classification.RetrievePropertyClassificationMock;
import aaa.helpers.mock.model.property_risk_reports.RetrievePropertyRiskReportsMock;
import aaa.helpers.openl.mock_generator.MockGenerator;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.helpers.openl.testdata_builder.PUPTestDataGenerator;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import toolkit.datax.TestData;

@ExcelTableElement(sheetName = PUP_POLICY_SHEET_NAME, headerRowIndex = PUPOpenLFile.POLICY_HEADER_ROW_NUMBER)
public class PUPOpenLPolicy extends OpenLPolicy {

	private List<PUPOpenLCoverage> coverages;
	private OpenLDwelling dwelling;
	private List<OpenLRiskItem> riskItems;

	private String autoTier;
	private Boolean businessPursuitsInd;
	private Integer daycareChildrenCount;
	private Boolean dropDownInd;
	private LocalDate effectiveDate;
	private String homeTier;
	private Boolean incidentalFarmingInd;
	private Integer numOfAccidents;
	private Integer numOfAddlResidences;
	private Integer numOfNanoVehicles;
	private Integer numOfSeniorOps;
	private Integer numOfViolations;
	private Integer numOfYouthfulOps;
	private Boolean permittedOccupancyInd;
	private Integer rentalUnitsCount;
	private String signature;
	private Integer term;

	public List<PUPOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<PUPOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public OpenLDwelling getDwelling() {
		return dwelling;
	}

	public void setDwelling(OpenLDwelling dwelling) {
		this.dwelling = dwelling;
	}

	public List<OpenLRiskItem> getRiskItems() {
		return new ArrayList<>(riskItems);
	}

	public void setRiskItems(List<OpenLRiskItem> riskItems) {
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
	public PUPTestDataGenerator getTestDataGenerator(String state, TestData baseTestData) {
		return new PUPTestDataGenerator(state, baseTestData);
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

		return requiredMocks;
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
	public String toString() {
		return "PUPOpenLPolicy{" +
				"coverages=" + coverages +
				", dwelling=" + dwelling +
				", riskItems=" + riskItems +
				", autoTier='" + autoTier + '\'' +
				", businessPursuitsInd=" + businessPursuitsInd +
				", daycareChildrenCount=" + daycareChildrenCount +
				", dropDownInd=" + dropDownInd +
				", effectiveDate=" + effectiveDate +
				", homeTier='" + homeTier + '\'' +
				", incidentalFarmingInd=" + incidentalFarmingInd +
				", numOfAccidents=" + numOfAccidents +
				", numOfAddlResidences=" + numOfAddlResidences +
				", numOfNanoVehicles=" + numOfNanoVehicles +
				", numOfSeniorOps=" + numOfSeniorOps +
				", numOfViolations=" + numOfViolations +
				", numOfYouthfulOps=" + numOfYouthfulOps +
				", permittedOccupancyInd=" + permittedOccupancyInd +
				", rentalUnitsCount=" + rentalUnitsCount +
				", signature='" + signature + '\'' +
				", number=" + number +
				", policyNumber='" + policyNumber + '\'' +
				'}';
	}
}
