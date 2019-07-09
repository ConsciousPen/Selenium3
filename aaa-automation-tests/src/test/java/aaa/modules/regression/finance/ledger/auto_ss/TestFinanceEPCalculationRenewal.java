package aaa.modules.regression.finance.ledger.auto_ss;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.Dollar;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.product.LedgerHelper;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestFinanceEPCalculationRenewal extends FinanceOperations {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	/**
	 * @author Reda Kazlauskiene
	 * Objectives : Renewal
	 * Preconditions:
	 * Every month earnedPremiumPostingAsyncTaskGenerationJob job is running
	 * 1. Create Annual Auto SS Policy with Effective date today
	 * 2. Create Renewal
	 * 3. Verify Calculations
	 */

	@Parameters({"state"})
	@StateList(states = {Constants.States.NV})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Finance.LEDGER, testCaseId = "PAS-21444")
	public void pas21444_testFinanceEPCalculationFlatCancellation(@Optional("NV") String state) {

		String policyNumber = openAppAndCreatePolicy();
		LocalDateTime today = TimeSetterUtil.getInstance().getCurrentTime();
		LocalDateTime renewalJobEndDate = PolicySummaryPage.getExpirationDate().plusYears(1).plusMonths(1);
		LocalDateTime jobDate = today.plusMonths(1).withDayOfMonth(1);

		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime renewalImageGenerationDate = getTimePoints().getRenewImageGenerationDate(expirationDate);
		LocalDateTime renewalPreviewGenerationDate = getTimePoints().getRenewPreviewGenerationDate(expirationDate);
		LocalDateTime renewalOfferGenerationDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		LocalDateTime billOfferGenerationDate = getTimePoints().getBillGenerationDate(expirationDate);
		LocalDateTime billDueDate = getTimePoints().getBillDueDate(expirationDate);
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(expirationDate);

		jobDate = runEPJobUntil(jobDate, renewalImageGenerationDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);

		searchForPolicy(policyNumber);
		renewalImageGeneration(policyNumber, expirationDate);

		jobDate = runEPJobUntil(jobDate, renewalPreviewGenerationDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		renewalPreviewGeneration(policyNumber, expirationDate);

		jobDate = runEPJobUntil(jobDate, renewalOfferGenerationDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		renewalOfferGeneration(policyNumber, expirationDate);

		jobDate = runEPJobUntil(jobDate, billOfferGenerationDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		generateRenewalBill(policyNumber, today, expirationDate);

		jobDate = runEPJobUntil(jobDate, billDueDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		payRenewalBill(policyNumber, expirationDate);

		jobDate = runEPJobUntil(jobDate, updateStatusDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);
		updatePolicyStatus(policyNumber, today, expirationDate);

		runEPJobUntil(jobDate, renewalJobEndDate, BatchJob.earnedPremiumPostingAsyncTaskGenerationJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber, "Policy Active");
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber, "renewal", true)))
				.isEqualTo(LedgerHelper.getEndingActualPremium(policyNumber));
	}
}
