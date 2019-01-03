package aaa.modules.regression.sales.home_ss.ho3;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.MainPage;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

import java.time.LocalDateTime;
import java.util.List;

import static toolkit.verification.CustomAssertions.assertThat;

/**
 * @author Sushil Sivaram
 * @name Test Create SS Home Policy
 * @scenario 1. Create new or open existed customer.
 * 2. Initiate HSS quote creation.
 * 3. Fill all mandatory fields on all tabs, order reports, calculate premium.
 * 4. Purchase policy.
 * 5. Verify policy status is Active on Consolidated policy view.
 * 6. Run iso batch recieve job at R-63
 * 7. Move policy to first renewal
 * 8. Assert Fireline and PPC reports are not ordered at renewal batch
 * 9. Run iso batch recieve job at 2R-63
 * 10. move policy to second renewal
 * 11.Assert Fireline is not ordered
 * @details
 */
public class TestPolicyFirelineReportsOnRenewal extends HomeSSHO3BaseTest {

	private String policyNumber;
	private LocalDateTime policyExpirationDate;
	private String FirelineOrderDateAtNewBusiness;
	private List<LocalDateTime> installmentDueDates;
	BillingAccount billingAccount = new BillingAccount();
	TestData tdBilling = testDataManager.billingAccount;
	ReportsTab reportsTab = new ReportsTab();
	@Parameters({"state"})
	@StateList(states = {Constants.States.AZ, Constants.States.CO, Constants.States.ID,Constants.States.NV, Constants.States.OR,Constants.States.UT})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3)
	public void testPolicyCreation(@Optional("") String state) {

		//open app and create policy
		createNewPolicy();
		//get fireline order date in inquiry mode
		getReportDate();
		//Move time to R-(35-28) = R-63 and run Offline iso batch jobs and renewal part 1 jobs
        runISOOfflineJobs();
		//validate fire line order date remains the same as new business creation
		validateFirelineAtIssue();
		//Move policy to R-35 and generate renewal images
		renewPolicy();
		//Pay renewal premium
		payTotalAmtDue(policyNumber);
		//update PolicyStatus
		updatePolicyStatus();
		//Validate fireline order d
		validateFirelineAtIssue();
		//get policy expiration date
		getExpirationDateOfPolicy();
		//run iso jobs
		runISOOfflineJobs();
		//validate fire line order date remains the same as new business creation for 2R
		validateFirelineAtIssue();
		//renew policy
		renewPolicy();
		//take total policy payment
		payTotalAmtDue(policyNumber);
		//update policy status
		updatePolicyStatus();
		//assert fireline order date does not change
		validateFirelineAtIssue();
	}

	private void getExpirationDateOfPolicy() {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
	}

	private void validateFirelineAtIssue() {
		mainApp().reopen();
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.REPORTS.get());
		assertThat(FirelineOrderDateAtNewBusiness.equals(firelineOrderDatePostRenewal()));
		mainApp().close();
	}

	private void createNewPolicy() {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		policyNumber = PolicySummaryPage.getPolicyNumber();
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.getExpirationDate()).isEqualTo(PolicySummaryPage.getEffectiveDate().plusYears(1));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		mainApp().close();
	}

	private void getReportDate() {
		mainApp().open();
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.REPORTS.get());
		FirelineOrderDateAtNewBusiness = reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.ORDER_DATE.getLabel()).getValue();
		mainApp().close();
	}

	private void updatePolicyStatus() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
	}

	private void renewPolicy() {
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		HttpStub.executeAllBatches();
		PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
		BindTab bindTab = new BindTab();
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.submitTab();
		assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();
	}

	private void runISOOfflineJobs() {
		LocalDateTime timePoint1 = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate).minusDays(28);
		TimeSetterUtil.getInstance().nextPhase(timePoint1);
		JobUtils.executeJob(Jobs.AAAIsoRenewBatchOrderJob);
		HttpStub.executeAllBatches();
		HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_ISO_BATCH);
		Waiters.SLEEP(5000).go();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
	}

	private String firelineOrderDatePostRenewal() {
		mainApp().reopen();
		MainPage.QuickSearch.buttonSearchPlus.click();
		SearchPage.openPolicy(policyNumber);
		policy.policyInquiry().start();
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.REPORTS.get());
		return reportsTab.tblFirelineReport.getRow(1).getCell(HomeSSMetaData.ReportsTab.FirelineReportRow.ORDER_DATE.getLabel()).getValue();
	}
	@Override
    protected void payTotalAmtDue(String policyNumber) {
		// Open Billing account and Pay min due for the renewal
		mainApp().open();
		SearchPage.openBilling(policyNumber);
		Dollar totalDue = new Dollar(BillingSummaryPage.getTotalDue());
		new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), totalDue);
		// Open Policy (Renewal)
		SearchPage.openPolicy(policyNumber);
	}
}

