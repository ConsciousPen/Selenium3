package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.HomeSSMetaData;

public class CreateQuoteVersionTab extends Tab {
	public CreateQuoteVersionTab() {
		super(HomeSSMetaData.CreateQuoteVersionTab.class);
	}

	public boolean isPresent() {
		return getAssetList().getAsset(HomeSSMetaData.CreateQuoteVersionTab.VERSION_NUM).isPresent();
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
