package aaa.helpers.openl.model.home_ss;

import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.OpenLForm;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.bind.annotation.ExcelTransient;

@ExcelTableElement(sheetName = OpenLFile.FORM_SHEET_NAME, headerRowIndex = OpenLFile.FORM_HEADER_ROW_NUMBER)
public class HomeSSOpenLForm extends OpenLForm {
	private Integer covPercentage;

	@ExcelTransient
	private Integer formOid;

	private Boolean masonaryOrFarmPremisesInd;
	private Double optionalValue;
	private Double optionalValue3;
	private Double optionalValue4;
	private Double optionalValue5;
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

	public Double getOptionalValue() {
		return optionalValue;
	}

	public void setOptionalValue(Double optionalValue) {
		this.optionalValue = optionalValue;
	}

	public Double getOptionalValue3() {
		return optionalValue3;
	}

	public void setOptionalValue3(Double optionalValue3) {
		this.optionalValue3 = optionalValue3;
	}

	public Double getOptionalValue4() {
		return optionalValue4;
	}

	public void setOptionalValue4(Double optionalValue4) {
		this.optionalValue4 = optionalValue4;
	}

	public Double getOptionalValue5() {
		return optionalValue5;
	}

	public void setOptionalValue5(Double optionalValue5) {
		this.optionalValue5 = optionalValue5;
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
}
