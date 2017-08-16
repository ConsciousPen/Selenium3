package aaa.main.modules.policy.pup.defaulttabs;

import aaa.main.modules.policy.abstract_tabs.CommonErrorTab;
import aaa.common.Tab;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;

public class ErrorTab extends CommonErrorTab {
	
	public ErrorTab() {
		super(PersonalUmbrellaMetaData.ErrorTab.class);
	}

	@Override
	public Tab submitTab() {
		buttonOverride.click();
		new BindTab().submitTab();
		return this;
	}
	

}
