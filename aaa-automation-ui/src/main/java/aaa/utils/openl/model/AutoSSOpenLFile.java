package aaa.utils.openl.model;

import java.util.ArrayList;
import java.util.List;

public class AutoSSOpenLFile extends OpenLFile {
	@ExcelTableElement(sheetName = "Batch- PolicyAZ")
	protected List<AutoSSOpenLPolicy> policies;

	public List<AutoSSOpenLPolicy> getPolicies() {
		return policies;
	}

	public void setPolicies(List<AutoSSOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	//...
}
