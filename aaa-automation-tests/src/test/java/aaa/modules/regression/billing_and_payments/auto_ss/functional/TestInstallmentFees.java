package aaa.modules.regression.billing_and_payments.auto_ss.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
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
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static aaa.main.enums.BillingConstants.BillingAccountPoliciesTable.MIN_DUE;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.INSTALLMENT_FEE;
import static aaa.main.enums.PolicyConstants.PolicyCoverageInstallmentFeeTable.PAYMENT_METHOD;


public class TestInstallmentFees extends PolicyBilling {

	private TestData tdBilling = testDataManager.billingAccount;
	private TestData cash_payment = tdBilling.getTestData("AcceptPayment", "TestData_Cash");
	private TestData check_payment = tdBilling.getTestData("AcceptPayment", "TestData_Check");
	private TestData cc_payment = tdBilling.getTestData("AcceptPayment", "TestData_CC");
	private TestData eft_payment = tdBilling.getTestData("AcceptPayment", "TestData_EFT");
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-1943")
	public void pas1943_InstallmentFeeCreditDebitCardSplit(@Optional("") String state) {

		String paymentPlan = "contains=Standard"; //"Monthly"
		String premiumCoverageTabMetaKey = TestData.makeKeyPath(new PremiumAndCoveragesTab().getMetaKey(), AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel());
		TestData policyTdAdjusted = getPolicyTD().adjust(premiumCoverageTabMetaKey, paymentPlan);

		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(policyTdAdjusted);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		//check Installment Fees table in P&C
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.linkPaymentPlan.click();
		PremiumAndCoveragesTab.linkViewApplicableFeeSchedule.click();
		Dollar nonEftInstallmentFee = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Any").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeACH = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Checking / Savings Account (ACH)").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeCreditCard = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Credit Card").getCell(INSTALLMENT_FEE).getValue());
		Dollar eftInstallmentFeeDebitCard = new Dollar(PremiumAndCoveragesTab.tableInstallmentFeeDetails.getRowContains(PAYMENT_METHOD, "Debit Card").getCell(INSTALLMENT_FEE).getValue());
		Page.dialogConfirmation.buttonCloseWithCross.click();
		premiumAndCoveragesTab.saveAndExit();

		//check Info Message about saving by switching to EFT
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.update().start();
		String installmentSavingInfo = String.format("This customer can save %s per installment if enrolled into AutoPay with a checking/savings account.", nonEftInstallmentFee.subtract(eftInstallmentFeeACH).toString().replace(".00", ""));
		CustomAssert.assertTrue(BillingAccount.tableInstallmentSavingInfo.getRow(1).getCell(2).getValue().equals(installmentSavingInfo));

		//PAS-3846 start - will change in future
		AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD).setValue("contains=Card");
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.CARD_TYPE).verify.value("Credit Card");
		List<String> cardType = new ArrayList<>();
		cardType.add("Credit Card");
		cardType.add("Debit Card");
		CustomAssert.assertTrue(updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.CARD_TYPE).getAllValues().containsAll(cardType));
		CustomAssert.assertFalse(updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.PAYMENT_METHODS).getAsset(BillingAccountMetaData.AddPaymentMethodTab.CARD_TYPE).isEnabled());
		Tab.buttonBack.click();
		Tab.buttonCancel.click();
		//PAS-3846 end

		//check Non-EFT fee
		feeSubtypeCheck(policyNumber, 2, "Non EFT Installment Fee", nonEftInstallmentFee);
		billingAccount.acceptPayment().perform(cash_payment, new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(MIN_DUE).getValue()));

		//check ACH Fee
		billingAccount.update().perform(getTestSpecificTD("TestData_UpdateBilling"));
		//TODO numberACH will be used for Refund check in future
		String numberACH = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(1).getValue("Account #"); //ACH
		feeSubtypeCheck(policyNumber, 3, "EFT Installment Fee - ACH", eftInstallmentFeeACH);
		billingAccount.acceptPayment().perform(eft_payment, new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(MIN_DUE).getValue()));

		//check Non-EFT DC fee
		autopaySelection("contains=Visa");
		//TODO visaNumber will be used for Refund check in future
		String visaNumber = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(0).getValue("Number");  //Visa
		feeSubtypeCheck(policyNumber, 4, "EFT Installment Fee - Debit Card", eftInstallmentFeeDebitCard);
		billingAccount.acceptPayment().perform(cc_payment, new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(MIN_DUE).getValue()));

		//check Non-EFT CC fee
		autopaySelection("contains=Master");
		//TODO masterCard will be used for Refund check in future
		String masterNumber = getTestSpecificTD("TestData_UpdateBilling").getTestData("UpdateBillingAccountActionTab").getTestDataList("PaymentMethods").get(2).getValue("Number");  //Master
		feeSubtypeCheck(policyNumber, 5, "EFT Installment Fee - Credit Card", eftInstallmentFeeCreditCard);
		billingAccount.acceptPayment().perform(cc_payment, new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(MIN_DUE).getValue()));

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void feeSubtypeCheck(String policyNumber, int installmentNumber, String transactionSubtype, Dollar amount) {
		AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();
		LocalDateTime billDueDate3 = BillingSummaryPage.getInstallmentDueDate(installmentNumber).minusDays(20);
		TimeSetterUtil.getInstance().nextPhase(billDueDate3);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON).verify.value(transactionSubtype);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.TYPE).controls.links.get("Fee").click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_TYPE.getLabel(), ComboBox.class).verify.value("Fee");
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.TRANSACTION_SUBTYPE.getLabel(), ComboBox.class).verify.value(transactionSubtype);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value(amount.toString());
		acceptPaymentActionTab.back();
	}

	private void autopaySelection(String autopaySelectionValue) {
		UpdateBillingAccountActionTab updateBillingAccountActionTab = new UpdateBillingAccountActionTab();
		BillingSummaryPage.linkUpdateBillingAccount.click();
		updateBillingAccountActionTab.getAssetList().getAsset(BillingAccountMetaData.UpdateBillingAccountActionTab.AUTOPAY_SELECTION.getLabel(), ComboBox.class).setValue(autopaySelectionValue);
		UpdateBillingAccountActionTab.buttonSave.click();
	}
}
