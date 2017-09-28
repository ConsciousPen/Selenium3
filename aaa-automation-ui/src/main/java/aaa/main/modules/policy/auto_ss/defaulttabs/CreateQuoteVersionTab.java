package aaa.main.modules.policy.auto_ss.defaulttabs;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.AutoSSMetaData;

public class CreateQuoteVersionTab extends Tab {
	public CreateQuoteVersionTab() {
		super(AutoSSMetaData.CreateQuoteVersionTab.class);
	}

	public boolean isPresent() {
		return getAssetList().getAsset(AutoSSMetaData.CreateQuoteVersionTab.VERSION_NUM).isPresent();
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
