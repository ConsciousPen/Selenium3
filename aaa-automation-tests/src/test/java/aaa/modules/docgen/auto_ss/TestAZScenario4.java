package aaa.modules.docgen.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;

import java.time.LocalDateTime;

import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.exigen.ipb.etcsa.utils.TimeSetterUtil;

import toolkit.datax.TestData;
import toolkit.utils.Dollar;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.webdriver.controls.TextBox;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;

public class TestAZScenario4 extends AutoSSBaseTest{
	protected String policyNumber;
//	protected String policyNumber="AZSS952111102";
	protected String termEffDt;
	protected String plcyEffDt;
	protected String plcyExprDt;
	protected String curRnwlAmt;
	protected String totNwCrgAmt;
	protected String plcyPayMinAmt;
	protected String plcyPayFullAmt;
	protected String plcyDueDt;
	protected String plcyTotRnwlPrem;
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
	@Parameters({"state"})
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	public void TC01_EndorsementOne(@Optional("") String state) {
		mainApp().open();
//		SearchPage.openPolicy(policyNumber);
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectivenDate = PolicySummaryPage.getEffectiveDate();
		Log.info("Policy Effective date" + policyEffectivenDate);
		log.info("Make first endorsement for Policy #" + policyNumber);
		
		TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementOne");
		policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		termEffDt = DocGenHelper.convertToZonedDateTime(policyEffectivenDate);
		
//		verify the xml file AASR22 and AAGCAZ
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR22,AAGCAZ).verify.mapping(getTestSpecificTD("TestData_VerificationEDOne")
				.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt","DateTimeField"), termEffDt)
				.adjust(TestData.makeKeyPath("AAGCAZ", "form", "PlcyNum", "TextField"), policyNumber),
				policyNumber);	
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
	
	@Parameters({"state"})
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL },dependsOnMethods = "TC01_EndorsementOne")
	public void TC02_EndorsementTwo(@Optional("") String state) {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		log.info("Make second endorsement for Policy #" + policyNumber);
		TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementTwo");
		policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
//		verify the xml file AA41XX
		
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AA41XX).verify.mapping(getTestSpecificTD("TestData_VerificationEDTWO")
				.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA41XX", "form", "TermEffDt","DateTimeField"), termEffDt),
				policyNumber);
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
	
	@Parameters({ "state" })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL }, dependsOnMethods = "TC01_EndorsementOne")
	public void TC03_RenewalImageGeneration(@Optional("") String state) {

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL }, dependsOnMethods = "TC01_EndorsementOne")
	public void TC04_RenewaPreviewGeneration(@Optional("") String state) {

		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
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
	
	@Parameters({ "state" })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL }, dependsOnMethods = "TC01_EndorsementOne")
	public void TC05_RenewaOfferGeneration(@Optional("") String state) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
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
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL }, dependsOnMethods = "TC01_EndorsementOne")
	public void TC06_RenewaOfferBillGeneration(@Optional("") String state) {
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();
		LocalDateTime plcyEffDtRenewal = TimeSetterUtil.getInstance().parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(PolicyInformation.EFFECTIVE_DATE.getLabel(), TextBox.class).getValue(), DateTimeUtils.MM_DD_YYYY);
		LocalDateTime plcyExprDtRenewal = TimeSetterUtil.getInstance().parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(PolicyInformation.EXPIRATION_DATE.getLabel(), TextBox.class).getValue(), DateTimeUtils.MM_DD_YYYY);
		plcyEffDt = DocGenHelper.convertToZonedDateTime(plcyEffDtRenewal);
		termEffDt = DocGenHelper.convertToZonedDateTime(plcyEffDtRenewal);
		plcyExprDt = DocGenHelper.convertToZonedDateTime(plcyExprDtRenewal);
		Tab.buttonCancel.click();

		BillingSummaryPage.open();
		curRnwlAmt = formatValue(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		totNwCrgAmt = formatValue(BillingSummaryPage.tableInstallmentSchedule.getRow(12).getCell(BillingInstallmentScheduleTable.BILLED_AMOUNT).getValue());
		plcyPayMinAmt = formatValue(BillingSummaryPage.getMinimumDue().toString());
		plcyDueDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(BillingBillsAndStatmentsTable.TYPE, "Bill").getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));
		plcyPayFullAmt = formatValue(BillingSummaryPage.getTotalDue().toString());
		plcyTotRnwlPrem = formatValue(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Renewal - Policy Renewal Proposal").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		// verify the xml file AHRBXX
		
		DocGenHelper.verifyDocumentsGenerated(true,true,policyNumber, AHRBXX).verify.mapping(getTestSpecificTD("TestData_VerificationRenewal")
				.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyEffDt","DateTimeField"), plcyEffDt)
				.adjust(TestData.makeKeyPath("AHRBXX", "form", "TermEffDt","DateTimeField"), termEffDt)
				.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyExprDt","DateTimeField"), plcyExprDt)
				.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "CurRnwlAmt","TextField"), curRnwlAmt)
				.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "TotNwCrgAmt","TextField"), totNwCrgAmt)
				.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyPayMinAmt","TextField"), plcyPayMinAmt)
				.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyTotRnwlPrem","TextField"), plcyTotRnwlPrem)
				.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyDueDt","DateTimeField"), plcyDueDt),
				policyNumber);

	}
	
	private String formatValue(String value) {
		return  new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
	}
	
}
