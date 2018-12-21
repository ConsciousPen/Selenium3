package aaa.helpers.openl.model.home_ca.ho3;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.FORM_SHEET_NAME, headerRowIndex = HomeCaHO3OpenLFile.FORM_HEADER_ROW_NUMBER)
public class HomeCaHO3OpenLForm extends HomeCaOpenLForm {
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

	@Override
	public Double getLimit() {
		return limit;
	}

	@Override
	public void setLimit(Double limit) {
		this.limit = limit;
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
		return this.scheduledPropertyItems != null ? new ArrayList<>(this.scheduledPropertyItems) : null;
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
}
