package aaa.modules.regression.billing_and_payments.home_ca.ho3.functional;

import static aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.modules.regression.billing_and_payments.template.functional.TestEarnedPremiumWriteOffAbstract;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestEarnedPremiumWriteOff extends TestEarnedPremiumWriteOffAbstract{

	private PremiumsAndCoveragesQuoteTab premiumAndCoverage = new PremiumsAndCoveragesQuoteTab();
	private BindTab bindTab = new BindTab();

	@Override
	protected TestData getTdPolicy() {
		return testDataManager.policy.get(getPolicyType());
	}

	@Override
	protected TestData getTestSpecificTDForTestEndorsement() {
		return getTestSpecificTD("TestData_Endorsement");
	}

	public void changeStatusFromDeclineToProposed(String policyNumber) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		policy.renew().perform();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		premiumAndCoverage.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());
		bindTab.submitTab();
	}

	@Override
	public String processEarnedPremiumJobWithAPEndorsementMortgagee(LocalDateTime expirationDate, String policyNumber, String endorsementAmount) {
		processEarnedPremiumJob(expirationDate, policyNumber);
		assertThat(BillingSummaryPage.labelEarnedPremiumWriteOff).isPresent();
		assertThat(BillingSummaryPage.labelAmountEarnedPremiumWriteOff).hasValue(endorsementAmount);
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(TYPE)).hasValue("Adjustment");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(SUBTYPE_REASON)).hasValue("Earned Premium Write-off");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(AMOUNT).getValue()).isEqualTo("("+endorsementAmount+")");
		assertThat(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(1).getCell(STATUS)).hasValue("Applied");
		return endorsementAmount;
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment less that earned premium write off.
	 * 10. Verify that earned premium became less (earned premium write off reversal transaction).
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffLessDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffLessDecline(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment equal to earned premium write off.
	 * 10. Verify that earned premium is fully reversed.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffEqualDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffEqualDecline(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment more than earned premium write off.
	 * 10. Verify that earned premium is fully reversed.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffMoreDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffMoreDecline(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Change status to Proposed.
	 * 10. Make payment less that earned premium write off.
	 * 11. Verify that earned premium became less (earned premium write off reversal transaction).
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffLessProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffLessProposed(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Change status to Proposed.
	 * 10. Make payment equal to earned premium write off.
	 * 11. Verify that earned premium is fully reversed.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffEqualProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffEqualProposed(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Change status to Proposed.
	 * 10. Make payment more than earned premium write off.
	 * 11. Verify that earned premium is fully reversed.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffMoreProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffMoreProposed(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Move time to R and run status update job.
	 * 5. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 6. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 7. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 8. Earned premium write off is absent on policy
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffNoAP(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffNoAP(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy with Mortgagee.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment equal to earned premium write off.
	 * 10. Verify that earned premium is fully reversed.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffLessDeclineMortgagee(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffLessDeclineMortgagee(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy with Mortgagee.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Change status to Proposed.
	 * 10. Make payment equal to earned premium write off.
	 * 11. Verify that earned premium is fully reversed.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffEqualProposedMortgagee(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffEqualProposedMortgagee(state);
	}

	/**
	 * @name Test Earned premium write off generation
	 * @scenario 1. Create Customer
	 * 2. Create active policy with Mortgagee.
	 * 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * 4. Do AP Endorsement at R-10.
	 * 5. Move time to R and run status update job.
	 * 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * 9. Make payment more than earned premium write off.
	 * 10. Verify that earned premium is fully reversed.
	 * @details
	 */
	@Parameters({STATE_PARAM})
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.HOME_CA_HO3, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffMoreDeclineMortgagee(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffMoreDeclineMortgagee(state);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
}
