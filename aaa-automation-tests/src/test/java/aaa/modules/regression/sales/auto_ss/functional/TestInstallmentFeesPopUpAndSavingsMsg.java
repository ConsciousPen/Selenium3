/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestInstallmentFeesPopUpAndSavingsMsg extends AutoSSBaseTest {

	private static final String AUTOPAY_SAVING_MESSAGE = "This customer can save %s per installment if enrolled into AutoPay with a checking/savings account.";
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab(); //TODO test with policy.dataGather().getView().getTab(DocumentsAndBindTab.class); instead of new Tab();
	private ErrorTab errorTab = new ErrorTab();

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
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-246"})
	public void pas246_InstallmentFeesPopUpAndSavingsMsg(@Optional("VA") String state) {
		mainApp().open();

		createCustomerIndividual();
		createQuote();
		policy.dataGather().start();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.linkPaymentPlan.click();
		PremiumAndCoveragesTab.linkViewApplicableFeeSchedule.click();
		Dollar nonEftInstallmentFee = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeACH =
				new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeCreditCard = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeDebitCard = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE).getValue());
		String delta = nonEftInstallmentFee.subtract(eftInstallmentFeeACH).toString().replace(".00", "");
		Page.dialogConfirmation.buttonCloseWithCross.click();

		autopaySavingMessageCheck(false, delta);

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.fillTab(getPolicyDefaultTD());
		documentsAndBindTab.submitTab();
		CustomAssert.assertTrue(Purchase.autoPaySetupSavingMessage.getRow(1).getCell(2).getValue().equals(String.format(AUTOPAY_SAVING_MESSAGE, delta)));

		Purchase.linkViewApplicableFeeSchedule.click();
		assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE)).hasValue(nonEftInstallmentFee.toString());
		assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeACH.toString());
		assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeCreditCard.toString());
		assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeDebitCard.toString());
		Page.dialogConfirmation.buttonCloseWithCross.click();

		new PurchaseTab().fillTab(getPolicyTD()).submitTab();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		autopaySavingMessageCheck(true, delta);

		PremiumAndCoveragesTab.linkPaymentPlan.click();
		PremiumAndCoveragesTab.linkViewApplicableFeeSchedule.click();
		assertThat(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE)).hasValue(nonEftInstallmentFee.toString());
		assertThat(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeACH.toString());
		assertThat(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeCreditCard.toString());
		assertThat(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeDebitCard.toString());
		Page.dialogConfirmation.buttonCloseWithCross.click();

		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());
		documentsAndBindTab.submitTab();

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void autopaySavingMessageCheck(boolean isPresent, String delta) {
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("Annual");
		PremiumAndCoveragesTab.calculatePremium();
		PremiumAndCoveragesTab.autoPaySetupSavingMessage.getRow(1).getCell(2).verify.present(false);

		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue("contains=Standard");
		//BUG PAS-7586 A popup about removal of eValue discount is shown on Endorsement wjen eValue=No and payment plan is changed from Annual to non-Annual
		PremiumAndCoveragesTab.calculatePremium();
		PremiumAndCoveragesTab.autoPaySetupSavingMessage.getRow(1).getCell(2).verify.present(isPresent);
		if (isPresent) {
			CustomAssert.assertTrue(PremiumAndCoveragesTab.autoPaySetupSavingMessage.getRow(1).getCell(2).getValue().equals(String.format(AUTOPAY_SAVING_MESSAGE, delta)));
		}
	}

}
