package aaa.modules.regression.billing_and_payments.auto_ss.functional;

import aaa.admin.pages.general.GeneralSchedulerPage;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
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
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.TextBox;

import java.util.HashMap;
import java.util.Map;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;


public class TestRefundProcess extends PolicyBilling {

	private TestData tdBilling = testDataManager.billingAccount;
	private TestData tdRefund = tdBilling.getTestData("Refund", "TestData_Check");
	private BillingAccount billingAccount = new BillingAccount();
	private AcceptPaymentActionTab acceptPaymentActionTab = new AcceptPaymentActionTab();

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
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, dependsOnMethods = "precondJobAdding")
	@TestInfo(component = ComponentConstant.BillingAndPayments.AUTO_SS, testCaseId = "PAS-2186")
	public void pas2186_ManualRefundProcess(@Optional("") String state) {
		Dollar amount = new Dollar(25);
		String checkDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeUtils.MM_DD_YYYY);

		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(getPolicyTD());
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		String policyNumber = PolicySummaryPage.getPolicyNumber();
		log.info("policyNumber: " + policyNumber);

		CustomAssert.enableSoftMode();
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		//"Check" payment type refund creation
		billingAccount.refund().perform(tdRefund, new Dollar(amount));
		Map<String, String> refund1 = new HashMap<>();
		refund1.put(TRANSACTION_DATE, checkDate);
		refund1.put(TYPE, "Refund");
		refund1.put(SUBTYPE_REASON, "Manual Refund");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(STATUS).verify.value("Approved");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(1).verify.value("Void");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(2).verify.value("Issue");

		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(TYPE).controls.links.get(1).click();
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_NUMBER.getLabel(), TextBox.class).verify.value("Processing");
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.CHECK_DATE.getLabel(), TextBox.class).verify.value(checkDate);
		acceptPaymentActionTab.getAssetList().getAsset(BillingAccountMetaData.AcceptPaymentActionTab.AMOUNT.getLabel(), TextBox.class).verify.value(amount.toString());


		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);

		mainApp().reopen();
		SearchPage.search(SearchEnum.SearchFor.BILLING, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(STATUS).verify.value("Issued");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(1).verify.value("Void");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(2).verify.value("Stop");
		BillingSummaryPage.tablePaymentsOtherTransactions.getRow(refund1).getCell(ACTION).controls.links.get(3).verify.value("Clear");

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
