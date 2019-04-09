package aaa.main.modules.policy.home_ca.views;

import aaa.common.Workspace;
import aaa.main.modules.policy.home_ca.actiontabs.ManualRenewActionTab;

public class ManualRenewView extends Workspace {
	public ManualRenewView(){
		super();
		registerTab(ManualRenewActionTab.class);
	}
}
