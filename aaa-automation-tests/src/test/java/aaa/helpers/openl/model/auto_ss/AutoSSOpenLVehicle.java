package aaa.helpers.openl.model.auto_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLDriver;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.utils.excel.bind.ExcelTableElement;

public class AutoSSOpenLVehicle extends OpenLVehicle {
	private String airbagCode;
	private String antiTheftString;
	private Boolean isHybrid;
	private Boolean isTelematic; // OR specific
	private Boolean isTeenTelematic; // CT specific ?
	private Boolean newCarAddedProtection;
	private Integer safetyScore;
	private String usage;
	private Integer vehicleAge;
	private Boolean isABS; // DC specific ?
	private Boolean isDaytimeRunning; // DC specific ?
	private String firstOrAddlVehicle; // NJ specific ?
	private Integer maxDriverAge; // VA specific ?
	private Integer totalVehiclePoint; // VA specific ?

	@ExcelTableElement(sheetName = "Batch- DriverAZ", headerRowNumber = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<OpenLDriver> ratedDriver;

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

	public List<OpenLDriver> getRatedDriver() {
		return new ArrayList<>(ratedDriver);
	}

	public void setRatedDriver(List<OpenLDriver> ratedDriver) {
		this.ratedDriver = new ArrayList<>(ratedDriver);
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

	@Override
	public String toString() {
		return "AutoSSOpenLVehicle{" +
				"airbagCode='" + airbagCode + '\'' +
				", antiTheftString='" + antiTheftString + '\'' +
				", isHybrid=" + isHybrid +
				", isTelematic=" + isTelematic +
				", isTeenTelematic=" + isTeenTelematic +
				", newCarAddedProtection=" + newCarAddedProtection +
				", safetyScore=" + safetyScore +
				", usage='" + usage + '\'' +
				", vehicleAge=" + vehicleAge +
				", isABS=" + isABS +
				", isDaytimeRunning=" + isDaytimeRunning +
				", firstOrAddlVehicle='" + firstOrAddlVehicle + '\'' +
				", maxDriverAge=" + maxDriverAge +
				", totalVehiclePoint=" + totalVehiclePoint +
				", ratedDriver=" + ratedDriver +
				", number=" + number +
				", annualMileage=" + annualMileage +
				", collSymbol=" + collSymbol +
				", compSymbol=" + compSymbol +
				", id='" + id + '\'' +
				", modelYear=" + modelYear +
				", statCode='" + statCode + '\'' +
				", address=" + address +
				", coverages=" + coverages +
				'}';
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
}
