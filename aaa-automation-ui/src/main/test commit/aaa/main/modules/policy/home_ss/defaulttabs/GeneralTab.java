package aaa.main.modules.policy.home_ss.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.waiters.Waiters;

public class GeneralTab extends Tab {

	public GeneralTab() {
		super(HomeSSMetaData.GeneralTab.class);
	}

	public Button btnContinue = new Button(By.id("policyDataGatherForm:continueBtn_AAAGeneralPageContinueAction_footer"), Waiters.AJAX);

	@Override
	public Tab submitTab() {
		btnContinue.click();
		return this;
	}
}
