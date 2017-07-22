package aaa.main.modules.policy.abstract_tabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.toolkit.webdriver.customcontrols.PaymentMethodAllocationControl;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.waiters.Waiters;

public abstract class Purchase extends Tab {
	public Button btnApplyPayment = new Button(By.id("purchaseForm:finishBtn_footer"), Waiters.AJAX);
	public Dialog confirmPurchase = new Dialog("//div[@id='purchaseForm:FinishConfirmationDialog_container']");
	public StaticElement totalRemainingTermPremium = new StaticElement(By.id("purchaseForm:downpaymentComponent_totalRemainingDueValue"));
	public StaticElement remainingBalanceDueToday = new StaticElement(By.id("purchaseForm:downpaymentComponent_remainingBalanceValue"));

	protected Purchase(Class<? extends MetaData> mdClass) {
		super(mdClass);
	}

	@Override
	public Tab submitTab() {
		btnApplyPayment.click();
		confirmPurchase.confirm();
		return this;
	}

	@Override
	public Tab fillTab(TestData td) {
		String value = remainingBalanceDueToday.getValue();
		td.adjust(TestData.makeKeyPath(getAssetList().getName(), PurchaseMetaData.PurchaseTab.PAYMENT_ALLOCATION.getLabel(), PaymentMethodAllocationControl.BALANCE_DUE_KEY), value);
		super.fillTab(td);
		return this;
	}
}
