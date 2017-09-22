package aaa.main.modules.policy.pup.defaulttabs;

import aaa.common.Tab;
import aaa.common.pages.Page;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;

public class CreateQuoteVersionTab extends Tab {
	public CreateQuoteVersionTab() {
		super(PersonalUmbrellaMetaData.CreateQuoteVersionTab.class);
	}

	public boolean isPresent() {
		return getAssetList().getAsset(PersonalUmbrellaMetaData.CreateQuoteVersionTab.VERSION_NUM).isPresent();
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
