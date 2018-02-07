package aaa.helpers.openl.model.pup;

import static aaa.helpers.openl.model.OpenLFile.COVERAGE_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.OpenLFile.DWELLING_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.pup.PUPOpenLFile.PUP_COVERAGE_SHEET_NAME;
import static aaa.helpers.openl.model.pup.PUPOpenLFile.PUP_DWELLING_SHEET_NAME;
import static aaa.helpers.openl.model.pup.PUPOpenLFile.PUP_RISK_ITEM_HEADER_ROW_NUMBER;
import static aaa.helpers.openl.model.pup.PUPOpenLFile.PUP_RISK_ITEM_SHEET_NAME;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.ExcelTableElement;

public class PUPOpenLPolicy extends OpenLPolicy {
	@ExcelTableElement(sheetName = PUP_COVERAGE_SHEET_NAME, headerRowNumber = COVERAGE_HEADER_ROW_NUMBER)
	private List<PUPOpenLCoverage> coverages;

	@ExcelTableElement(sheetName = PUP_DWELLING_SHEET_NAME, headerRowNumber = DWELLING_HEADER_ROW_NUMBER)
	private List<OpenLDwelling> dwelling;

	@ExcelTableElement(sheetName = PUP_RISK_ITEM_SHEET_NAME, headerRowNumber = PUP_RISK_ITEM_HEADER_ROW_NUMBER)
	private List<OpenLRiskItem> riskItems;

	private String autoTier;
	private Boolean businessPursuitsInd;
	private Integer daycareChildrenCount;
	private Boolean dropDownInd;
	private LocalDateTime effectiveDate;
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

	public List<PUPOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<PUPOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public List<OpenLDwelling> getDwelling() {
		return new ArrayList<>(dwelling);
	}

	public void setDwelling(List<OpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
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

	public LocalDateTime getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(LocalDateTime effectiveDate) {
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
