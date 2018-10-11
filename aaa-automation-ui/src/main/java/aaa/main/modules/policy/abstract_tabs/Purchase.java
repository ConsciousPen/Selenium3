package aaa.main.modules.policy.abstract_tabs;

import org.openqa.selenium.By;
import aaa.common.Tab;
import aaa.common.components.Dialog;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.toolkit.webdriver.customcontrols.PaymentMethodAllocationControl;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.Link;
import toolkit.webdriver.controls.StaticElement;
import toolkit.webdriver.controls.composite.assets.metadata.MetaData;
import toolkit.webdriver.controls.composite.table.Table;
import toolkit.webdriver.controls.waiters.Waiters;

public abstract class Purchase extends Tab {
	public static Table tablePaymentPlan = new Table(By.id("purchaseForm:PaymentPlanTable"));
	public static Button btnApplyPayment = new Button(By.id("purchaseForm:actionButton_aaaPurchaseIssueActionViewModel_footer"));
	public static Button buttonCancel = new Button(By.id("purchaseForm:noButton_aaaPurchaseIssueActionViewModel_footer"));
	public static Dialog confirmPurchase = new Dialog("//div[@id='purchaseForm:FinishConfirmationDialog_container']");
	public static Dialog confirmVoiceSignature = new Dialog("//div[@id='purchaseForm:VoiceSignatureDialog_container']");
	public static StaticElement totalRemainingTermPremium = new StaticElement(By.id("purchaseForm:downpaymentComponent_totalRemainingDueValue"));
	public static StaticElement remainingBalanceDueToday = new StaticElement(By.id("purchaseForm:downpaymentComponent_remainingBalanceValue"));

	public static Table autoPaySetupSavingMessage = new Table(By.id("purchaseForm:installmentFeeAmountSavedPanel"));
	public static Link linkViewApplicableFeeSchedule = new Link(By.id("purchaseForm:installmentFeeDetails"), Waiters.AJAX);
	public static Table tableInstallmentFeeDetails = new Table(By.id("purchaseForm:installmentFeeDetailsTable"));

	protected Purchase(Class<? extends MetaData> mdClass) {
		super(mdClass);
	}

	public boolean isVisible() {
		return btnApplyPayment.isPresent() && btnApplyPayment.isVisible();
	}

	@Override
	public Tab submitTab() {
		btnApplyPayment.click();
		confirmPurchase.confirm();
		return this;
	}

	@Override
	public Tab fillTab(TestData td) {
		ErrorTab errorTab = new ErrorTab();
		if (errorTab.isVisible() && errorTab.getErrorCodesList().contains(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME.getCode())) {
			errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_AUTO_SS_MEM_LASTNAME);
			DocumentsAndBindTab.btnPurchase.click();
			DocumentsAndBindTab.confirmPurchase.buttonYes.click();
		}
		String value = remainingBalanceDueToday.getValue();
		String total = totalRemainingTermPremium.getValue();
		td.adjust(TestData.makeKeyPath(getAssetList().getName(), PurchaseMetaData.PurchaseTab.PAYMENT_ALLOCATION.getLabel(), PaymentMethodAllocationControl.BALANCE_DUE_KEY), value);
		td.adjust(TestData.makeKeyPath(getAssetList().getName(), PurchaseMetaData.PurchaseTab.PAYMENT_ALLOCATION.getLabel(), PaymentMethodAllocationControl.TOTAL_PREMIUM_KEY), total);
		super.fillTab(td);
		return this;
	}

	public Tab payRemainingBalance() {
		return payRemainingBalance(BillingConstants.AcceptPaymentMethod.VISA);
	}

	public Tab payRemainingBalance(String paymentMethod) {
		TestData payRemainingBalanceTD = DataProviderFactory.dataOf(getMetaKey(), DataProviderFactory.dataOf(PurchaseMetaData.PurchaseTab.PAYMENT_ALLOCATION.getLabel(),
				DataProviderFactory.dataOf(paymentMethod, PaymentMethodAllocationControl.REST_VALUE)));
		fillTab(payRemainingBalanceTD);
		return this;
	}
}
