package aaa.main.modules.policy.auto_ca.defaulttabs;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoCaMetaData;

public class CreateQuoteVersionTab extends Tab {
	public CreateQuoteVersionTab() {
		super(AutoCaMetaData.CreateQuoteVersionTab.class);
	}

	public boolean isPresent() {
		return getAssetList().getAsset(AutoCaMetaData.CreateQuoteVersionTab.VERSION_NUM).isPresent();
	}

	public void submitIfPresent() {
		if (isPresent()) {
			submitTab();
		}
	}

	@Override
	public Tab submitTab() {
		buttonOk.click();
		Page.dialogConfirmation.confirm();
		return this;
	}
}
