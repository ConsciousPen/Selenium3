package aaa.utils.openl.model;

import java.util.ArrayList;
import java.util.List;

public class AutoSSNJOpenLFile extends OpenLFile {
	@ExcelTableElement(sheetName = "Batch- PolicyNJ")
	protected List<AutoSSNJOpenLPolicy> policies;

	public List<AutoSSNJOpenLPolicy> getPolicies() {
		return policies;
	}

	public void setPolicies(List<AutoSSNJOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}


	//...
}
