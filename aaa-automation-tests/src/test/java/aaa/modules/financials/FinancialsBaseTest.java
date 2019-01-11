package aaa.modules.financials;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import static toolkit.verification.CustomAssertions.assertThat;
import static toolkit.verification.CustomSoftAssertions.assertSoftly;
import java.time.LocalDateTime;

public class FinancialsBaseTest extends FinancialsTestDataFactory {

	protected String createFinancialPolicy() {
		return createFinancialPolicy(getPolicyTD());
	}

	protected String createFinancialPolicy(TestData td) {
		String policyNum = createPolicy(td);
		ALL_POLICIES.add(policyNum);
		return policyNum;
	}

	protected Dollar getTotalTermPremium() {
		if (!getPolicyType().isAutoPolicy()) {
			return PolicySummaryPage.getTotalPremiumSummaryForProperty();
		}
		if (isStateCA()){
			return new Dollar(PolicySummaryPage.tableCoveragePremiumSummaryCA.getRow(3).getCell(2).getValue());
		}
		return new Dollar(PolicySummaryPage.getAutoCoveragesSummaryTestData().getValue("Total Term Premium"));
	}

	protected Dollar payTotalAmountDue(){
		// Open Billing account and Pay min due for the renewal
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar due = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), due);

		// Open Policy Summary Page
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
		return due;
	}
	protected Dollar payMinAmountDue() {
		// Open Billing account and Pay min due for the renewal
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		Dollar due = new Dollar(BillingSummaryPage.getMinimumDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), due);

		// Open Policy Summary Page
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.POLICY.get());
		return due;
	}

	protected void cancelPolicy() {
		cancelPolicy(TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void cancelPolicy(LocalDateTime cxDate) {
		policy.cancel().perform(getCancellationTD(cxDate));
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
	}

	protected void performReinstatement() {
		policy.reinstate().perform(getReinstatementTD());
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected void performReinstatementWithLapse(LocalDateTime effDate, String policyNumber) {
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(effDate.plusMonths(1).minusDays(20).with(DateTimeUtils.closestPastWorkingDay));
		JobUtils.executeJob(Jobs.changeCancellationPendingPoliciesStatus);
		TimeSetterUtil.getInstance().nextPhase(effDate.plusDays(20));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.reinstate().perform(getReinstatementTD());
		if (Page.dialogConfirmation.buttonYes.isPresent()) {
			Page.dialogConfirmation.buttonYes.click();
		}
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	protected Dollar performAPEndorsement(String policyNumber) {
		return performAPEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected Dollar performAPEndorsement(String policyNumber, LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getAddPremiumTD());
		Dollar addedPrem = payTotalAmountDue();
		SearchPage.openPolicy(policyNumber);
		return addedPrem;
	}

	protected void performRPEndorsement(LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getReducePremiumTD());
	}

	protected void performNPBEndorsement(String policyNumber) {
		performNPBEndorsement(policyNumber, TimeSetterUtil.getInstance().getCurrentTime());
	}

	protected void performNPBEndorsement(String policyNumber, LocalDateTime effDate) {
		policy.endorse().perform(getEndorsementTD(effDate));
		policy.getDefaultView().fill(getNPBEndorsementTD());
		SearchPage.openPolicy(policyNumber);
	}

}
