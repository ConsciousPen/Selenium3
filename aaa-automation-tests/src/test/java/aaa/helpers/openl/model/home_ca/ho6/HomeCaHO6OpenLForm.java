package aaa.helpers.openl.model.home_ca.ho6;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;

public class HomeCaHO6OpenLForm extends HomeCaOpenLForm {

	private List<HomeCaOpenLScheduledPropertyItem> scheduledPropertyItems;
	private Integer age;
	private String coApplicantClass;
	private Double deductible;
	private String formClass;
	private Boolean hasCoApplicant;
	private Integer numOfFamilies;
	private Double percentage;
	private String territoryCode;
	private String type;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public String getCoApplicantClass() {
		return coApplicantClass;
	}

	public void setCoApplicantClass(String coApplicantClass) {
		this.coApplicantClass = coApplicantClass;
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

	public Boolean getHasCoApplicant() {
		return hasCoApplicant;
	}

	public void setHasCoApplicant(Boolean hasCoApplicant) {
		this.hasCoApplicant = hasCoApplicant;
	}

	public Integer getNumOfFamilies() {
		return numOfFamilies;
	}

	public void setNumOfFamilies(Integer numOfFamilies) {
		this.numOfFamilies = numOfFamilies;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public List<HomeCaOpenLScheduledPropertyItem> getScheduledPropertyItems() {
		return this.scheduledPropertyItems != null ? new ArrayList<>(this.scheduledPropertyItems) : null;
	}

	public void setScheduledPropertyItems(List<HomeCaOpenLScheduledPropertyItem> scheduledPropertyItem) {
		this.scheduledPropertyItems = new ArrayList<>(scheduledPropertyItem);
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
}
