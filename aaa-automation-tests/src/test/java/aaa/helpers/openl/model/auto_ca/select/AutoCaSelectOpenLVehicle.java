package aaa.helpers.openl.model.auto_ca.select;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.VEHICLE_SHEET_NAME, headerRowIndex = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
public class AutoCaSelectOpenLVehicle extends OpenLVehicle {

	@RequiredField
	private List<AutoCaSelectOpenLCoverage> coverages;

	private List<AutoCaSelectOpenLCoverage> optionalCoverages;

	@RequiredField
	private AutoCaSelectOpenLDriver primaryDriver;

	private AutoCaSelectOpenLDriver manuallyAssignedDriver;

	@SuppressWarnings({"FieldNameHidesFieldInSuperclass"})
	@ExcelColumnElement(name = "umbiLiabilitySymbol")
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

	@RequiredField
	private Integer vehicleAge;
	private Boolean manuallyAssignedUndesignatedDriverInd;

	public AutoCaSelectOpenLDriver getPrimaryDriver() {
		return primaryDriver;
	}

	public void setPrimaryDriver(AutoCaSelectOpenLDriver primaryDriver) {
		this.primaryDriver = primaryDriver;
	}

	public AutoCaSelectOpenLDriver getManuallyAssignedDriver() {
		return manuallyAssignedDriver;
	}

	public void setManuallyAssignedDriver(AutoCaSelectOpenLDriver manuallyAssignedDriver) {
		this.manuallyAssignedDriver = manuallyAssignedDriver;
	}

	public Boolean isAaaMembership() {
		return aaaMembership;
	}

	public void setAaaMembership(Boolean aaaMembership) {
		this.aaaMembership = aaaMembership;
	}

	public Boolean isApplyFixedExpense() {
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

	public Boolean isEte() {
		return ete;
	}

	public void setEte(Boolean ete) {
		this.ete = ete;
	}

	public Boolean isFullGlassCoverage() {
		return fullGlassCoverage;
	}

	public void setFullGlassCoverage(Boolean fullGlassCoverage) {
		this.fullGlassCoverage = fullGlassCoverage;
	}

	public Boolean isGapCoverage() {
		return gapCoverage;
	}

	public void setGapCoverage(Boolean gapCoverage) {
		this.gapCoverage = gapCoverage;
	}

	public Boolean isMultiCarInd() {
		return multiCarInd;
	}

	public void setMultiCarInd(Boolean multiCarInd) {
		this.multiCarInd = multiCarInd;
	}

	public Boolean isNewCarProtection() {
		return newCarProtection;
	}

	public void setNewCarProtection(Boolean newCarProtection) {
		this.newCarProtection = newCarProtection;
	}

	public Boolean isOemCoverage() {
		return oemCoverage;
	}

	public void setOemCoverage(Boolean oemCoverage) {
		this.oemCoverage = oemCoverage;
	}

	public Boolean isRideShareCov() {
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

	public Boolean istManuallyAssignedUndesignatedDriverInd() {
		return manuallyAssignedUndesignatedDriverInd;
	}

	public void setManuallyAssignedUndesignatedDriverInd(Boolean manuallyAssignedUndesignatedDriverInd) {
		this.manuallyAssignedUndesignatedDriverInd = manuallyAssignedUndesignatedDriverInd;
	}

	public List<AutoCaSelectOpenLCoverage> getOptionalCoverages() {
		return CollectionUtils.isNotEmpty(optionalCoverages) ? new ArrayList<>(optionalCoverages) : null;
	}

	public void setOptionalCoverages(List<AutoCaSelectOpenLCoverage> optionalCoverages) {
		this.optionalCoverages = CollectionUtils.isNotEmpty(optionalCoverages) ? new ArrayList<>(optionalCoverages) : null;
	}
}
