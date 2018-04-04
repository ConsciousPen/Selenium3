package aaa.helpers.openl.model.auto_ca.select;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class AutoCaSelectOpenLVehicle extends OpenLVehicle {
	@ExcelTableElement(sheetName = OpenLFile.COVERAGE_SHEET_NAME, headerRowIndex = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
	protected List<AutoCaSelectOpenLCoverage> coverages;

	@ExcelTableElement(sheetName = OpenLFile.DRIVER_SHEET_NAME, headerRowIndex = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<AutoCaSelectOpenLDriver> primaryDriver;

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelTableColumnElement(name = "umbiLiabilitySymbol")
	private String umLiabilitySymbol;

	private Boolean aaaMembership;
	private Boolean applyFixedExpense;
	private String commuteBand;
	private Boolean ete;
	private Boolean fullGlassCoverage;
	private Boolean gapCoverage;
	private Boolean multiCarInd;
	private Boolean newCarProtection;
	private Boolean oemCoverage;
	private Boolean rideShareCov;
	private Integer vehicleAge;
	private Boolean manuallyAssignedDriver; // TODO-dchubkov: double check type, column in test is empty
	private Boolean manuallyAssignedUndesignatedDriverInd;
	private String optionalCoverages; // TODO-dchubkov: double check type, column in test is empty

	public List<AutoCaSelectOpenLDriver> getPrimaryDriver() {
		return new ArrayList<>(primaryDriver);
	}

	public void setPrimaryDriver(List<AutoCaSelectOpenLDriver> primaryDriver) {
		this.primaryDriver = new ArrayList<>(primaryDriver);
	}

	public Boolean getAaaMembership() {
		return aaaMembership;
	}

	public void setAaaMembership(Boolean aaaMembership) {
		this.aaaMembership = aaaMembership;
	}

	public Boolean getApplyFixedExpense() {
		return applyFixedExpense;
	}

	public void setApplyFixedExpense(Boolean applyFixedExpense) {
		this.applyFixedExpense = applyFixedExpense;
	}

	public String getCommuteBand() {
		return commuteBand;
	}

	public void setCommuteBand(String commuteBand) {
		this.commuteBand = commuteBand;
	}

	public Boolean getEte() {
		return ete;
	}

	public void setEte(Boolean ete) {
		this.ete = ete;
	}

	public Boolean getFullGlassCoverage() {
		return fullGlassCoverage;
	}

	public void setFullGlassCoverage(Boolean fullGlassCoverage) {
		this.fullGlassCoverage = fullGlassCoverage;
	}

	public Boolean getGapCoverage() {
		return gapCoverage;
	}

	public void setGapCoverage(Boolean gapCoverage) {
		this.gapCoverage = gapCoverage;
	}

	public Boolean getMultiCarInd() {
		return multiCarInd;
	}

	public void setMultiCarInd(Boolean multiCarInd) {
		this.multiCarInd = multiCarInd;
	}

	public Boolean getNewCarProtection() {
		return newCarProtection;
	}

	public void setNewCarProtection(Boolean newCarProtection) {
		this.newCarProtection = newCarProtection;
	}

	public Boolean getOemCoverage() {
		return oemCoverage;
	}

	public void setOemCoverage(Boolean oemCoverage) {
		this.oemCoverage = oemCoverage;
	}

	public Boolean getRideShareCov() {
		return rideShareCov;
	}

	public void setRideShareCov(Boolean rideShareCov) {
		this.rideShareCov = rideShareCov;
	}

	public Integer getVehicleAge() {
		return vehicleAge;
	}

	public void setVehicleAge(Integer vehicleAge) {
		this.vehicleAge = vehicleAge;
	}

	@Override
	public List<AutoCaSelectOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<AutoCaSelectOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	@SuppressWarnings("RedundantMethodOverride")
	@Override
	public void setMpLiabilitySymbol(String mpLiabilitySymbol) {
		this.mpLiabilitySymbol = mpLiabilitySymbol;
	}

	@Override
	public String getUmLiabilitySymbol() {
		return umLiabilitySymbol;
	}

	public Boolean isManuallyAssignedDriver() {
		return manuallyAssignedDriver;
	}

	public void setManuallyAssignedDriver(Boolean manuallyAssignedDriver) {
		this.manuallyAssignedDriver = manuallyAssignedDriver;
	}

	public Boolean istManuallyAssignedUndesignatedDriverInd() {
		return manuallyAssignedUndesignatedDriverInd;
	}

	public void setManuallyAssignedUndesignatedDriverInd(Boolean manuallyAssignedUndesignatedDriverInd) {
		this.manuallyAssignedUndesignatedDriverInd = manuallyAssignedUndesignatedDriverInd;
	}

	public String getOptionalCoverages() {
		return optionalCoverages;
	}

	public void setOptionalCoverages(String optionalCoverages) {
		this.optionalCoverages = optionalCoverages;
	}

	@Override
	public String toString() {
		return "AutoCaSelectOpenLVehicle{" +
				"coverages=" + coverages +
				", primaryDriver=" + primaryDriver +
				", umLiabilitySymbol='" + umLiabilitySymbol + '\'' +
				", aaaMembership=" + aaaMembership +
				", applyFixedExpense=" + applyFixedExpense +
				", commuteBand='" + commuteBand + '\'' +
				", ete=" + ete +
				", fullGlassCoverage=" + fullGlassCoverage +
				", gapCoverage=" + gapCoverage +
				", multiCarInd=" + multiCarInd +
				", newCarProtection=" + newCarProtection +
				", oemCoverage=" + oemCoverage +
				", rideShareCov=" + rideShareCov +
				", vehicleAge=" + vehicleAge +
				", manuallyAssignedDriver=" + manuallyAssignedDriver +
				", manuallyAssignedUndesignatedDriverInd=" + manuallyAssignedUndesignatedDriverInd +
				", optionalCoverages='" + optionalCoverages + '\'' +
				", number=" + number +
				", annualMileage=" + annualMileage +
				", collSymbol=" + collSymbol +
				", compSymbol=" + compSymbol +
				", id='" + id + '\'' +
				", modelYear=" + modelYear +
				", statCode='" + statCode + '\'' +
				", oldStatCode='" + oldStatCode + '\'' +
				", biLiabilitySymbol='" + biLiabilitySymbol + '\'' +
				", pdLiabilitySymbol='" + pdLiabilitySymbol + '\'' +
				", mpLiabilitySymbol='" + mpLiabilitySymbol + '\'' +
				", umLiabilitySymbol='" + umLiabilitySymbol + '\'' +
				", address=" + address +
				'}';
	}
}
