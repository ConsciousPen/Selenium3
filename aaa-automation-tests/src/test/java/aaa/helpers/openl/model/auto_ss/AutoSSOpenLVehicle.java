package aaa.helpers.openl.model.auto_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

@ExcelTableElement(containsSheetName = OpenLFile.VEHICLE_SHEET_NAME, headerRowIndex = OpenLFile.VEHICLE_HEADER_ROW_NUMBER)
public class AutoSSOpenLVehicle extends OpenLVehicle {

	@RequiredField
	protected List<AutoSSOpenLCoverage> coverages;
	private AutoSSOpenLDriver ratedDriver;

	@RequiredField
	private String airbagCode;

	@RequiredField
	private String antiTheftString;
	private Boolean isHybrid;
	private Boolean isTelematic; // OR specific
	private Boolean isTeenTelematic; // CT specific ?
	private Boolean newCarAddedProtection;
	private Integer safetyScore;
	@RequiredField
	private String usage;

	@RequiredField
	private Integer vehicleAge;

	private Boolean isABS; // NY specific
	private Boolean isDaytimeRunning; // NY specific
	private String firstOrAddlVehicle; // NJ specific
	private Integer maxDriverAge; // VA specific ?
	private Integer totalVehiclePoint; // VA specific ?

	@ExcelTransient
	private String vinCode = "";

	public String getAirbagCode() {
		return airbagCode;
	}

	public void setAirbagCode(String airbagCode) {
		this.airbagCode = airbagCode;
	}

	public String getAntiTheftString() {
		return antiTheftString;
	}

	public void setAntiTheftString(String antiTheftString) {
		this.antiTheftString = antiTheftString;
	}

	public Integer getSafetyScore() {
		return safetyScore;
	}

	public void setSafetyScore(Integer safetyScore) {
		this.safetyScore = safetyScore;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public Integer getVehicleAge() {
		return vehicleAge;
	}

	public void setVehicleAge(Integer vehicleAge) {
		this.vehicleAge = vehicleAge;
	}

	public AutoSSOpenLDriver getRatedDriver() {
		return ratedDriver;
	}

	public void setRatedDriver(AutoSSOpenLDriver ratedDriver) {
		this.ratedDriver = ratedDriver;
	}

	public String getFirstOrAddlVehicle() {
		return firstOrAddlVehicle;
	}

	public void setFirstOrAddlVehicle(String firstOrAddlVehicle) {
		this.firstOrAddlVehicle = firstOrAddlVehicle;
	}

	public Integer getMaxDriverAge() {
		return maxDriverAge;
	}

	public void setMaxDriverAge(Integer maxDriverAge) {
		this.maxDriverAge = maxDriverAge;
	}

	public Integer getTotalVehiclePoint() {
		return totalVehiclePoint;
	}

	public void setTotalVehiclePoint(Integer totalVehiclePoint) {
		this.totalVehiclePoint = totalVehiclePoint;
	}

	@Override
	public List<AutoSSOpenLCoverage> getCoverages() {
		return new ArrayList<>(coverages);
	}

	public void setCoverages(List<AutoSSOpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public void setHybrid(Boolean hybrid) {
		isHybrid = hybrid;
	}

	public void setTelematic(Boolean isTelematic) {
		this.isTelematic = isTelematic;
	}

	public void setNewCarAddedProtection(Boolean newCarAddedProtection) {
		this.newCarAddedProtection = newCarAddedProtection;
	}

	public void setTeenTelematic(Boolean isTeenTelematic) {
		this.isTeenTelematic = isTeenTelematic;
	}

	public void setABS(Boolean isABS) {
		this.isABS = isABS;
	}

	public void setDaytimeRunning(Boolean isDaytimeRunning) {
		this.isDaytimeRunning = isDaytimeRunning;
	}

	public Boolean isABS() {
		return isABS;
	}

	public Boolean isDaytimeRunning() {
		return isDaytimeRunning;
	}

	public Boolean isHybrid() {
		return isHybrid;
	}

	public Boolean isTelematic() {
		return isTelematic;
	}

	public Boolean isNewCarAddedProtection() {
		return newCarAddedProtection;
	}

	public Boolean isTeenTelematic() {
		return isTeenTelematic;
	}

	public String getVinCode() {return vinCode;}

	public void setVinCode(String vinCode) {this.vinCode = vinCode;}
}
