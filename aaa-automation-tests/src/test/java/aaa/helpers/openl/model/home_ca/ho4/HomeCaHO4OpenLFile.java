package aaa.helpers.openl.model.home_ca.ho4;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLDwelling;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;
import aaa.utils.excel.bind.ExcelTableElement;
import aaa.utils.excel.bind.ExcelTransient;

public class HomeCaHO4OpenLFile extends HomeCaOpenLFile<HomeCaHO4OpenLPolicy> {
	@ExcelTransient
	static final int FORM_HEADER_ROW_NUMBER = 4;

	@ExcelTableElement(sheetName = POLICY_SHEET_NAME, headerRowNumber = POLICY_HEADER_ROW_NUMBER)
	protected List<HomeCaHO4OpenLPolicy> policies;

	@ExcelTransient
	@ExcelTableElement(sheetName = FORM_SHEET_NAME, headerRowNumber = FORM_HEADER_ROW_NUMBER)
	private List<HomeCaHO4OpenLForm> forms;

	@ExcelTransient
	@ExcelTableElement(sheetName = DWELLING_SHEET_NAME, headerRowNumber = DWELLING_HEADER_ROW_NUMBER)
	private List<HomeCaOpenLDwelling> dwelling;

	@ExcelTransient
	@ExcelTableElement(sheetName = SCHEDULED_PROPERTY_ITEM_SHEET_NAME, headerRowNumber = SCHEDULED_PROPERTY_ITEM_HEADER_ROW_NUMBER)
	protected List<HomeCaOpenLScheduledPropertyItem> scheduledPropertyItems;

	public List<HomeCaHO4OpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeCaHO4OpenLForm> forms) {
		this.forms = new ArrayList<>(forms);
	}

	public List<HomeCaOpenLDwelling> getDwellings() {
		return new ArrayList<>(dwelling);
	}

	public void setDwellings(List<HomeCaOpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

	@Override
	public List<HomeCaHO4OpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<HomeCaHO4OpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	@Override
	public String toString() {
		return "HomeCaDP3OpenLFile{" +
				"policies=" + policies +
				", forms=" + forms +
				", coverages=" + coverages +
				", address=" + address +
				", dwelling=" + dwelling +
				", tests=" + tests +
				'}';
	}
}
