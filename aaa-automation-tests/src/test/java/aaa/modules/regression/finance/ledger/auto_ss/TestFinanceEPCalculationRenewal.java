package aaa.modules.regression.finance.ledger.auto_ss;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.LedgerHelper;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.finance.template.FinanceOperations;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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

		jobDate = runEPJobUntil(jobDate, renewalImageGenerationDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

        searchForPolicy(policyNumber);
		renewalImageGeneration(expirationDate, policyNumber);

        jobDate = runEPJobUntil(jobDate, renewalPreviewGenerationDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		renewalPreviewGeneration(expirationDate, policyNumber);

        jobDate = runEPJobUntil(jobDate, renewalOfferGenerationDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        renewalOfferGeneration(expirationDate, policyNumber);

        jobDate = runEPJobUntil(jobDate, billOfferGenerationDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
		generateRenewalBill(expirationDate, today, policyNumber);

        jobDate = runEPJobUntil(jobDate, billDueDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        payRenewalBill(expirationDate, policyNumber);

        jobDate = runEPJobUntil(jobDate, updateStatusDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);
        updatePolicyStatus(expirationDate, today, policyNumber);

        runEPJobUntil(jobDate, renewalJobEndDate, Jobs.earnedPremiumPostingAsyncTaskGenerationJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber, "Policy Active");
		PolicySummaryPage.buttonTransactionHistory.click();

		assertThat(LedgerHelper.getEndingActualPremium(policyNumber))
				.isEqualTo(new Dollar(LedgerHelper.getEarnedMonthlyReportedPremiumTotal(policyNumber)));

		List<TxType> txTypes = Arrays.asList(TxType.ISSUE, TxType.CANCEL);
		validateEPCalculations(policyNumber, txTypes, today, expirationDate);
	}
}