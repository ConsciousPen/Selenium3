package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import aaa.utils.excel.bind.ExcelTableColumnElement;
import aaa.utils.excel.bind.ExcelTableElement;

public class OpenLVehicle {
	@ExcelTableColumnElement(name = "_PK_", isPrimaryKey = true)
	private int number;
	private String airbagCode;
	private int annualMileage;
	private String antiTheftString;
	private int collSymbol;
	private int compSymbol;
	private String id;
	private boolean isHybrid;
	private boolean isTelematic; // OR specific
	private int modelYear;
	private boolean newCarAddedProtection;
	private String statCode;
	private String usage;
	private int vehicleAge;

	@ExcelTableElement(sheetName = "Batch- Address", headerRowNumber = OpenLFile.ADDRESS_HEADER_ROW_NUMBER)
	private List<OpenLAddress> address;

	@ExcelTableElement(sheetName = "Batch- CoverageAZ", headerRowNumber = OpenLFile.COVERAGE_HEADER_ROW_NUMBER)
	private List<OpenLCoverage> coverages;

	@ExcelTableElement(sheetName = "Batch- DriverAZ", headerRowNumber = OpenLFile.DRIVER_HEADER_ROW_NUMBER)
	private List<OpenLDriver> ratedDriver;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getAirbagCode() {
		return airbagCode;
	}

	public void setAirbagCode(String airbagCode) {
		this.airbagCode = airbagCode;
	}

	public int getAnnualMileage() {
		return annualMileage;
	}

	public void setAnnualMileage(int annualMileage) {
		this.annualMileage = annualMileage;
	}

	public String getAntiTheftString() {
		return antiTheftString;
	}

	public void setAntiTheftString(String antiTheftString) {
		this.antiTheftString = antiTheftString;
	}

	public int getCollSymbol() {
		return collSymbol;
	}

	public void setCollSymbol(int collSymbol) {
		this.collSymbol = collSymbol;
	}

	public int getCompSymbol() {
		return compSymbol;
	}

	public void setCompSymbol(int compSymbol) {
		this.compSymbol = compSymbol;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isHybrid() {
		return isHybrid;
	}

	public void setHybrid(boolean hybrid) {
		isHybrid = hybrid;
	}

	public boolean isTelematic() {
		return isTelematic;
	}

	public void setTelematic(boolean telematic) {
		isTelematic = telematic;
	}

	public int getModelYear() {
		return modelYear;
	}

	public void setModelYear(int modelYear) {
		this.modelYear = modelYear;
	}

	public boolean isNewCarAddedProtection() {
		return newCarAddedProtection;
	}

	public void setNewCarAddedProtection(boolean newCarAddedProtection) {
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

	public int getVehicleAge() {
		return vehicleAge;
	}

	public void setVehicleAge(int vehicleAge) {
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
