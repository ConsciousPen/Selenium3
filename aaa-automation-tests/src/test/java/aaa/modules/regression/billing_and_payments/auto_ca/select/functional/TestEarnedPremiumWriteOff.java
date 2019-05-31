package aaa.modules.regression.billing_and_payments.auto_ca.select.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
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
import aaa.utils.StateList;
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
	 * <b> Test Earned premium write off generation </b>
	 * <p> Steps: 1. Create Customer
	 * <p> 2. Create active policy.
	 * <p> 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * <p> 4. Do AP Endorsement at R-10.
	 * <p> 5. Move time to R and run status update job.
	 * <p> 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * <p> 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * <p> 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * <p> 9. Make payment less that earned premium write off.
	 * <p> 10. Verify that earned premium became less (earned premium write off reversal transaction).
	 *
	 */
	@Override
	@Parameters({STATE_PARAM})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffLessDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffLessDecline(state);
	}

	/**
	 * <b> Test Earned premium write off generation </b>
	 * <p> Steps: 1. Create Customer
	 * <p> 2. Create active policy.
	 * <p> 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * <p> 4. Do AP Endorsement at R-10.
	 * <p> 5. Move time to R and run status update job.
	 * <p> 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * <p> 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * <p> 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * <p> 9. Make payment equal to earned premium write off.
	 * <p> 10. Verify that earned premium is fully reversed.
	 *
	 */
	@Override
	@Parameters({STATE_PARAM})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffEqualDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffEqualDecline(state);
	}

	/**
	 * <b> Test Earned premium write off generation </b>
	 * <p> Steps: 1. Create Customer
	 * <p> 2. Create active policy.
	 * <p> 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * <p> 4. Do AP Endorsement at R-10.
	 * <p> 5. Move time to R and run status update job.
	 * <p> 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * <p> 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * <p> 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * <p> 9. Make payment more than earned premium write off.
	 * <p> 10. Verify that earned premium is fully reversed.
	 *
	 */
	@Override
	@Parameters({STATE_PARAM})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffMoreDecline(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffMoreDecline(state);
	}

	/**
	 * <b> Test Earned premium write off generation </b>
	 * <p> Steps: 1. Create Customer
	 * <p> 2. Create active policy.
	 * <p> 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * <p> 4. Do AP Endorsement at R-10.
	 * <p> 5. Move time to R and run status update job.
	 * <p> 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * <p> 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * <p> 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * <p> 9. Change status to Proposed.
	 * <p> 10. Make payment less that earned premium write off.
	 * <p> 11. Verify that earned premium became less (earned premium write off reversal transaction).
	 *
	 */
	@Override
	@Parameters({STATE_PARAM})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffLessProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffLessProposed(state);
	}

	/**
	 * <b> Test Earned premium write off generation </b>
	 * <p> Steps: 1. Create Customer
	 * <p> 2. Create active policy.
	 * <p> 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * <p> 4. Do AP Endorsement at R-10.
	 * <p> 5. Move time to R and run status update job.
	 * <p> 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * <p> 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * <p> 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * <p> 9. Change status to Proposed.
	 * <p> 10. Make payment equal to earned premium write off.
	 * <p> 11. Verify that earned premium is fully reversed.
	 *
	 */
	@Override
	@Parameters({STATE_PARAM})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffEqualProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffEqualProposed(state);
	}

	/**
	 * <b> Test Earned premium write off generation </b>
	 * <p> Steps: 1. Create Customer
	 * <p> 2. Create active policy.
	 * <p> 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * <p> 4. Do AP Endorsement at R-10.
	 * <p> 5. Move time to R and run status update job.
	 * <p> 6. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * <p> 7. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * <p> 8. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * <p> 9. Change status to Proposed.
	 * <p> 10. Make payment more than earned premium write off.
	 * <p> 11. Verify that earned premium is fully reversed.
	 *
	 */
	@Override
	@Parameters({STATE_PARAM})
	@StateList(states = States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffMoreProposed(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffMoreProposed(state);
	}

	/**
	 * <b> Test Earned premium write off generation </b>
	 * <p> Steps: 1. Create Customer
	 * <p> 2. Create active policy.
	 * <p> 3. Go to R-45/R-35/R-20 and run all needed renewal jobs and renewal notice job
	 * <p> 4. Move time to R and run status update job.
	 * <p> 5. Move time to R+10 and run policyLapsedRenewalProcessAsyncJob
	 * <p> 6. Move time to R+15/R+30/R+45 and run AAACollectionCancellDebtBatchAsyncJob
	 * <p> 7. Move time to R+60 and run earnedpremiumWriteoffprocessingjob
	 * <p> 8. Earned premium write off is absent on policy
	 *
	 */
	@Override
	@Parameters({STATE_PARAM})
	@StateList(states = States.CA)
	@Test(groups = {Groups.FUNCTIONAL, Groups.TIMEPOINT, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.DocumentFulfillment.AUTO_CA_SELECT, testCaseId = {"PAS-11697"})
	public void pas11697_testEarnedPremiumWriteOffNoAP(@Optional("CA") String state)  {
		super.pas11697_testEarnedPremiumWriteOffNoAP(state);
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}
}
