package aaa.modules.regression.finance.billing.home_ca.ho4;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.BatchJob;
import aaa.main.enums.BillingConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestFinanceCancelNoticeFlagVerification extends FinanceOperations {

	/**
	 * @author Reda Kazlauskiene
	 * Objectives: Policy Flag "Cancel Notice Flag" is remaining on the policy in error
	 * TC Steps:
	 * 1. Create an CA H4 policy
	 * 2. Make payment for deposit as well as DD1-DD9
	 * 3.  Move to DD10-20 and generate installment bill
	 * > The bill is generated for the last installment of the current term
	 * 4. Generate renewal proposal
	 * 5. Generate cancellation notice. Move to DD11 (monthly anniversary
	 * date of the last month of the term) and run aaaCancellationNoticeAsyncJob.
	 * 6. Advance to Cancellation Effective date and make payment for Min due
	 * 7. Verify that Cancel Notice flag isn't remain on renewal term policy
	 */

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO4;
	}

	@Parameters({"state"})
	@StateList(states = {Constants.States.CA})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.BILLING, testCaseId = "PAS-26250")
	public void pas26250_testFinanceCancelNoticeFlagVerification(@Optional("CA") String state) {
		List<LocalDateTime> installmentDueDates;

		mainApp().open();
		createCustomerIndividual();
		TestData policyTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DataGather", "TestData")
				.adjust("PremiumsAndCoveragesQuoteTab|Payment Plan", BillingConstants.PaymentPlan.MONTHLY_STANDARD).resolveLinks();
		String policyNumber = createPolicy(policyTD);
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		SearchPage.openBilling(policyNumber);

		installmentDueDates = BillingHelper.getInstallmentDueDates();
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		Dollar lastInstallmentDue = BillingHelper.getInstallmentDueByDueDate(installmentDueDates.get(10));
		Dollar amount = new Dollar(totalDue.subtract(lastInstallmentDue));
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), amount);

		TimeSetterUtil.getInstance().nextPhase(installmentDueDates.get(10).minusDays(25));
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		new BillingAccount().generateFutureStatement().perform();

		//Initiate Renewal Offer
		renewalImageGeneration(policyNumber, policyExpirationDate);
		renewalPreviewGeneration(policyNumber, policyExpirationDate);
		renewalOfferGeneration(policyNumber, policyExpirationDate);

		SearchPage.openBilling(policyNumber);
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));
		JobUtils.executeJob(BatchJob.aaaCancellationNoticeAsyncJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.verifyCancelNoticeFlagPresent();

		//Verify Cancel Notice Flag
		SearchPage.openBilling(policyNumber);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.CANCEL_NOTICE)
				.verifyRowWithEffectiveDate(policyEffectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyFlag(BillingConstants.PolicyFlag.CANCEL_NOTICE)
				.verifyRowWithEffectiveDate(policyEffectiveDate.plusYears(1));

		Dollar minDue = new Dollar(BillingSummaryPage.tableBillingGeneralInformation.getRow(1)
				.getCell(BillingConstants.BillingGeneralInformationTable.MINIMUM_DUE).getValue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount
				.getTestData("AcceptPayment", "TestData_Cash"), minDue);

		BillingSummaryPage.showPriorTerms();
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(1).getCell(
				BillingConstants.BillingAccountPoliciesTable.POLICY_FLAG)).doesNotHaveValue(BillingConstants.PolicyFlag.CANCEL_NOTICE);
		assertThat(BillingSummaryPage.tableBillingAccountPolicies.getRow(2).getCell(
				BillingConstants.BillingAccountPoliciesTable.POLICY_FLAG)).doesNotHaveValue(BillingConstants.PolicyFlag.CANCEL_NOTICE);
	}
}
