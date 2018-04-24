package aaa.helpers.openl.model.home_ca.ho6;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;

public class HomeCaHO6OpenLForm extends HomeCaOpenLForm {
	private List<HomeCaOpenLScheduledPropertyItem> scheduledPropertyItems;

	private Integer age;
	private Double deductible;
	private String formClass;
	private Boolean hasCorporalPunishmentSurcharge;
	private Integer numOfFamilies;
	private String territoryCode;
	private String type;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public Double getDeductible() {
		return deductible;
	}

	public void setDeductible(Double deductible) {
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

	public List<HomeCaOpenLScheduledPropertyItem> getScheduledPropertyItems() {
		return scheduledPropertyItems != null ? new ArrayList<>(scheduledPropertyItems) : null;
	}

	public void setScheduledPropertyItems(List<HomeCaOpenLScheduledPropertyItem> scheduledPropertyItems) {
		this.scheduledPropertyItems = new ArrayList<>(scheduledPropertyItems);
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
	public Double getLimit() {
		return limit;
	}

	@Override
	public void setLimit(Double limit) {
		this.limit = limit;
	}

	@Override
	public String toString() {
		return "HomeCaHO6OpenLForm{" +
				"scheduledPropertyItems=" + scheduledPropertyItems +
				", age=" + age +
				", deductible=" + deductible +
				", formClass='" + formClass + '\'' +
				", hasCorporalPunishmentSurcharge=" + hasCorporalPunishmentSurcharge +
				", numOfFamilies=" + numOfFamilies +
				", territoryCode='" + territoryCode + '\'' +
				", type='" + type + '\'' +
				", applyDiscounts=" + applyDiscounts +
				", hasSupportingForm=" + hasSupportingForm +
				", number=" + number +
				", formCode='" + formCode + '\'' +
				", limit=" + limit +
				'}';
	}
}
