package aaa.modules.regression.finance.billing.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Cell;

public class TestFinancePolicyEscheatmentCheckReversals extends PolicyBaseTest {

	/**
	 * @author Maksim Piatrouski
	 * Objectives : check reverse rules of a systematically created escheatment transaction(s).
	 * Preconditions:
	 * 1. Create Annual Policy
	 * 2. Pay $25 more than full with check
	 * 3. Refund 25$ with check - run *aaaRefundGenerationAsyncJob*
	 * 4. Run *aaaRefundDisbursementAsyncJob* to make refund status to issued
	 * 7. Create Renewal
	 * 8. Turn time for more than a year of Refund
	 * 9. Run Esheatment async job at the beginning of the month:  *aaaEscheatmentProcessAsyncJob*
	 * 10. Navigate to BA
	 * TC Steps:
	 * 1. Check new Escheatment transaction exist
	 * 2. Click Reverse action
	 * 3. Click Cancel button - check no new transaction in Payments & Other Transactions table
	 * 4. Click Reverse action
	 * 5. Click Ok button - check:
	 * New transaction created with negative amount
	 * New BAM message created
	 * Balance due and Prepaid amounts recalculated
	 * In original Escheatment transaction status changed from Applied to Reversed
	 * Reversed action not exist
	 */

	BillingAccount billingAccount = new BillingAccount();
	TestData tdBilling = testDataManager.billingAccount;

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-18992")
	public void pas18992_testFinancePolicyEscheatmentCheckReversals() {

		//preconditions
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();

		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Check"), new Dollar(25));

		LocalDateTime paymentDate = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime refundDate = getTimePoints().getRefundDate(paymentDate);
		TimeSetterUtil.getInstance().nextPhase(refundDate);
		JobUtils.executeJob(Jobs.aaaRefundGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaRefundDisbursementAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
		policy.renew().perform();

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusMonths(13));

		JobUtils.executeJob(Jobs.aaaEscheatmentProcessAsyncJob);

		mainApp().reopen();
		SearchPage.openBilling(policyNumber);

		Cell escheatmentActions = BillingSummaryPage.tablePaymentsOtherTransactions
				.getRowContains("Subtype/Reason", "Escheatment").getCell("Action");
		assertThat(escheatmentActions.getValue()).contains("Reverse");

		escheatmentActions.controls.links.get("Reverse").click();
		Page.dialogConfirmation.reject();
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
				.getCell("Subtype/Reason")).hasValue("Escheatment");

		escheatmentActions.controls.links.get("Reverse").click();
		Page.dialogConfirmation.confirm();
		//check new transaction, etc
	}
}
