package aaa.helpers.openl.model.home_ca.ho4;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLForm;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

@ExcelTableElement(sheetName = OpenLFile.FORM_SHEET_NAME, headerRowIndex = HomeCaHO4OpenLFile.FORM_HEADER_ROW_NUMBER)
public class HomeCaHO4OpenLForm extends HomeCaOpenLForm {

	private List<HomeCaOpenLScheduledPropertyItem> scheduledPropertyItems;

	private Integer age;
	private Integer deductible;
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
		return "HomeCaHO4OpenLForm{" +
				"scheduledPropertyItems=" + scheduledPropertyItems +
				", age=" + age +
				", deductible=" + deductible +
				", formClass='" + formClass + '\'' +
				", hasCorporalPunishmentSurcharge=" + hasCorporalPunishmentSurcharge +
				", numOfFamilies=" + numOfFamilies +
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
