package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;

public class OpenLNamedInsured {
	@ExcelTableColumnElement(name = OpenLFile.PRIMARY_KEY_COLUMN_NAME, isPrimaryKey = true)
	private Integer number;

	private Integer aAAPropPersistency;
	private Integer ageOfOldestInsured;
	private Integer initialAgeOfOldestInsured;
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

	@Override
	public String toString() {
		return "OpenLNamedInsured{" +
				"number=" + number +
				", aAAPropPersistency=" + aAAPropPersistency +
				", ageOfOldestInsured=" + ageOfOldestInsured +
				", initialAgeOfOldestInsured=" + initialAgeOfOldestInsured +
				", maritialStatus='" + maritialStatus + '\'' +
				", numofOccupants=" + numofOccupants +
				", totalNoReinstatements=" + totalNoReinstatements +
				'}';
	}
}
