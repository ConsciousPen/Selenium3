package aaa.helpers.openl.model.home_ca.ho4;

import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;

public class HomeCaHO4OpenLForm extends HomeCaOpenLForm {
	private Integer age;
	private Integer deductible;
	private String formClass;
	private Boolean hasCorporalPunishmentSurcharge;
	private Integer numOfFamilies;
	private Integer scheduledPropertyItems;
	private String territoryCode;
	private String type;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Integer getDeductible() {
		return deductible;
	}

	public void setDeductible(Integer deductible) {
		this.deductible = deductible;
	}

	public String getFormClass() {
		return formClass;
	}

	public void setFormClass(String formClass) {
		this.formClass = formClass;
	}

	public Boolean getHasCorporalPunishmentSurcharge() {
		return hasCorporalPunishmentSurcharge;
	}

	public void setHasCorporalPunishmentSurcharge(Boolean hasCorporalPunishmentSurcharge) {
		this.hasCorporalPunishmentSurcharge = hasCorporalPunishmentSurcharge;
	}

	public Integer getNumOfFamilies() {
		return numOfFamilies;
	}

	public void setNumOfFamilies(Integer numOfFamilies) {
		this.numOfFamilies = numOfFamilies;
	}

	public Integer getScheduledPropertyItems() {
		return scheduledPropertyItems;
	}

	public void setScheduledPropertyItems(Integer scheduledPropertyItems) {
		this.scheduledPropertyItems = scheduledPropertyItems;
	}

	public String getTerritoryCode() {
		return territoryCode;
	}

	public void setTerritoryCode(String territoryCode) {
		this.territoryCode = territoryCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "HomeCaHO3OpenLForm{" +
				"age=" + age +
				", deductible=" + deductible +
				", formClass='" + formClass + '\'' +
				", formCode='" + formCode + '\'' +
				", hasCorporalPunishmentSurcharge=" + hasCorporalPunishmentSurcharge +
				", numOfFamilies=" + numOfFamilies +
				", scheduledPropertyItems=" + scheduledPropertyItems +
				", territoryCode='" + territoryCode + '\'' +
				", type='" + type + '\'' +
				", limit=" + limit +
				", applyDiscounts=" + applyDiscounts +
				", hasSupportingForm=" + hasSupportingForm +
				", number=" + number +
				", formCode='" + formCode + '\'' +
				", limit=" + limit +
				'}';
	}
}
