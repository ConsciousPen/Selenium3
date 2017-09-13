package aaa.modules.docgen.auto_ss;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import aaa.helpers.ssh.RemoteHelper;
import org.mortbay.log.Log;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import static aaa.main.enums.DocGenEnum.Documents.*;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
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
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();
		TestData tdpolicy = getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks());
		policyNumber = createPolicy(tdpolicy);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		log.info("Original Policy #" + policyNumber);
		BillingSummaryPage.open();

		List<TestData> dueAmount = new ArrayList<>();
		List<TestData> installmentDueDate = new ArrayList<>();
		for (int i = 2; i <= 11; i++) {
			TestData td_dueAmount = DataProviderFactory.dataOf("TextField", BillingSummaryPage.getInstallmentAmount(i).add(2).toString().replace("$", ""));
			TestData td_installmentDueDate = DataProviderFactory.dataOf("DateTimeField", convertToZonedDateTime(BillingSummaryPage.getInstallmentDueDate(i)));
			dueAmount.add(td_dueAmount);
			installmentDueDate.add(td_installmentDueDate);
		}

//		verify the xml file 
//		AH35XX // TODO need to resolve the time zone issue
//		AA02AZ
//		AA10XX
//		AA43AZ
//		AA52AZ
//		AA59XX
//		AAGCAZ
//		AARFIXX
//		AASR22
//		AHNBXX
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AA43AZ, AASR22).verify.mapping(getTestSpecificTD("TestData_Verification")
				.adjust(TestData.makeKeyPath("AA43AZ", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AH35XX", "PaymentDetails", "PlcyTotWdrlAmt"), dueAmount)
				.adjust(TestData.makeKeyPath("AH35XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AH35XX", "form", "FutInstlDueDt"), installmentDueDate)
				.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
				);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	 }

	//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC02_EndorsePolicy(){
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		TestData endorsementTd = getTestSpecificTD("TestData_Endorsement");
		policy.createEndorsement(endorsementTd.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
	}
	
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
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
	
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
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
	
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
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
	
//	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_CreatePolicy")
	public void TC06_RenewaOfferBillGeneration(){
		LocalDateTime renewOfferBillGenDate=getTimePoints().getBillGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
//		TODO verify the xml file AHREXX, AH35XX
	}

	private String convertToZonedDateTime(LocalDateTime installmentDueDate) {
		final String zoneId = RemoteHelper.getServerTimeZone();
		return installmentDueDate.atZone(ZoneId.of(zoneId)).format(DocGenHelper.DATE_TIME_FIELD_FORMAT);
	}
}
