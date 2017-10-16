package aaa.main.modules.policy.pup.defaulttabs;

import org.openqa.selenium.By;

import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import toolkit.webdriver.controls.Button;

public class PurchaseTab extends Purchase {
	
	public Button btnApplyPayment = new Button(By.id("purchaseForm:finishBtn_footer"));
	
	public PurchaseTab() {
		super(PurchaseMetaData.PurchaseTab.class);
	}
}
