package aaa.main.modules.policy.home_ca.defaulttabs;

import org.openqa.selenium.By;

import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.Purchase;
import aaa.toolkit.webdriver.customcontrols.dialog.DialogAssetList;
import toolkit.webdriver.controls.Button;

public class PurchaseTab extends Purchase {

	public PurchaseTab() {
		super(PurchaseMetaData.PurchaseTab.class);
	}

	public Dialog ceaQuoteConfirmation = new Dialog("//div[@id='purchaseForm:showRedirectionInfoPopup_container']");
	public DialogAssetList dialogComunityServiceSurveyPromt = new DialogAssetList(By.xpath("//div[@id='purchaseForm:updateSupplDataPopup_container']"), PurchaseMetaData.PurchaseTab.ComunityServiceSurveyPromt.class) {
		@Override
		public void submit() {
			Button buttonClosePopup = (Button) getAssetCollection().get("Ok");
			if (buttonClosePopup != null) {
				buttonClosePopup.click();
			}
		}
	};

	@Override
	public Tab submitTab() {
		btnApplyPayment.click();
		confirmPurchase.confirm();
		dialogComunityServiceSurveyPromt.submit();
		if (ceaQuoteConfirmation.isPresent() && ceaQuoteConfirmation.isVisible())
			ceaQuoteConfirmation.confirm();
		return this;
	}

}
