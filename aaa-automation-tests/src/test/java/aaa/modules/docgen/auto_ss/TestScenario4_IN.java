package aaa.modules.docgen.auto_ss;

import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants.BillingBillsAndStatmentsTable;
import aaa.main.enums.BillingConstants.BillingInstallmentScheduleTable;
import aaa.main.enums.BillingConstants.BillingPaymentsAndOtherTransactionsTable;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.ProductConstants.PolicyStatus;
import aaa.main.metadata.policy.AutoSSMetaData.GeneralTab.PolicyInformation;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.TextBox;

import java.time.LocalDateTime;

import static aaa.main.enums.DocGenEnum.Documents.*;

public class TestScenario4_IN extends AutoSSBaseTest {
	private String policyNumber;
	private String termEffDt;
	private String plcyEffDt;
	private String plcyExprDt;
	private String curRnwlAmt;
	private String totNwCrgAmt;
	private String plcyPayMinAmt;
	private String plcyDueDt;
	private String plcyTotRnwlPrem;
	private String instlFee;
	private String sr22Fee;
	private LocalDateTime policyExpirationDate;
	private LocalDateTime policyEffectiveDate;
	private LocalDateTime scheduleDueDate2 = LocalDateTime.now().plusMonths(1);
	private IBillingAccount billing = new BillingAccount();
	private TestData tdBilling = testDataManager.billingAccount;
	private TestData cash_payment = tdBilling.getTestData("AcceptPayment", "TestData_Cash");
	
	/**
	 * @author Ryan Yu
	 * @name Verify the documents generated during first endorsement
	 * @scenario 
	 * 1. Create a active policy 
	 * 2. Make an endorsement to add SR22 and Car
	 * 3. Calculate premium and bind the policy
	 * 4. Verify the documents generate AASR22
	 * @details
	 */	
	@Parameters({"state"})
	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL })
	public void TC01_EndorsementOne(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		log.info("Policy Effective date" + policyEffectiveDate);
		log.info("Make first endorsement for Policy #" + policyNumber);
		
		TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementOne");
		policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		
		termEffDt = DocGenHelper.convertToZonedDateTime(policyEffectiveDate);
		
		// verify the xml file AASR22
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AASR22).verify.mapping(getTestSpecificTD("TestData_VerificationEDOne").adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber).adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt", "DateTimeField"), termEffDt), policyNumber);
			
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	 }
	
	/**
	 * @author Ryan Yu
	 * @name Verify the documents generated during second endorsement
	 * @scenario 
	 *  
	 * 1. Open Policy Consolidated screen.
	 * 2. Make an endorsement to change policy type as NANO
	 * 3. Calculate premium and bind the policy
	 * 4. Verify the documents generate AA41XX
	 * @details
	 */	
	@Parameters({"state"})
    @Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL },dependsOnMethods = "TC01_EndorsementOne")
	public void TC02_EndorsementTwo(@Optional("") String state) {
		CustomAssert.enableSoftMode();
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		log.info("Make second endorsement for Policy #" + policyNumber);
		TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementTwo");
		policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
		PolicySummaryPage.buttonPendedEndorsement.verify.enabled(false);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		// verify the xml file AA41XX
		DocGenHelper.verifyDocumentsGenerated(policyNumber, AA41XX).verify.mapping(getTestSpecificTD("TestData_VerificationEDTWO")
				.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber)
				.adjust(TestData.makeKeyPath("AA41XX", "form", "TermEffDt","DateTimeField"), termEffDt),
				policyNumber);
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
		
		// Get DD1
		BillingSummaryPage.open();
		scheduleDueDate2 = BillingSummaryPage.getInstallmentDueDate(2);
	}
	
	/**
	 * @author Ryan Yu
	 * @scenario 
	 * 1. Switch time to DD1-20.
	 * 2. Run aaaBillingInvoiceAsyncTaskJob
	 * 3. Pay policy in Full
	 */	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL } ,dependsOnMethods = "TC01_EndorsementOne")
	public void TC03_PayPolicyInFull(@Optional("") String state) {
		LocalDateTime billGenerationDate = getTimePoints().getBillGenerationDate(scheduleDueDate2);
		TimeSetterUtil.getInstance().nextPhase(billGenerationDate);
		JobUtils.executeJob(Jobs.billingInvoiceAsyncTaskJob, true);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		BillingSummaryPage.open();
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billing.acceptPayment().perform(cash_payment, totalDue);
	}
	
	/**
	 * @author Ryan Yu
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
	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL }, dependsOnMethods = "TC01_EndorsementOne")
	public void TC04_RenewalImageGeneration(@Optional("") String state) {
		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1, true);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2, true);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL }, dependsOnMethods = "TC01_EndorsementOne")
	public void TC05_RenewaPreviewGeneration(@Optional("") String state) {
		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Preview Generation Date" + renewPreviewGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2, true);

		CustomAssert.enableSoftMode();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PREMIUM_CALCULATED).verify(1);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL }, dependsOnMethods = "TC01_EndorsementOne")
	public void TC06_RenewaOfferGeneration(@Optional("") String state) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Generation Date" + renewOfferGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2, true);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);

		CustomAssert.enableSoftMode();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		PolicySummaryPage.buttonRenewals.verify.enabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(PolicyStatus.PROPOSED).verify(1);
		BillingSummaryPage.open();
		new BillingAccountPoliciesVerifier().setPolicyStatus(PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
	
	@Parameters({ "state" })
	@Test(groups = { Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL }, dependsOnMethods = "TC01_EndorsementOne")
	public void TC07_RenewaOfferBillGeneration(@Optional("") String state) {
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob, true);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob, true);

		CustomAssert.enableSoftMode();

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
		Dollar _curRnwlAmt = new Dollar(BillingSummaryPage.tableInstallmentSchedule.getRow(12).getCell(BillingInstallmentScheduleTable.BILLED_AMOUNT).getValue());
		Dollar _instlFee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Non EFT Installment Fee").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
//		Dollar _sr22Fee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "SR22 Fee").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		Dollar _sr22Fee = new Dollar(0); // TODO currently it is 0 in the xml file
		curRnwlAmt = _curRnwlAmt.subtract(_instlFee).subtract(_sr22Fee).toString().replace("$", "").replace(",", "");
		totNwCrgAmt = formatValue(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		plcyPayMinAmt = formatValue(BillingSummaryPage.getMinimumDue().toString());
		plcyDueDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance().parse(BillingSummaryPage.tableBillsStatements.getRow(BillingBillsAndStatmentsTable.TYPE, "Bill").getCell(BillingBillsAndStatmentsTable.DUE_DATE).getValue(), DateTimeUtils.MM_DD_YYYY));
		plcyTotRnwlPrem = formatValue(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Renewal - Policy Renewal Proposal").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		instlFee = formatValue(_instlFee.toString());
		sr22Fee = formatValue(_sr22Fee.toString());
		
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
				.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyDueDt","DateTimeField"), plcyDueDt)
				.adjust(TestData.makeKeyPath("AHRBXX", "form", "SR22Fee","TextField"), sr22Fee)
				.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "InstlFee","TextField"), instlFee),
				policyNumber);
		
		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}	
	
	private String formatValue(String value) {
		return new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
	}
	
}
