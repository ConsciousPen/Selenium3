package aaa.main.modules.policy.home_ss.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.home_ss.actiontabs.ManualRenewalWithOrWithoutLapseActionTab;

public class ManualRenewalWithOrWithoutLapseView extends Workspace {
	public ManualRenewalWithOrWithoutLapseView() {
		super();
		registerTab(ManualRenewalWithOrWithoutLapseActionTab.class);
	}
}
