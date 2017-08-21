package aaa.main.modules.policy.pup.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.pup.actiontabs.ManualRenewalWithOrWithoutLapseActionTab;

public class ManualRenewalWithOrWithoutLapseView extends Workspace {
	public ManualRenewalWithOrWithoutLapseView() {
		super();
		registerTab(ManualRenewalWithOrWithoutLapseActionTab.class);
	}
}
