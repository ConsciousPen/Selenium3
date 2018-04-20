package aaa.helpers.openl.model.home_ca.ho4;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLDwelling;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLScheduledPropertyItem;
import aaa.utils.excel.bind.annotation.ExcelTransient;

public class HomeCaHO4OpenLFile extends HomeCaOpenLFile<HomeCaHO4OpenLPolicy> {
	@ExcelTransient
	static final int FORM_HEADER_ROW_NUMBER = 4;

	protected List<HomeCaHO4OpenLPolicy> policies;

	@ExcelTransient
	private List<HomeCaHO4OpenLForm> forms;

	@ExcelTransient
	private List<HomeCaOpenLDwelling> dwelling;

	@ExcelTransient
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
