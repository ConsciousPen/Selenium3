package aaa.main.modules.policy.auto_ss.defaulttabs;

import aaa.common.Tab;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import toolkit.datax.TestData;

public class PurchaseTab extends Purchase {

	public PurchaseTab() {
		super(PurchaseMetaData.PurchaseTab.class);
	}

	@Override
	public Tab fillTab(TestData td) {
		return super.fillTab(td);
	}
}
