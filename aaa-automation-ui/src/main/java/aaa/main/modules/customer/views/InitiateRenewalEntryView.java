package aaa.main.modules.customer.views;

import aaa.common.Workspace;
import aaa.main.modules.customer.actiontabs.InitiateRenewalEntryActionTab;

public class InitiateRenewalEntryView extends Workspace {

	public InitiateRenewalEntryView() {
		registerTab(InitiateRenewalEntryActionTab.class);
	}
}
