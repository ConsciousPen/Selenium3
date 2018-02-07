package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLForm;

public class HomeSSOpenLForm extends OpenLForm {
	private Integer covPercentage;
	private Integer formOid;
	private Boolean masonaryOrFarmPremisesInd;
	private Integer optionalValue;
	private String type;
	private Integer noOfFamilies; // MD specific ?
	private Integer noOfPersons; // MD specific ?

	public Integer getCovPercentage() {
		return covPercentage;
	}

	public void setCovPercentage(Integer covPercentage) {
		this.covPercentage = covPercentage;
	}

	public Integer getFormOid() {
		return formOid;
	}

	public void setFormOid(Integer formOid) {
		this.formOid = formOid;
	}

	public Boolean getMasonaryOrFarmPremisesInd() {
		return masonaryOrFarmPremisesInd;
	}

	public void setMasonaryOrFarmPremisesInd(Boolean masonaryOrFarmPremisesInd) {
		this.masonaryOrFarmPremisesInd = masonaryOrFarmPremisesInd;
	}

	public Integer getOptionalValue() {
		return optionalValue;
	}

	public void setOptionalValue(Integer optionalValue) {
		this.optionalValue = optionalValue;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getNoOfFamilies() {
		return noOfFamilies;
	}

	public void setNoOfFamilies(Integer noOfFamilies) {
		this.noOfFamilies = noOfFamilies;
	}

	public Integer getNoOfPersons() {
		return noOfPersons;
	}

	public void setNoOfPersons(Integer noOfPersons) {
		this.noOfPersons = noOfPersons;
	}

	@Override
	public String toString() {
		return "HomeSSOpenLForm{" +
				"covPercentage=" + covPercentage +
				", formOid=" + formOid +
				", masonaryOrFarmPremisesInd=" + masonaryOrFarmPremisesInd +
				", optionalValue=" + optionalValue +
				", type='" + type + '\'' +
				", noOfFamilies=" + noOfFamilies +
				", noOfPersons=" + noOfPersons +
				", number=" + number +
				", formCode='" + formCode + '\'' +
				", limit=" + limit +
				'}';
	}
}
