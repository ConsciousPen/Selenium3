package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import aaa.utils.excel.bind.ExcelTableColumnElement;
import aaa.utils.excel.bind.ExcelTableElement;

public class OpenLVehicle {
	@ExcelTableColumnElement(name = "_PK_", isPrimaryKey = true)
	private Integer number;
	private String airbagCode;
	private Integer annualMileage;
	private String antiTheftString;
	private Integer collSymbol;
	private Integer compSymbol;
	private String id;
	private Boolean isHybrid;
	private Boolean isTelematic; // OR specific
	private Integer modelYear;
	private Boolean newCarAddedProtection;
	private String statCode;
	private String usage;
	private Integer vehicleAge;

	@ExcelTableElement(sheetName = "Batch- Address", headerRowNumber = OpenLFile.ADDRESS_HEADER_ROW_NUMBER)
	private List<OpenLAddress> address;

	@ExcelTableElement(sheetName = "Batch- CoverageAZ", headerRowNumber = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
	private List<OpenLCoverage> coverages;

	@ExcelTableElement(sheetName = "Batch- DriverAZ", headerRowNumber = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<OpenLDriver> ratedDriver;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getAirbagCode() {
		return airbagCode;
	}

	public void setAirbagCode(String airbagCode) {
		this.airbagCode = airbagCode;
	}

	public Integer getAnnualMileage() {
		return annualMileage;
	}

	public void setAnnualMileage(Integer annualMileage) {
		this.annualMileage = annualMileage;
	}

	public String getAntiTheftString() {
		return antiTheftString;
	}

	public void setAntiTheftString(String antiTheftString) {
		this.antiTheftString = antiTheftString;
	}

	public Integer getCollSymbol() {
		return collSymbol;
	}

	public void setCollSymbol(Integer collSymbol) {
		this.collSymbol = collSymbol;
	}

	public Integer getCompSymbol() {
		return compSymbol;
	}

	public void setCompSymbol(Integer compSymbol) {
		this.compSymbol = compSymbol;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean isHybrid() {
		return isHybrid;
	}

	public void setHybrid(Boolean hybrid) {
		isHybrid = hybrid;
	}

	public Boolean isTelematic() {
		return isTelematic;
	}

	public void setTelematic(Boolean telematic) {
		isTelematic = telematic;
	}

	public Integer getModelYear() {
		return modelYear;
	}

	public void setModelYear(Integer modelYear) {
		this.modelYear = modelYear;
	}

	public Boolean isNewCarAddedProtection() {
		return newCarAddedProtection;
	}

	public void setNewCarAddedProtection(Boolean newCarAddedProtection) {
		this.newCarAddedProtection = newCarAddedProtection;
	}

	public String getStatCode() {
		return statCode;
	}

	public void setStatCode(String statCode) {
		this.statCode = statCode;
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

	public List<OpenLAddress> getAddress() {
		return address;
	}

	public void setAddress(List<OpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	public List<OpenLCoverage> getCoverages() {
		return coverages;
	}

	public void setCoverages(List<OpenLCoverage> coverages) {
		this.coverages = new ArrayList<>(coverages);
	}

	public List<OpenLDriver> getRatedDriver() {
		return ratedDriver;
	}

	public void setRatedDriver(List<OpenLDriver> ratedDriver) {
		this.ratedDriver = new ArrayList<>(ratedDriver);
	}
}
