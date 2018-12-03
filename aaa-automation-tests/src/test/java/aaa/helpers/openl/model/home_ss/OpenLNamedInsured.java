package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.annotation.RequiredField;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.NAMED_INSURED_SHEET_NAME, headerRowIndex = OpenLFile.NAMED_INSURED_HEADER_ROW_NUMBER)
public class OpenLNamedInsured {
	@ExcelColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	@RequiredField
	private Integer number;

	@RequiredField
	private Integer aAAPropPersistency;

	@RequiredField
	private Integer ageOfOldestInsured;
	private Integer initialAgeOfOldestInsured;

	@RequiredField
	private String maritialStatus;
	private Integer numofOccupants;
	private Integer totalNoReinstatements;

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getaAAPropPersistency() {
		return aAAPropPersistency;
	}

	public void setaAAPropPersistency(Integer aAAPropPersistency) {
		this.aAAPropPersistency = aAAPropPersistency;
	}

	public Integer getAgeOfOldestInsured() {
		return ageOfOldestInsured;
	}

	public void setAgeOfOldestInsured(Integer ageOfOldestInsured) {
		this.ageOfOldestInsured = ageOfOldestInsured;
	}

	public Integer getInitialAgeOfOldestInsured() {
		return initialAgeOfOldestInsured;
	}

	public void setInitialAgeOfOldestInsured(Integer initialAgeOfOldestInsured) {
		this.initialAgeOfOldestInsured = initialAgeOfOldestInsured;
	}

	public String getMaritialStatus() {
		return maritialStatus;
	}

	public void setMaritialStatus(String maritialStatus) {
		this.maritialStatus = maritialStatus;
	}

	public Integer getNumofOccupants() {
		return numofOccupants;
	}

	public void setNumofOccupants(Integer numofOccupants) {
		this.numofOccupants = numofOccupants;
	}

	public Integer getTotalNoReinstatements() {
		return totalNoReinstatements;
	}

	public void setTotalNoReinstatements(Integer totalNoReinstatements) {
		this.totalNoReinstatements = totalNoReinstatements;
	}
}
