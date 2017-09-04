package aaa.modules.docgen.auto_ss;

import java.time.LocalDateTime;

import org.mortbay.log.Log;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

public class TestAZScenario4 extends AutoSSBaseTest{
	protected String policyNumber;
	protected LocalDateTime policyExpirationDate;
	protected LocalDateTime policyEffectivenDate;
	
	/**
	 * @author Lina Li
	 * @name Verify the documents generated during first endorsement
	 * @scenario 
	 * 1. Create a active policy 
	 * 2. Make an endorsement to add SR22 and Golf Car
	 * 3. Calculate premium and bind the policy
	 * 4. Verify the documents generate AASR22 and AAGCAZ
	 * @details
	 */	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void TC01_EndorsementOne() {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectivenDate = PolicySummaryPage.getEffectiveDate();
		Log.info("Policy Effective date" + policyEffectivenDate);
		log.info("Make first endorsement for Policy #" + policyNumber);
		
		TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementOne");
		policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
//		TODO verify the xml file AASR22 and AAGCAZ
	 }
	
	/**
	 * @author Lina Li
	 * @name Verify the documents generated during second endorsement
	 * @scenario 
	 *  
	 * 1. Open Policy Consolidated screen.
	 * 2. Make an endorsement to change policy type as NANO
	 * 3. Calculate premium and bind the policy
	 * 4. Verify the documents generate AA41AZ
	 * @details
	 */	
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void TC02_EndorsementTwo(){
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		log.info("Make second endorsement for Policy #" + policyNumber);
		TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementTwo");
		policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
//		TODO verify the xml file AA41AZ
	}
	
	/**
	 * @author Lina Li
	 * @name Verify the documents generated during renew policy
	 * @scenario 
	 * 1. Open Policy Consolidated screen.
	 * 2. At R-96, run renewalOfferGenerationPart1 and renewalOfferGenerationPart2 image to generate the renewal image 
	 * 3. verify the policy status is active
	 * 4. At R-45, run renewalOfferGenerationPart2 and verify the renewal link is enable, Renewal status is Premium Calculated
	 * 5. At R-35, run renewalOfferGenerationPart2 and verify the renewal status is proposed
	 * 6. At R-20,  aaaRenewalNoticeBillAsyncJob generate the form AHR1XX
	 * @details
	 */	
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
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
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
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
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);
			
	}
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void TC05_RenewaOfferGeneration(){
		LocalDateTime renewOfferGenDate=getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Generation Date" + renewOfferGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);		
	}
	
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void TC06_RenewaOfferBillGeneration(){
		LocalDateTime renewOfferBillGenDate=getTimePoints().getBillGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
//		TODO verify the xml file AHR1XX	
	}
	
}
