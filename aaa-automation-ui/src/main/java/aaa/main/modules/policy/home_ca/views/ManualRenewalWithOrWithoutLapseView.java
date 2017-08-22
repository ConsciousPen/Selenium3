package aaa.main.modules.policy.home_ca.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.home_ca.actiontabs.ManualRenewalWithOrWithoutLapseActionTab;

public class ManualRenewalWithOrWithoutLapseView extends Workspace {
	public ManualRenewalWithOrWithoutLapseView() {
		super();
		registerTab(ManualRenewalWithOrWithoutLapseActionTab.class);
	}
}
