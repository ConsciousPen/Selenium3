package aaa.modules.regression.finance.billing.home_ss.ho3;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.billing.BillingPaymentsAndTransactionsVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestFinanceWaiveInstallmentFeeForRenewal extends FinanceOperations {

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Waive installment fee - Renewal
	 * 1. Create Monhtly Policy plan
	 * 2. At DD1-20 create 1st installment and assessb intallment fee
	 * 3. After creation of Installment, pay full amount = tortal due - taxes
	 * 4. Run aaaRefundGenerationAsyncJob job
	 * 5. Verify
	 * 6. Create renewal offer
	 * 7. Verify
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.KY, Constants.States.NJ})
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-22285")
	public void pas22285_testFinanceWaiveInstallmentFeeForRenewal(@Optional("KY") String state) {
		List<LocalDateTime> installmentDueDates;
		LocalDateTime billGenDate;
		Dollar totalPayment;

		mainApp().open();
		createCustomerIndividual();
		TestData td = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData");
		TestData testData = td.adjust(TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), BillingConstants.PaymentPlan.ELEVEN_PAY);
		String policyNumber = createPolicy(testData);
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		SearchPage.openBilling(policyNumber);

		installmentDueDates = BillingHelper.getInstallmentDueDates();
		billGenDate = getTimePoints().getBillGenerationDate(installmentDueDates.get(1));
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(BatchJob.aaaBillingInvoiceAsyncTaskJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		totalPayment = BillingSummaryPage.getTotalDue().subtract(new Dollar(
				BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1)
						.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue()));

		new BillingPaymentsAndTransactionsVerifier().setTransactionDate(billGenDate).setType(BillingConstants.PaymentsAndOtherTransactionType.FEE).verifyPresent();
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalPayment);

		TimeSetterUtil.getInstance().nextPhase(billGenDate.plusDays(1));

		JobUtils.executeJob(BatchJob.aaaRefundGenerationAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policyNumber);

		assertThat(new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRowContains(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON,
				BillingConstants.PaymentsAndOtherTransactionSubtypeReason.NON_EFT_INSTALLMENT_FEE_WAIVED).
				getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue())).isEqualTo(new Dollar(-5));
		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM,
				policyNumber).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue())).isEqualTo(new Dollar(0));

		//Initiate Renewal Offer
		renewalImageGeneration(policyNumber, policyExpirationDate);
		renewalPreviewGeneration(policyNumber, policyExpirationDate);
		renewalOfferGeneration(policyNumber, policyExpirationDate);
		renewalPremiumNotice(policyNumber, policyEffectiveDate, policyExpirationDate);

		mainApp().open();
		SearchPage.openBilling(policyNumber);

		assertThat(new Dollar(BillingSummaryPage.tableBillingAccountPolicies.getRow(BillingConstants.BillingAccountPoliciesTable.POLICY_NUM,
				policyNumber).getCell(BillingConstants.BillingAccountPoliciesTable.TOTAL_DUE).getValue())).isNotEqualTo(new Dollar(0));
	}
}
