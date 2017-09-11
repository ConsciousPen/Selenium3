package aaa.modules.docgen.auto_ss;

import java.time.LocalDateTime;

import org.mortbay.log.Log;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.billing.BillingInstallmentsScheduleVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.DocGenEnum.Documents;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

public class TestAZScenario2 extends AutoSSBaseTest{

	protected String policyNumber;
	protected LocalDateTime policyExpirationDate;

	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.AUTO_SS)
	public void TC01_CreatePolicy() {
		mainApp().open();
		createCustomerIndividual();
		TestData tdpolicy = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
    	createPolicy(tdpolicy);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policyExpirationDate=PolicySummaryPage.getExpirationDate();
		log.info("Original Policy #" + policyNumber);
//		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
//		String dueAmount1= BillingSummaryPage.getInstallmentAmount(2).toString();
//		String dueAmount2= BillingSummaryPage.getInstallmentAmount(3).toString();
//		String dueAmount3= BillingSummaryPage.getInstallmentAmount(4).toString();
//		String dueAmount4= BillingSummaryPage.getInstallmentAmount(5).toString();
//		String dueAmount5= BillingSummaryPage.getInstallmentAmount(6).toString();
//		String dueAmount6= BillingSummaryPage.getInstallmentAmount(7).toString();
//		String dueAmount7= BillingSummaryPage.getInstallmentAmount(8).toString();
//		String dueAmount8= BillingSummaryPage.getInstallmentAmount(9).toString();
//		String dueAmount9= BillingSummaryPage.getInstallmentAmount(10).toString();
//		String dueAmount10= BillingSummaryPage.getInstallmentAmount(11).toString();	
//		TODO vereify the xml file 
//		AH35XX
//		AA02AZ
//		AA10XX
//		AA43AZ
//		AA52AZ
//		AA59XX
//		AAGCAZ
//		AARFIXX
//		AASR22
//		AHNBXX
		DocGenHelper.verifyDocumentsGenerated(policyNumber, Documents.AA43AZ).verify.mapping(getTestSpecificTD("TestData_Verification").adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum","TextField"), policyNumber));
	 }
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_EndorsePolicy(){
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTd = getTestSpecificTD("TestData_Endorsement");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
	}
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC03_RenewalImageGeneration(){
		LocalDateTime renewImageGenDate=getTimePoints().getRenewImageGenerationDate(policyExpirationDate);	
		Log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);	
	}
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
    public void TC04_RenewaPreviewGeneration(){
		
		LocalDateTime renewPreviewGenDate=getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Preview Generation Date" + renewPreviewGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
	    Tab.buttonOk.click();
		// Create new renewal version
	    if (Page.dialogConfirmation.isPresent() && Page.dialogConfirmation.isVisible()) {
			  Page.dialogConfirmation.confirm();
		    }
	    policy.getDefaultView().fill(getTestSpecificTD("TestData_AddRenewal"));
	    PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
		
	}
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC05_RenewaOfferGeneration(){
		LocalDateTime renewOfferGenDate=getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Generation Date" + renewOfferGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
		
//		TODO verify the documents AA02,AHAUXX,AA10XX,AHPNXX
	}
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC06_RenewaOfferBillGeneration(){
		LocalDateTime renewOfferBillGenDate=getTimePoints().getBillGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
//		TODO verify the xml file AHREXX, AH35XX
	}
}
