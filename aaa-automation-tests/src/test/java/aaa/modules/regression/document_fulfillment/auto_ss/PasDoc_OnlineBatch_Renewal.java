package aaa.modules.regression.document_fulfillment.auto_ss;

import java.time.LocalDateTime;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingBillsAndStatementsVerifier;
import aaa.helpers.billing.BillingHelper;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum.EventName;
import static aaa.main.enums.DocGenEnum.Documents.*;

public class PasDoc_OnlineBatch_Renewal extends AutoSSBaseTest{
	
	protected BillingAccount billingAccount = new BillingAccount();
	protected TestData tdBilling = testDataManager.billingAccount;
	
	/**
	 * OnlineBatch - Scenario 48 - PRE-RENEWAL_REMINDER_NOTICE: AHRRXX
	 * Steps:
	 * 		1. Policy is issued. 
	 * 		2. Shift time to R-35 Execute Renewal_Offer_Generation_Part1 and Part2. 
	 * 		3. Shift time to R-10 Execute preRenewalReminderGenerationAsyncJob. 
	 * Expected result: 
	 * 		The following form is generated: AHRRXX
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario48(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();	
		String policyNumber = createPolicy();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(10));
		JobUtils.executeJob(Jobs.preRenewalReminderGenerationAsyncJob);
		PasDocImpl.verifyDocumentsGenerated(null, true, false, policyNumber, AHRRXX);		
	}
	
	/**
	 * OnlineBatch: Scenario 54 - RENEWAL_BILL: AHRBXX, AH35XX
	 * Precondition: 
	 * 		(a) Policy is issued: Monthly Payment Plan, AutoPay IS active. 
	 * 		(b) Policy is issued: Monthly Payment Plan, AutoPay is NOT active. 
	 * Steps: 
	 * 		1. Set time to R-35 and run Renewal_Offer_Generation_Part1 job and then  Renewal_Offer_Generation_Part2 job. 
	 * 		Result is Renewal in status Proposed. 
	 * 		2. Set time to R-20 and run aaaRenewalNoticeBillAsyncJob to generate a bill.
	 * Expected result: 
	 * 		(a) The following forms are generated: AHRBXX, AH35XX. 
	 * 		(b) AHRBXX form is generated, AH35XX is not generated. 
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario54(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();	
		TestData td_withAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_withAutoPay").resolveLinks());
		TestData td_withoutAutoPay = getPolicyTD().adjust(getTestSpecificTD("TestData_withoutAutoPay").resolveLinks());
		
		String policyWithAutoPay = createPolicy(td_withAutoPay);
		String policyWithoutAutoPay = createPolicy(td_withoutAutoPay);
		
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalStatusIsProposed(policyWithAutoPay, policyEffectiveDate, policyExpirationDate);
		verifyRenewalStatusIsProposed(policyWithoutAutoPay, policyEffectiveDate, policyExpirationDate);
		
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		//mainApp().open();
		//SearchPage.openBilling(policyWithAutoPay);
		CustomSoftAssertions.assertSoftly(softly -> {
			PasDocImpl.verifyDocumentsGenerated(softly, true, false, policyWithAutoPay, EventName.RENEWAL_BILL, AHRBXX, AH35XX);
			PasDocImpl.verifyDocumentsGenerated(softly, true, false, policyWithoutAutoPay, EventName.RENEWAL_BILL, AHRBXX);
			PasDocImpl.verifyDocumentsGenerated(softly, false, false, policyWithoutAutoPay, EventName.RENEWAL_BILL, AH35XX);
		});
	}
	
	/**
	 * OnlineBatch - Scenario 55 - RENEWAL_ISSUE: AASR22
	 * Precondition: 
	 * 		Policy is issued: Driver with Financial Responsibility = Yes. 
	 * Steps: 
	 * 		1. Set time to R-35. 
	 * 		2. Run Renewal_Offer_Generation_Part1 job and then  Renewal_Offer_Generation_Part2 job. 
	 * 		Result is Renewal in status Proposed.
	 * 		3. Set time to R-20 and run aaaRenewalNoticeBillAsyncJob to generate a bill. 
	 * 		4. Pay bill. 
	 * 		5. Set time to R+1 and run policyStatusUpdateJob.
	 * Expected result: 
	 * 		Form AASR22 is NOT generated. 		
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario55(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		TestData td_finDriver = getPolicyTD().adjust(getTestSpecificTD("TestData_FinancialDriver").resolveLinks());
		String policy_finDriver = createPolicy(td_finDriver);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalStatusIsProposed(policy_finDriver, policyEffectiveDate, policyExpirationDate);
		
		LocalDateTime billGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(billGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		mainApp().open();
		SearchPage.openBilling(policy_finDriver);
		new BillingBillsAndStatementsVerifier().setType(BillingConstants.BillsAndStatementsType.BILL).verifyRowWithDueDate(policyExpirationDate);
		Dollar renewalBillAmount = BillingHelper.getPolicyRenewalProposalSum(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate), policy_finDriver);
		billingAccount.acceptPayment().perform(tdBilling.getTestData("AcceptPayment", "TestData_Cash"), renewalBillAmount);
		
		LocalDateTime updateStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(updateStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);
		//JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		PasDocImpl.verifyDocumentsGenerated(null, false, false, policy_finDriver, EventName.RENEWAL_ISSUE, AASR22);
	}
	
	/**
	 * OnlineBatch Scenario 56 - RENEWAL_OFFER: 
	 * AA02AZ + AAAEAZ2, AA10XX, AHAUXX, AHMVCNV, AHPIFNXX, AAAEXX2, AHRBXX
	 * Preconditions: 
	 * 		Policy is issued: 
	 * 		- No Excluded Drivers, 
	 * 		- Uninsured and Underinsured Coverages = recommended, 
	 * 		- No Vehicles enrolled in UBI, 
	 * 		- AutoPay is NOT active. 
	 * Steps: 
	 * 		1. Set time to R-35. 
	 * 		2. Run Renewal_Offer_Generation_Part1 job and then  Renewal_Offer_Generation_Part2 job. 
	 * 		Result is Renewal in status Proposed.
	 * Expected result: 
	 * 		Only following forms are generated: AA10XX, AA02AZ, AAAEAZ2, AHPNXX, AARNXX. 
	 * 		Not generated: AH35XX, AA43AZ, AA52AZ, AADNUBI, AAPNUBI, ACPPNUBI, AAINXX1
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario56(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();	
		String policyNumber = createPolicy();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalStatusIsProposed(policyNumber, policyEffectiveDate, policyExpirationDate);
		
		CustomSoftAssertions.assertSoftly(softly -> {
			PasDocImpl.verifyDocumentsGenerated(softly, true, false, policyNumber, EventName.RENEWAL_OFFER, AA10XX, AA02AZ, AAAEAZ2, AHPNXX, AARNXX);
			PasDocImpl.verifyDocumentsGenerated(softly, false, false, policyNumber, EventName.RENEWAL_OFFER, AH35XX, AA43AZ, AA52AZ, AADNUBI, AAPNUBI, ACPPNUBI, AAINXX1);
		});
	}
	
	/**
	 * OnlineBatch Scenario 58 - RENEWAL_OFFER: AA43AZ
	 * Precondition: 
	 * 		(a) Policy is issued: 
	 * 			- Add Excluded Driver, 
	 * 			- Uninsured and Underinsured Coverages = recommended, 
	 * 			- No Vehicles enrolled in UBI, 
	 * 			- AutoPay is active. 
	 * 		(b) Policy is issued without Excluded Driver. On Endorsement add Excluded Driver. 
	 * Steps: 
	 * 		1. Set time to R-35. 
	 * 		2. Run Renewal_Offer_Generation_Part1 job and then  Renewal_Offer_Generation_Part2 job. 
	 * 		Result is Renewal in status Proposed. 
	 * Expected result: 
	 * 		(a) Not generated: AA43AZ
	 * 		(b) Not generated: AA43AZ
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario58(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();			
		TestData td_excludedDriver = getPolicyTD().adjust(getTestSpecificTD("TestData_ExcludedDriver").resolveLinks());
		String policy1_excludedDriver = createPolicy(td_excludedDriver);
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		
		String policy2_excludedDriver = createPolicy();
		TestData td_addExcludedDriver = getTestSpecificTD("TestData_AddExcludedDriver").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_addExcludedDriver);		
		
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalStatusIsProposed(policy1_excludedDriver, policyEffectiveDate, policyExpirationDate);
		verifyRenewalStatusIsProposed(policy2_excludedDriver, policyEffectiveDate, policyExpirationDate);
		
		CustomSoftAssertions.assertSoftly(softly -> {
			PasDocImpl.verifyDocumentsGenerated(null, false, false, policy1_excludedDriver, EventName.RENEWAL_OFFER, AA43AZ);
			PasDocImpl.verifyDocumentsGenerated(null, false, false, policy2_excludedDriver, EventName.RENEWAL_OFFER, AA43AZ);
		});
	}
	
	/**
	 * OnlineBatch Scenario 59 - RENEWAL_OFFER: AA52AZ
	 * Precondition: 
	 * 		Policy is issued: 
	 * 		- No Excluded Drivers, 
	 * 		- Uninsured and Underinsured Coverages less than recommended, 
	 * 		- No Vehicles enrolled in UBI, 
	 * 		- AutoPay is NOT active. 
	 * Steps: 
	 * 		1. Set time to R-35. 
	 * 		2. Run Renewal_Offer_Generation_Part1 job and then  Renewal_Offer_Generation_Part2 job. 
	 * 		Result is Renewal in status Proposed. 
	 * Expected result: 
	 * 		Not generated: AA52AZ
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario59(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();	
		TestData td_UMandUIMLessThanBI = getPolicyTD().adjust(getTestSpecificTD("TestData_UMandUIMLessThanBI").resolveLinks());
		String policy_UMandUIMLessThanBI = createPolicy(td_UMandUIMLessThanBI);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalStatusIsProposed(policy_UMandUIMLessThanBI, effectiveDate, expirationDate);
		
		PasDocImpl.verifyDocumentsGenerated(null, false, false, policy_UMandUIMLessThanBI, EventName.RENEWAL_OFFER, AA52AZ);
	}
	
	/**
	 * OnlineBatch Scenario 60 - RENEWAL_OFFER: AAPNUBI, ACPPNUBI 
	 * Precondition: 
	 * 		(a) Policy is issued: 
	 * 			- No Excluded Drivers, 
	 * 			- Uninsured and Underinsured Coverages = recommended, 
	 * 			- Vehicle is enrolled in UBI, 
	 * 			- AutoPay is NOT active. 
	 * 		(b) Policy is issued without Vehicle Enrolled in UBI. On Endorsement add Vehicle Enrolled in UBI (has Score). 
	 * Steps: 
	 * 		1. Set time to R-35. 
	 * 		2. Run Renewal_Offer_Generation_Part1 job and then  Renewal_Offer_Generation_Part2 job. 
	 * 		Result is Renewal in status Proposed. 
	 * Expected result: 
	 * 		(a) The following forms are generated: AAPNUBI, ACPPNUBI. Not generated: AADNUBI 
	 * 		(b) The following forms are generated: AAPNUBI, ACPPNUBI. Not generated: AADNUBI
	 * 		
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario60(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();	
		
		TestData td_vehicleEnrolledInUBI = getPolicyTD().adjust(getTestSpecificTD("TestData_VehicleEnrolledInUBI").resolveLinks());
		String policy1_EnrolledInUBI = createPolicy(td_vehicleEnrolledInUBI);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		
		String policy2_EnrolledInUBI = createPolicy();
		TestData td_addEnrolledInUBI = getTestSpecificTD("TestData_AddEnrolledInUBI").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_addEnrolledInUBI);
		if (new ErrorTab().isVisible()) {
			new ErrorTab().overrideAllErrors();
			new ErrorTab().submitTab();
		}
		
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalStatusIsProposed(policy1_EnrolledInUBI, effectiveDate, expirationDate);
		verifyRenewalStatusIsProposed(policy2_EnrolledInUBI, effectiveDate, expirationDate);
		
		CustomSoftAssertions.assertSoftly(softly -> {
			PasDocImpl.verifyDocumentsGenerated(softly, true, false, policy1_EnrolledInUBI, EventName.RENEWAL_OFFER, AAPNUBI, ACPPNUBI);
			PasDocImpl.verifyDocumentsGenerated(softly, false, false, policy1_EnrolledInUBI, EventName.RENEWAL_OFFER, AADNUBI);
			PasDocImpl.verifyDocumentsGenerated(softly, true, false, policy2_EnrolledInUBI, EventName.RENEWAL_OFFER, AAPNUBI, ACPPNUBI);
			PasDocImpl.verifyDocumentsGenerated(softly, false, false, policy2_EnrolledInUBI, EventName.RENEWAL_OFFER, AADNUBI);
		});
	}
	
	/**
	 * OnlineBatch Scenario 61 - RENEWAL_OFFER: AADNUBI
	 * Precondition: 
	 * 		(a) Policy is issued: 
	 * 			- No Excluded Drivers, 
	 * 			- Uninsured and Underinsured Coverages = recommended, 
	 * 			- Vehicle is enrolled in UBI (has NO Score), 
	 * 			- AutoPay is NOT active.
	 * 		(b) Policy is issued without Vehicle Enrolled in UBI. On Endorsement add Vehicle Enrolled in UBI (has NO Score). 
	 * Steps: 
	 * 		1. Set time to R-35. 
	 * 		2. Run Renewal_Offer_Generation_Part1 job and then  Renewal_Offer_Generation_Part2 job. 
	 * 		Result is Renewal in status Proposed.
	 * Expected result: 
	 * 		(a) The following forms are generated: AAPNUBI, ACPPNUBI, AADNUBI. 
	 * 		(b) The following forms are generated: AAPNUBI, ACPPNUBI, AADNUBI. 
	 * 
	 * @param state
	 */
	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.REGRESSION, Groups.HIGH})
	public void testScenario61(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();	
		
		TestData td_vehicleEnrolledInUBI = getPolicyTD().adjust(getTestSpecificTD("TestData_VehicleEnrolledInUBI_NoScore").resolveLinks());
		String policy1_EnrolledInUBI = createPolicy(td_vehicleEnrolledInUBI);
		LocalDateTime expirationDate = PolicySummaryPage.getExpirationDate();
		LocalDateTime effectiveDate = PolicySummaryPage.getEffectiveDate();
		
		String policy2_EnrolledInUBI = createPolicy();
		TestData td_addEnrolledInUBI = getTestSpecificTD("TestData_AddEnrolledInUBI_NoScore").adjust(getPolicyTD("Endorsement", "TestData"));
		policy.endorse().performAndFill(td_addEnrolledInUBI);
		if (new ErrorTab().isVisible()) {
			new ErrorTab().overrideAllErrors();
			new ErrorTab().submitTab();
		}
		
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(expirationDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		mainApp().open();
		verifyRenewalStatusIsProposed(policy1_EnrolledInUBI, effectiveDate, expirationDate);
		verifyRenewalStatusIsProposed(policy2_EnrolledInUBI, effectiveDate, expirationDate);
		
		CustomSoftAssertions.assertSoftly(softly -> {
			PasDocImpl.verifyDocumentsGenerated(softly, true, false, policy1_EnrolledInUBI, EventName.RENEWAL_OFFER, AAPNUBI, ACPPNUBI, AADNUBI);
			PasDocImpl.verifyDocumentsGenerated(softly, true, false, policy2_EnrolledInUBI, EventName.RENEWAL_OFFER, AAPNUBI, ACPPNUBI, AADNUBI);
		});
	}
	
	private void verifyRenewalStatusIsProposed(String policyNum, LocalDateTime effectiveDate, LocalDateTime expirationDate) {
		SearchPage.openBilling(policyNum);
		BillingSummaryPage.showPriorTerms();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.POLICY_ACTIVE).verifyRowWithEffectiveDate(effectiveDate);
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(expirationDate);
		BillingSummaryPage.hidePriorTerms();
	}
}
