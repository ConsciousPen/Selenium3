/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.regression.sales.home_ss.ho3.functional;

import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.abstract_tabs.Purchase;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

public class TestInstallmentFeesPopUpAndSavingsMsgMortgagee extends HomeSSHO3BaseTest {

	private static final String AUTOPAY_SAVING_MESSAGE = "This customer can save %s per installment if enrolled into AutoPay with a checking/savings account.";
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();

	/**
	 * *@author Megha Gubbala
	 * *@name Test Autopay Saving Message in P&C tab and in Purchase flow
	 * *@scenario 1. Create new quote
	 * 2. Open P&C, check that Autopay Saving Message is not shown for Mortgagee
	 * 3. Go to Purchase flow, check that Autopay Saving Message is not shown
	 * 4. Issue quote
	 * 5. Start an endorsement
	 * 6. Open P&C, check that Autopay Saving Message is not shown for Mortgagee bill
	 * *@details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = {"PAS-1383"})
	public void pas246_InstallmentFeesPopUpAndSavingsMsgForMortgagee(@Optional("PA") String state) {
		mainApp().open();
		TestData policyTD = getPolicyTD();
		String mortgageeTabKey = TestData.makeKeyPath(MortgageesTab.class.getSimpleName());
		TestData mortgageeTD = getTestSpecificTD("MortgageesTab");
		policyTD.adjust(mortgageeTabKey, mortgageeTD);

		createCustomerIndividual();
		createQuote(policyTD);

		policy.dataGather().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue("Mortgagee Bill");
		premiumsAndCoveragesQuoteTab.calculatePremium();
		PremiumsAndCoveragesQuoteTab.linkPaymentPlan.click();
		PremiumsAndCoveragesQuoteTab.linkViewApplicableFeeSchedule.click();
		Dollar nonEftInstallmentFee = new Dollar(PremiumsAndCoveragesQuoteTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeACH =
				new Dollar(PremiumsAndCoveragesQuoteTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeCreditCard = new Dollar(PremiumsAndCoveragesQuoteTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeDebitCard = new Dollar(PremiumsAndCoveragesQuoteTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE).getValue());
		String delta = nonEftInstallmentFee.subtract(eftInstallmentFeeACH).toString().replace(".00", "");
		Page.dialogConfirmation.buttonCloseWithCross.click();

		autopaySavingMessageCheckForMortgagee(false, delta);

		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();

		assertThat(Purchase.autoPaySetupSavingMessage.getValue()).isEmpty();

		Purchase.linkViewApplicableFeeSchedule.click();
		CustomSoftAssertions.assertSoftly(softly -> {
			assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE)).hasValue(nonEftInstallmentFee.toString());
			assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeACH.toString());
			assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeCreditCard.toString());
			assertThat(Purchase.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE)).hasValue(eftInstallmentFeeDebitCard.toString());
		});
		Page.dialogConfirmation.buttonCloseWithCross.click();

		new PurchaseTab().fillTab(getPolicyTD()).submitTab();

		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		autopaySavingMessageCheckForMortgagee(false, delta);
	}


	private void autopaySavingMessageCheckForMortgagee(boolean isPresent, String delta) {
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue("Mortgagee Bill");
		premiumsAndCoveragesQuoteTab.calculatePremium();

		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PremiumsAndCoveragesQuoteTab.autoPaySetupSavingMessage.getRow(1).getCell(2)).isPresent(false);

			if (Page.dialogConfirmation.isPresent()) {
				Page.dialogConfirmation.confirm();
			}
			premiumsAndCoveragesQuoteTab.calculatePremium();
			softly.assertThat(PremiumsAndCoveragesQuoteTab.autoPaySetupSavingMessage.getRow(1).getCell(2)).isPresent(isPresent);
			if (isPresent) {
				softly.assertThat(PremiumsAndCoveragesQuoteTab.autoPaySetupSavingMessage.getRow(1).getCell(2)).hasValue(String.format(AUTOPAY_SAVING_MESSAGE, delta));
			}
		});
	}

}
