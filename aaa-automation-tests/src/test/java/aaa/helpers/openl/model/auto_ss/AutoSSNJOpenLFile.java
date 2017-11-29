package aaa.helpers.openl.model.auto_ss;

import java.util.ArrayList;
import java.util.List;
import aaa.helpers.openl.model.OpenLFile;
import aaa.utils.excel.bind.ExcelTableElement;

public class AutoSSNJOpenLFile extends OpenLFile<AutoSSNJOpenLPolicy> {
	@ExcelTableElement(sheetName = "Batch- PolicyNJ")
	protected List<AutoSSNJOpenLPolicy> policies;

	@Override
	public List<AutoSSNJOpenLPolicy> getPolicies() {
		return policies;
	}

	public void setPolicies(List<AutoSSNJOpenLPolicy> policies) {
		this.policies = new ArrayList<>(policies);
	}

	//...
}
