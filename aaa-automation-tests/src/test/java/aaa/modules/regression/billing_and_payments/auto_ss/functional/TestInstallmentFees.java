package aaa.modules.regression.billing_and_payments.auto_ss.functional;

import static aaa.main.enums.BillingConstants.BillingAccountPoliciesTable.MIN_DUE;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.billing.account.actiontabs.UpdateBillingAccountActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.billing_and_payments.template.PolicyBilling;
import aaa.toolkit.webdriver.customcontrols.AddPaymentMethodsMultiAssetList;
import aaa.toolkit.webdriver.customcontrols.InquiryAssetList;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestInstallmentFees extends PolicyBilling {

	private static final String AUTOPAY_SAVING_MESSAGE= "This customer can save %s per installment if enrolled into AutoPay with a checking/savings account.";
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData cashPayment = tdBilling.getTestData("AcceptPayment", "TestData_Cash");
	private TestData checkPayment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
	private TestData ccPayment = tdBilling.getTestData("AcceptPayment", "TestData_CC");

	private TestData eftPayment = tdBilling.getTestData("AcceptPayment", "TestData_EFT");
	private TestData refund = tdBilling.getTestData("Refund", "TestData_Cash");
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private BillingAccount billingAccount = new BillingAccount();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
	private UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Installment Fee split to Credit Card and Debit Card
	 * @scenario 1. Create new policy
	 * 2. Do endorsement, check P&C tab Installment Fee Table values
	 * 3. Start Update Billing Account, check the saving message for switching rom Non-EFT to EFT payment Method
	 * 4. Add ACH, CC, DC
	 * 5. Switch between them, generating bills and checking Installment Fee Labels and Amounts
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-1943")
	public void pas1943_InstallmentFeeCreditDebitCardSplit(@Optional("UT") String state) {
		String paymentPlan = "contains=Standard"; //"Monthly"
		String premiumCoverageTabMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
		TestData policyTdAdjusted = getPolicyTD().adjust(premiumCoverageTabMetaKey, paymentPlan);

		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(policyTdAdjusted);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		pas1943_InstallmentFeeCreditDebitCardSplitBody(policyNumber);
	}

	/**
	 * @author Oleg Stasyuk
	 * @name Test Installment Fee split to Credit Card and Debit Card
	 * @scenario 1. Create new policy
	 * 2. Do endorsement, check P&C tab Installment Fee Table values
	 * 3. Start Update Billing Account, check the saving message for switching rom Non-EFT to EFT payment Method
	 * 4. Add ACH, CC, DC
	 * 5. Switch between them, generating bills and checking Installment Fee Labels and Amounts
	 * @details
	 */
	@Parameters({"state"})
	@StateList(statesExcept = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-1943")
	public void pas1455_MessageAmountSavedIfSignedForAutoPayACHRenewalEndorsement(@Optional("UT") String state) {
		String paymentPlan = "contains=Standard"; //"Monthly"
		String premiumCoverageTabMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
		TestData policyTdAdjusted = getPolicyTD().adjust(premiumCoverageTabMetaKey, paymentPlan);

		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(policyTdAdjusted);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue);

		LocalDateTime renewalOfferDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewalOfferDate);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart1);
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);

		uiMessageCheck();
	}

	private void uiMessageCheck() {
		//check Installment Fees table in P&C
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.linkPaymentPlan.click();
		PremiumAndCoveragesTab.linkViewApplicableFeeSchedule.click();
		Dollar nonEftInstallmentFee = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeACH = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE).getValue());
		Page.dialogConfirmation.buttonCloseWithCross.click();

		CustomSoftAssertions.assertSoftly(softly -> {
			//PAS-1455 start
			softly.assertThat(PremiumAndCoveragesTab.autoPaySetupSavingMessage.getRow(1).getCell(2))
					.hasValue(String.format(AUTOPAY_SAVING_MESSAGE, nonEftInstallmentFee.subtract(eftInstallmentFeeACH).toString().replace(".00", "")));
			//PAS-1455 end

			premiumAndCoveragesTab.saveAndExit();

			//check Info Message about saving by switching to EFT
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			billingAccount.update().start();
			//PAS-241 Start
			String installmentSavingInfo = String.format(AUTOPAY_SAVING_MESSAGE, nonEftInstallmentFee.subtract(eftInstallmentFeeACH).toString().replace(".00", ""));
			//PAS-241 End
			softly.assertThat(BillingAccount.tableInstallmentSavingInfo.getRow(1).getCell(2)).hasValue(installmentSavingInfo);

			//PAS-3846 start - will change in future
			AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("contains=Card");
			//PAS-4127 start
			softly.assertThat(updateBillingAccountActionTab.isFieldThatIsNotInAssetListIsPresent("Card Type")).as("Fiels 'Card Type' should be absent").isFalse();
			//PAS-4127 end
		});
	}

	private void pas1943_InstallmentFeeCreditDebitCardSplitBody(String policyNumber) {
		CustomSoftAssertions.assertSoftly(softly -> {
			//check Installment Fees table in P&C
			policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
			PremiumAndCoveragesTab.linkPaymentPlan.click();
			PremiumAndCoveragesTab.linkViewApplicableFeeSchedule.click();
			Dollar nonEftInstallmentFee = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE).getValue());
			Dollar eftInstallmentFeeACH =
					new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE).getValue());
			Dollar eftInstallmentFeeCreditCard = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE).getValue());
			Dollar eftInstallmentFeeDebitCard = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE).getValue());
			Page.dialogConfirmation.buttonCloseWithCross.click();

			//PAS-1455 start
			softly.assertThat(PremiumAndCoveragesTab.autoPaySetupSavingMessage.getRow(1).getCell(2))
					.hasValue(String.format(AUTOPAY_SAVING_MESSAGE, nonEftInstallmentFee.subtract(eftInstallmentFeeACH).toString().replace(".00", "")));
			//PAS-1455 end

			premiumAndCoveragesTab.saveAndExit();

			//check Info Message about saving by switching to EFT
			NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
			billingAccount.update().start();
			//PAS-241 Start
			String installmentSavingInfo = String.format(AUTOPAY_SAVING_MESSAGE, nonEftInstallmentFee.subtract(eftInstallmentFeeACH).toString().replace(".00", ""));
			//PAS-241 End
			softly.assertThat(BillingAccount.tableInstallmentSavingInfo.getRow(1).getCell(2)).hasValue(installmentSavingInfo);

			//PAS-3846 start - will change in future
			AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
			acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("contains=Card");
			//PAS-4127 start
			softly.assertThat(updateBillingAccountActionTab.isFieldThatIsNotInAssetListIsPresent("Card Type")).as("Fiels 'Card Type' should be absent").isFalse();
			//PAS-4127 end

			TestData dcPayment = getTestSpecificTD("TestData_DebitCard");
			TestData ccPayment = getTestSpecificTD("TestData_CreditCard");
			TestData dcVisa = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(0);
			TestData ccMaster = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(2);
			//PAS-834 start
			updateBillingAccountCardFormatCheck(dcVisa, "Debit", softly);
			updateBillingAccountCardFormatCheck(ccMaster, "Credit", softly);
			//PAS-834 end
			Tab.buttonBack.click();
			Tab.buttonCancel.click();
			//PAS-3846 end

			//check Non-EFT fee
			feeSubtypeCheck(policyNumber, 2, "Non EFT Installment Fee", nonEftInstallmentFee, softly);
			billingAccount.acceptPayment().perform(cashPayment, new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(MIN_DUE).getValue()));

			//check ACH Fee
			billingAccount.update().perform(getTestSpecificTD("TestData_UpdateBilling"));
			//TODO numberACH will be used for Refund check in future
			String numberACH = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(1).getValue("Account #"); //ACH
			feeSubtypeCheck(policyNumber, 3, "EFT Installment Fee - ACH", eftInstallmentFeeACH, softly);
			billingAccount.acceptPayment().perform(eftPayment, new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(MIN_DUE).getValue()));

			//check Non-EFT DC fee
			autopaySelection("contains=Visa");
			//TODO visaNumber will be used for Refund check in future
			String visaNumber = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(0).getValue("Number");  //Visa
			feeSubtypeCheck(policyNumber, 4, "EFT Installment Fee - Debit Card", eftInstallmentFeeDebitCard, softly);
			billingAccount.acceptPayment().perform(dcPayment, new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(MIN_DUE).getValue()));
			//PAS-834 start
			completedPaymentCreditDebitCardCheck(dcVisa, "Debit", softly);
			//PAS-834 end

			//check Non-EFT CC fee
			autopaySelection("contains=Master");
			//TODO masterCard will be used for Refund check in future
			String masterNumber = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(2).getValue("Number");  //Master
			feeSubtypeCheck(policyNumber, 5, "EFT Installment Fee - Credit Card", eftInstallmentFeeCreditCard, softly);
			billingAccount.acceptPayment().perform(ccPayment, new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(MIN_DUE).getValue()));

			//PAS-834 start
			completedPaymentCreditDebitCardCheck(ccMaster, "Credit", softly);
			//PAS-834 end
		});
	}

	private void completedPaymentCreditDebitCardCheck(TestData cardData, String cardType, ETCSCoreSoftAssertions softly) {
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).controls.links.get("Payment").click();

		String expectedValueCard = formattedPaymentMethodValue(cardData, cardType);
		softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD)).valueContains(expectedValueCard);
		Tab.buttonBack.click();
	}

	private void updateBillingAccountCardFormatCheck(TestData cardData, String cardType, ETCSCoreSoftAssertions softly) {
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.TYPE)
				.fill(cardData);
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.NUMBER).fill(cardData);
		AddPaymentMethodsMultiAssetList.buttonAddUpdatePaymentMethod.click();

		String expectedValueCard = formattedPaymentMethodValue(cardData, cardType);
		//BUG PAS-4280 Last 4 digits for Card are displayed incorrectly after Updating Billing Account on the Billing Page
		softly.assertThat(AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Payment Method")).valueContains(expectedValueCard);
		AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Action").controls.links.get("View").click();
		//PAS-4127 start
		softly.assertThat(updateBillingAccountActionTab.getInquiryAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHODS.getLabel(), InquiryAssetList.class)
				.getStaticElement(BillingAccountMetaData.AddPaymentMethodTab.TYPE)).hasValue(cardData.getValue("Type") + " " + cardType + " Card");
		//PAS-4127 end

		softly.assertThat(AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Payment Method")).valueContains(expectedValueCard);
		AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Action").controls.links.get("Edit").click();
		//PAS-4127 start
		softly.assertThat(updateBillingAccountActionTab.getInquiryAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHODS.getLabel(), InquiryAssetList.class)
				.getStaticElement(BillingAccountMetaData.AddPaymentMethodTab.TYPE)).hasValue(cardData.getValue("Type") + " " + cardType + " Card");
		//PAS-4127 end

		softly.assertThat(AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Payment Method")).valueContains(expectedValueCard);
		AddPaymentMethodsMultiAssetList.tablePaymentMethods.getRow(1).getCell("Action").controls.links.get("Delete").click();
		Page.dialogConfirmation.confirm();
	}

	private String formattedPaymentMethodValue(TestData cardData, String cardType) {
		return cardType + " Card " + cardData.getValue("Type").replace(" ", "") + "-" + cardData.getValue("Number").substring(12, 16) + " expiring ";
	}

	private void feeSubtypeCheck(String policyNumber, int installmentNumber, String transactionSubtype, Dollar amount, ETCSCoreSoftAssertions softly) {
		AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
		LocalDateTime billDueDate3 = BillingSummaryPage.getInstallmentDueDate(installmentNumber).minusDays(20);
		TimeSetterUtil.getInstance().nextPhase(billDueDate3);
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		softly.assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON)).hasValue(transactionSubtype);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE).controls.links.get("Fee").click();
		softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_TYPE)).hasValue("Fee");
		softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_SUBTYPE)).hasValue(transactionSubtype);
		softly.assertThat(acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT)).hasValue(amount.toString());
		acceptPaymentActionTab.back();
	}

	private void autopaySelection(String autopaySelectionValue) {
		UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
		BillingSummaryPage.linkUpdateBillingAccount.click();
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION).setValue(autopaySelectionValue);
		UpdateBillingAccountActionTab.buttonSave.click();
	}
}
