package aaa.main.modules.policy.home_ca.actiontabs;

import aaa.common.ActionTab;
import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.HomeCaMetaData;

public class ManualRenewActionTab extends ActionTab {
	public ManualRenewActionTab() {
		super(HomeCaMetaData.ManualRenewActionTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonOk.click();
		if(Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
			Page.dialogConfirmation.confirm();
		}
		return this;
	}
}
