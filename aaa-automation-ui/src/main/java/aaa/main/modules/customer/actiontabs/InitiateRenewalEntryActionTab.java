package aaa.main.modules.customer.actiontabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.main.metadata.CustomerMetaData;

public class InitiateRenewalEntryActionTab extends ActionTab {
	public InitiateRenewalEntryActionTab() {
		super(CustomerMetaData.InitiateRenewalEntryActionTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonOk.click();
		return this;
	}
}
