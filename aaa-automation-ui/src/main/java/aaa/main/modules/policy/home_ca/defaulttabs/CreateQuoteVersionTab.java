package aaa.main.modules.policy.home_ca.defaulttabs;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.HomeCaMetaData;

public class CreateQuoteVersionTab extends Tab {
	public CreateQuoteVersionTab() {
		super(HomeCaMetaData.CreateQuoteVersionTab.class);
	}

	public boolean isPresent() {
		return getAssetList().getAsset(HomeCaMetaData.CreateQuoteVersionTab.VERSION_NUM).isPresent();
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
