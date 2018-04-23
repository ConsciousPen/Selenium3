package aaa.modules.regression.billing_and_payments.auto_ca.choice.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.billing_and_payments.template.functional.TestEarnedPremiumWriteOffAbstract;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestEarnedPremiumWriteOff extends TestEarnedPremiumWriteOffAbstract{

	private PremiumAndCoveragesTab premiumAndCoverages = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBind = new DocumentsAndBindTab();

	@Override
	protected TestData getTdPolicy() {
		return testDataManager.policy.get(getPolicyType());
	}

	@Override
	protected TestData getTestSpecificTDForTestEndorsement() {
		return getTestSpecificTD("TestData_Endorsement");
	}

	@Override
	public void changeStatusFromDeclineToProposed(String policyNumber) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoverages.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DOCUMENTS_AND_BIND.get());
		documentsAndBind.submitTab();
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-11697"})
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-11697"})
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-11697"})
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-11697"})
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-11697"})
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-11697"})
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
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_CHOICE, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffNoAP(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffNoAP(state);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}
}
