package aaa.modules.regression.document_fulfillment.auto_ss;

import java.time.LocalDateTime;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.impl.PasDocImpl;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.verification.CustomSoftAssertions;
import aaa.main.enums.DocGenEnum.EventName;
import static aaa.main.enums.DocGenEnum.Documents.*;


public class PasDoc_OnlineBatch_Renewal extends AutoSSBaseTest{
	
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
