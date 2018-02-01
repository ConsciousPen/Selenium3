package aaa.helpers.openl.model;

import java.util.ArrayList;
import java.util.List;
import aaa.utils.excel.bind.ExcelTableColumnElement;
import aaa.utils.excel.bind.ExcelTableElement;

public class OpenLVehicle {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	protected Integer number;

	protected Integer annualMileage;
	protected Integer collSymbol;
	protected Integer compSymbol;
	protected String id;
	protected Integer modelYear;
	protected String statCode;

	@ExcelTableElement(sheetName = OpenLFile.ADDRESS_SHEET_NAME, headerRowNumber = OpenLFile.ADDRESS_HEADER_ROW_NUMBER)
	protected List<OpenLAddress> address;

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

	public String getStatCode() {
		return statCode;
	}

	public void setStatCode(String statCode) {
		this.statCode = statCode;
	}

	public List<OpenLAddress> getAddress() {
		return new ArrayList<>(address);
	}

	public void setAddress(List<OpenLAddress> address) {
		this.address = new ArrayList<>(address);
	}

	@Override
	public String toString() {
		return "OpenLVehicle{" +
				"number=" + number +
				", annualMileage=" + annualMileage +
				", collSymbol=" + collSymbol +
				", compSymbol=" + compSymbol +
				", id='" + id + '\'' +
				", modelYear=" + modelYear +
				", statCode='" + statCode + '\'' +
				", address=" + address +
				'}';
	}
}
