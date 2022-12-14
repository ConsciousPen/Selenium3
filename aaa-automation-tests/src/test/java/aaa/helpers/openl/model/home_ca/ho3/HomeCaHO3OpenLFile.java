package aaa.helpers.openl.model.home_ca.ho3;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class HomeCaHO3OpenLFile extends HomeCaOpenLFile<HomeCaHO3OpenLPolicy> {
	@ExcelTransient
	static final int FORM_HEADER_ROW_NUMBER = 4;

	private List<HomeCaHO3OpenLPolicy> policies;

	@ExcelTransient
	private List<HomeCaHO3OpenLForm> forms;
	@ExcelTransient
	private List<HomeCaHO3OpenLDwelling> dwelling;
	@ExcelTransient
	private List<HomeCaOpenLScheduledPropertyItem> scheduledPropertyItems;

	public List<HomeCaHO3OpenLForm> getForms() {
		return new ArrayList<>(forms);
	}

	public void setForms(List<HomeCaHO3OpenLForm> forms) {
		this.forms = new ArrayList<>(forms);
	}

	public List<HomeCaHO3OpenLDwelling> getDwelling() {
		return new ArrayList<>(dwelling);
	}

	public void setDwellings(List<HomeCaHO3OpenLDwelling> dwelling) {
		this.dwelling = new ArrayList<>(dwelling);
	}

	@Override
	public List<HomeCaHO3OpenLPolicy> getPolicies() {
		return new ArrayList<>(policies);
	}

	public void setPolicies(List<HomeCaHO3OpenLPolicy> policies) {
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
