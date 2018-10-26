package aaa.helpers.openl.model;

import java.util.List;
import aaa.helpers.openl.annotation.RequiredField;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;

public abstract class OpenLVehicle {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	protected Integer number;

	@RequiredField
	protected OpenLAddress address;

	protected Integer annualMileage;

	@RequiredField
	protected Integer collSymbol;

	@RequiredField
	protected Integer compSymbol;

	protected String id;

	@RequiredField
	protected Integer modelYear;

	@RequiredField
	protected String biLiabilitySymbol;

	protected String pdLiabilitySymbol;
	protected String mpLiabilitySymbol;
	protected String umLiabilitySymbol;

	public abstract List<? extends AutoOpenLCoverage> getCoverages();

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getAnnualMileage() {
		return annualMileage;
	}

	public void setAnnualMileage(Integer annualMileage) {
		this.annualMileage = annualMileage;
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

	public Integer getModelYear() {
		return modelYear;
	}

	public void setModelYear(Integer modelYear) {
		this.modelYear = modelYear;
	}

	public String getBiLiabilitySymbol() {
		return biLiabilitySymbol;
	}

	public void setBiLiabilitySymbol(String biLiabilitySymbol) {
		this.biLiabilitySymbol = biLiabilitySymbol;
	}

	public String getPdLiabilitySymbol() {
		return pdLiabilitySymbol;
	}

	public void setPdLiabilitySymbol(String pdLiabilitySymbol) {
		this.pdLiabilitySymbol = pdLiabilitySymbol;
	}

	public String getMpLiabilitySymbol() {
		return mpLiabilitySymbol;
	}

	public void setMpLiabilitySymbol(String mpLiabilitySymbol) {
		this.mpLiabilitySymbol = mpLiabilitySymbol;
	}

	public String getUmLiabilitySymbol() {
		return umLiabilitySymbol;
	}

	public void setUmLiabilitySymbol(String umLiabilitySymbol) {
		this.umLiabilitySymbol = umLiabilitySymbol;
	}

	public OpenLAddress getAddress() {
		return address;
	}

	public void setAddress(OpenLAddress address) {
		this.address = address;
	}
}
