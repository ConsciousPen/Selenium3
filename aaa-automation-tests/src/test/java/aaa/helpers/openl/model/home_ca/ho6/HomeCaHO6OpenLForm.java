package aaa.helpers.openl.model.home_ca.ho6;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

public class HomeCaHO6OpenLForm extends HomeCaOpenLForm {
	@ExcelTableElement(sheetName = HomeCaOpenLFile.SCHEDULED_PROPERTY_ITEM_SHEET_NAME, headerRowIndex = HomeCaOpenLFile.SCHEDULED_PROPERTY_ITEM_HEADER_ROW_NUMBER)
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

	@Override
	public Double getLimit() {
		return limit;
	}

	@Override
	public void setLimit(Double limit) {
		this.limit = limit;
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
	public String toString() {
		return "HomeCaHO6OpenLForm{" +
				"scheduledPropertyItems=" + scheduledPropertyItems +
				", age=" + age +
				", deductible=" + deductible +
				", coApplicantClass" + coApplicantClass +
				", formClass='" + formClass + '\'' +
				", hasCoApplicant=" + hasCoApplicant +
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
