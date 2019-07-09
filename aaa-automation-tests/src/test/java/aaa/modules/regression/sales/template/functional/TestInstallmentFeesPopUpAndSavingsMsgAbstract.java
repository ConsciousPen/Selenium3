/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.template.functional;

import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD;
import static toolkit.verification.CustomAssertions.assertThat;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.PolicyBaseTest;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.composite.assets.metadata.AssetDescriptor;

public abstract class TestInstallmentFeesPopUpAndSavingsMsgAbstract extends PolicyBaseTest {

	protected abstract String getGeneralTab();

	protected abstract String getPremiumAndCoverageTab();

	protected abstract String getDocumentsAndBindTab();

	protected abstract Tab getGeneralTabElement();

	protected abstract Tab getPremiumAndCoverageTabElement();

	protected abstract Tab getDocumentsAndBindElement();

	protected abstract Tab getPurchaseTabElement();

	protected abstract InquiryAssetList getInquiryAssetList();

	protected abstract AssetDescriptor<ComboBox> getPaymentPlanComboBox();

	protected abstract AssetDescriptor<FillableTable> getInstallmentFeesDetailsTable();

	private static final String AUTOPAY_SAVING_MESSAGE = "This customer can save %s per installment if enrolled into AutoPay with a checking/savings account.";

	/**
	 * *@author Oleg Stasyuk
	 * *@name Test Autopay Saving Message in P&C tab and in Purchase flow
	 * *@scenario 1. Create new quote
	 * 2. Open P&C, check that Autopay Saving Message is not shown for Annual and Monthly plan
	 * 3. Go to Purchase flow, check that Autopay Saving Message is shown
	 * 4. Issue quote
	 * 5. Start an endorsement
	 * 6. Open P&C, check that Autopay Saving Message is not shown for Annual plan and is shown for Monthly plan
	 * *@details
	 */
	protected void pas246_InstallmentFeesPopUpAndSavingsMsg() {
		navigateAndRate();
		openInstallmentFeeTable();
		Dollar nonEftInstallmentFee = new Dollar(getPremiumAndCoverageTabElement().getAssetList().getAsset(getInstallmentFeesDetailsTable()).getTable().getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeACH = new Dollar(getPremiumAndCoverageTabElement().getAssetList().getAsset(getInstallmentFeesDetailsTable()).getTable().getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeCreditCard = new Dollar(getPremiumAndCoverageTabElement().getAssetList().getAsset(getInstallmentFeesDetailsTable()).getTable().getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeDebitCard = new Dollar(getPremiumAndCoverageTabElement().getAssetList().getAsset(getInstallmentFeesDetailsTable()).getTable().getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE).getValue());
		String delta = nonEftInstallmentFee.subtract(eftInstallmentFeeACH).toString().replace(".00", "");
		Page.dialogConfirmation.buttonCloseWithCross.click();

		autopaySavingMessageCheck(false, delta);

		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		getDocumentsAndBindElement().submitTab();
		assertThat(Purchase.autoPaySetupSavingMessage.getRow(1).getCell(2)).hasValue(String.format(AUTOPAY_SAVING_MESSAGE, delta));

		Purchase.linkViewApplicableFeeSchedule.click();
		CustomSoftAssertions.assertSoftly(softly -> {
			assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE)).hasValue(nonEftInstallmentFee.toString());
			assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeACH.toString());
			assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeCreditCard.toString());
			assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeDebitCard.toString());
		});
		Page.dialogConfirmation.buttonCloseWithCross.click();

		getPurchaseTabElement().fillTab(getPolicyTD()).submitTab();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		autopaySavingMessageCheck(true, delta);

		navigateAndRate();
		openInstallmentFeeTable();
		CustomSoftAssertions.assertSoftly(softly -> {
			assertThat(getPremiumAndCoverageTabElement().getAssetList().getAsset(getInstallmentFeesDetailsTable()).getTable().getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE)).hasValue(nonEftInstallmentFee.toString());
			assertThat(getPremiumAndCoverageTabElement().getAssetList().getAsset(getInstallmentFeesDetailsTable()).getTable().getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeACH.toString());
			assertThat(getPremiumAndCoverageTabElement().getAssetList().getAsset(getInstallmentFeesDetailsTable()).getTable().getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeCreditCard.toString());
			assertThat(getPremiumAndCoverageTabElement().getAssetList().getAsset(getInstallmentFeesDetailsTable()).getTable().getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeDebitCard.toString());
		});
		Page.dialogConfirmation.buttonCloseWithCross.click();

		NavigationPage.toViewSubTab(getDocumentsAndBindTab());
		getDocumentsAndBindElement().submitTab();
	}

	protected abstract void openInstallmentFeeTable();

	protected abstract void navigateAndRate();

	private void autopaySavingMessageCheck(boolean isPresent, String delta) {
		navigateAndRate();
		if (getPremiumAndCoverageTabElement().getAssetList().getAsset(getPaymentPlanComboBox()).isEnabled()) {
			if (getPremiumAndCoverageTabElement().getAssetList().getAsset(getPaymentPlanComboBox()).getAllValues().contains("Annual")) {
				getPremiumAndCoverageTabElement().getAssetList().getAsset(getPaymentPlanComboBox()).setValue("Annual");
			} else {
				getPremiumAndCoverageTabElement().getAssetList().getAsset(getPaymentPlanComboBox()).setValue("Pay in Full");
			}
			navigateAndRate();
			assertThat(PremiumAndCoveragesTab.autoPaySetupSavingMessage.getRow(1).getCell(2)).isPresent(false);
		}
		getPremiumAndCoverageTabElement().getAssetList().getAsset(getPaymentPlanComboBox()).setValue("contains=Standard");
		//BUG PAS-7586 A popup about removal of eValue discount is shown on Endorsement when eValue=No and payment plan is changed from Annual to non-Annual
		navigateAndRate();
		assertThat(PremiumAndCoveragesTab.autoPaySetupSavingMessage.getRow(1).getCell(2)).isPresent(isPresent);
		if (isPresent) {
			assertThat(PremiumAndCoveragesTab.autoPaySetupSavingMessage.getRow(1).getCell(2)).hasValue(String.format(AUTOPAY_SAVING_MESSAGE, delta));
		}
	}
}
