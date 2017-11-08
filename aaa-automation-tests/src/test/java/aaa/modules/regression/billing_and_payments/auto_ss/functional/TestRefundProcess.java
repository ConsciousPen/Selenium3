package aaa.modules.regression.billing_and_payments.auto_ss.functional;

import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.db.DbAwaitHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.BillingAccountMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.policy.PolicyType;
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
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.TextBox;

import java.util.HashMap;
import java.util.Map;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_BY_EVENT_NAME;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;


public class TestRefundProcess extends PolicyBilling {

	private TestData tdBilling = testDataManager.billingAccount;
	private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
	private BillingAccount billingAccount = new BillingAccount();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();

	private static final String REFUND_DOCUMENT_GENERATION_CONFIGURATION_CHECK_SQL = "select dtype, code, displayValue, productCd, riskStateCd, lookuplist_id from LOOKUPVALUE " +
			"where lookuplist_ID = (SELECT ID FROM LOOKUPLIST WHERE LOOKUPNAME='AAARolloutEligibilityLookup') " +
			"and code = 'pcDisbursementEngine' " +
			"and RISKSTATECD = 'VA' " +
			"and DISPLAYVALUE = 'TRUE' ";



	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Test
	@TestInfo(isAuxiliary = true)
	public void precondJobAdding() {
		adminApp().open();
		NavigationPage.toViewLeftMenu(NavigationEnum.AdminAppLeftMenu.GENERAL_SCHEDULER.get());
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_GENERATION_ASYNC_JOB);
		GeneralSchedulerPage.createJob(GeneralSchedulerPage.Job.AAA_REFUND_DISBURSEMENT_ASYNC_JOB);
	}

	@Test()
	@TestInfo(isAuxiliary = true)
	public static void refundDocumentGenerationConfigCheck() {
		CustomAssert.assertTrue("The configuration is missing, run refundDocumentGenerationConfigInsert and restart the env.", DbAwaitHelper.waitForQueryResult(REFUND_DOCUMENT_GENERATION_CONFIGURATION_CHECK_SQL, 5));
	}




	/**
	 * @author Oleg Stasyuk
	 * @name Test Installment Fee split to Credit Card and Debit Card
	 * @scenario 1. Create new policy
	 * 2. Create manual Check refund
	 * 3. Check Refund record in Payment and Other Transactions
	 * 4. Check Actions are Void and Issue
	 * 5. Check values of fields when Opening the Refund transaction's details
	 * 6. run aaaRefundDisbursementAsyncJob
	 * 7. check Refund's status is Issued
	 * 4. Check Actions are Void, Stop, Clear
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "refundDocumentGenerationConfigCheck")//TODO when running suite, the test which has Depends on is not being executed
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2186")
	public void pas2186_RefundProcess(@Optional("") String state) {
		Dollar refundAmount1 = new Dollar(25);
		Dollar refundAmount2 = new Dollar(100);
		String checkDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);
		String checkDate2 = TimeSetterUtil.getInstance().getCurrentTime().plusDays(1).format(DateTimeUtils.MM_DD_YYYY);

		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("policyNumber: " + policyNumber);

		CustomAssert.enableSoftMode();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		//"Check" payment type refund creation
		//PAS-1462 start
		billingAccount.refund().start();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYMENT_METHOD.getLabel(), ComboBox.class).setValue("Check");
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.present(false);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.present(false);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.enabled(false);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.present();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.enabled();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value("");
		AddPaymentMethodsMultiAssetList.buttonAddUpdateCreditCard.verify.present(false);
		acceptPaymentActionTab.cancel();
		//PAS-1462 end

		billingAccount.refund().perform(tdRefund, new Dollar(refundAmount1));

		Map<String, String> refund1 = new HashMap<>();
		refund1.put(TRANSACTION_DATE, checkDate);
		refund1.put(TYPE, "Refund");
		refund1.put(SUBTYPE_REASON, "Manual Refund");
		unissuedRefundActionsCheck(refund1);
		unissuedRefundRecordDetailsCheck(refundAmount1, checkDate, refund1);

		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		checkRefundDocumentInDb(state, policyNumber, 1);
		issuedRefundActionsCheck(refund1, policyNumber);


		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), totalDue.add(refundAmount2));

		TimeSetterUtil.getInstance().nextPhase(DateTimeUtils.getCurrentDateTime().plusDays(1));
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

		Map<String, String> refund2 = new HashMap<>();
		refund2.put(TRANSACTION_DATE, checkDate2);
		refund2.put(TYPE, "Refund");
		refund2.put(SUBTYPE_REASON, "Automated Refund");
		unissuedRefundActionsCheck(refund2);
		unissuedRefundRecordDetailsCheck(refundAmount2, checkDate2, refund2);

		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		checkRefundDocumentInDb(state, policyNumber, 2);
		issuedRefundActionsCheck(refund2, policyNumber);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void unissuedRefundRecordDetailsCheck(Dollar amount, String checkDate, Map<String, String> refund1) {
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(TYPE).controls.links.get(1).click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.value("Processing");
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.value(checkDate);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.PAYEE_NAME.getLabel(), TextBox.class).verify.present();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value(amount.toString());
	}

	private void unissuedRefundActionsCheck(Map<String, String> refund1) {
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(STATUS).verify.value("Approved");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(1).verify.value("Void");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(2).verify.value("Issue");
	}

	private void issuedRefundActionsCheck(Map<String, String> refund1, String policyNumber) {
		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(STATUS).verify.value("Issued");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(1).verify.value("Void");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(2).verify.value("Stop");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(3).verify.value("Clear");
	}

	private static void checkRefundDocumentInDb(String state, String policyNumber, int numberOfDocuments) {
		//PAS-443 start
		if (state.equals("VA")) {
			if (DbAwaitHelper.waitForQueryResult(REFUND_DOCUMENT_GENERATION_CONFIGURATION_CHECK_SQL, 5)){
				String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "55 3500", "REFUND");
				CustomAssert.assertFalse(DbAwaitHelper.waitForQueryResult(query, 5));
			}
		} else {
			String query = String.format(GET_DOCUMENT_BY_EVENT_NAME, policyNumber, "55 3500", "REFUND");
			CustomAssert.assertTrue(DbAwaitHelper.waitForQueryResult(query, 5));
			String query2 = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "55 3500", "REFUND");
			CustomAssert.assertEquals(Integer.parseInt(DBService.get().getValue(query2).get()), numberOfDocuments);
		}
		//PAS-443 end
	}
}
