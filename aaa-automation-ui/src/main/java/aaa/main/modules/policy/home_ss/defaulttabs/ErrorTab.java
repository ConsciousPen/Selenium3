package aaa.main.modules.policy.home_ss.defaulttabs;

import aaa.main.modules.policy.abstract_tabs.Error;
import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.composite.table.Table;

public class ErrorTab extends Error{
	
	public ErrorTab() {
		super(HomeSSMetaData.ErrorTab.class);
	}

	@Override
	public Tab submitTab() {
		btnOverride.click();
		new BindTab().submitTab();
		return this;
	}
	

}
