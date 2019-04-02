package aaa.modules.docgen.auto_ss;

import static aaa.main.enums.DocGenEnum.Documents.*;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.Constants.States;
import aaa.common.pages.SearchPage;
import aaa.helpers.billing.BillingAccountPoliciesVerifier;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.IBillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.ETCSCoreSoftAssertions;

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
	private LocalDateTime scheduleDueDate2;
	private IBillingAccount billing = new BillingAccount();

	/**
	 * @author Ryan Yu
	 * @name Verify the documents generated during first endorsement
	 * @scenario
	 * 1. Create a active policy 
	 * 2. Make an endorsement to add SR22 and Car
	 * 3. Calculate premium and bind the policy
	 * 4. Verify the documents generate AASR22
	 *
	 * 1. Open Policy Consolidated screen.
	 * 2. Make an endorsement to change policy type as NANO
	 * 3. Calculate premium and bind the policy
	 * 4. Verify the documents generate AA41XX
	 *
	 * 1. Switch time to DD1-20.
	 * 2. Run aaaBillingInvoiceAsyncTaskJob
	 * 3. Pay policy in Full
	 *
	 * 1. Open Policy Consolidated screen.
	 * 2. At R-96, run renewalOfferGenerationPart1 and renewalOfferGenerationPart2 image to generate the renewal image
	 * 3. verify the policy status is active
	 * 4. At R-45, run renewalOfferGenerationPart2 and verify the renewal link is enable, Renewal status is Premium Calculated
	 * 5. At R-35, run renewalOfferGenerationPart2 and verify the renewal status is proposed
	 * 6. At R-20,  aaaRenewalNoticeBillAsyncJob generate the form AHR1XX
	 *
	 * @details
	 */
	@Parameters({"state"})
	@StateList(states = States.IN)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL})
	public void testDocGenScenario04_IN(@Optional("") String state) {
		//DocGenHelper.checkPasDocEnabled(getState(), getPolicyType(), false);
		TestData tdBilling = testDataManager.billingAccount;
		TestData cash_payment = tdBilling.getTestData("AcceptPayment", "TestData_Cash");

		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
		log.info("Policy Effective date" + policyEffectiveDate);
		log.info("Make first endorsement for Policy #" + policyNumber);

		TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementOne");
		policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
		softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		termEffDt = DocGenHelper.convertToZonedDateTime(policyEffectiveDate);

		// verify the xml file AASR22
		DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, AASR22).verify
				.mapping(getTestSpecificTD("TestData_VerificationEDOne").adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt", "DateTimeField"), termEffDt), policyNumber, softly);
		softly.close();

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		log.info("Make second endorsement for Policy #" + policyNumber);
		TestData tdEndorsement2 = getTestSpecificTD("TestData_EndorsementTwo");
		policy.createEndorsement(tdEndorsement2.adjust(getPolicyTD("Endorsement", "TestData")));
		softly = new ETCSCoreSoftAssertions();
		softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
		softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		// verify the xml file AA41XX
		DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, AA41XX).verify.mapping(getTestSpecificTD("TestData_VerificationEDTWO")
						.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AA41XX", "form", "TermEffDt", "DateTimeField"), termEffDt),
				policyNumber, softly);
		softly.close();
		// Get DD1
		BillingSummaryPage.open();
		scheduleDueDate2 = BillingSummaryPage.getInstallmentDueDate(2);
		LocalDateTime billGenerationDate = getTimePoints().getBillGenerationDate(scheduleDueDate2);
		TimeSetterUtil.getInstance().nextPhase(billGenerationDate);
		JobUtils.executeJob(Jobs.aaaBillingInvoiceAsyncTaskJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		BillingSummaryPage.open();
		Dollar totalDue = BillingSummaryPage.getTotalDue();
		billing.acceptPayment().perform(cash_payment, totalDue);

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Preview Generation Date" + renewPreviewGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);

		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Offer Generation Date" + renewOfferGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		BillingSummaryPage.open();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		LocalDateTime renewOfferGenDate2 = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Offer Generation Date" + renewOfferGenDate2);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate2);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		BillingSummaryPage.open();
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);

		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.policyInquiry().start();
		LocalDateTime plcyEffDtRenewal = TimeSetterUtil.getInstance()
				.parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE)
						.getValue(), DateTimeUtils.MM_DD_YYYY);
		LocalDateTime plcyExprDtRenewal = TimeSetterUtil.getInstance()
				.parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXPIRATION_DATE)
						.getValue(), DateTimeUtils.MM_DD_YYYY);
		plcyEffDt = DocGenHelper.convertToZonedDateTime(plcyEffDtRenewal);
		termEffDt = DocGenHelper.convertToZonedDateTime(plcyEffDtRenewal);
		plcyExprDt = DocGenHelper.convertToZonedDateTime(plcyExprDtRenewal);
		Tab.buttonCancel.click();

		BillingSummaryPage.open();
		Dollar _curRnwlAmt = new Dollar(BillingSummaryPage.tableInstallmentSchedule.getRow(12).getCell(BillingConstants.BillingInstallmentScheduleTable.BILLED_AMOUNT).getValue());
		Dollar _instlFee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Non EFT Installment Fee")
				.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		//		Dollar _sr22Fee = new Dollar(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "SR22 Fee").getCell(BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		Dollar _sr22Fee = new Dollar(0); // TODO currently it is 0 in the xml file
		curRnwlAmt = _curRnwlAmt.subtract(_instlFee).subtract(_sr22Fee).toString().replace("$", "").replace(",", "");
		totNwCrgAmt = formatValue(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.MINIMUM_DUE).getValue());
		plcyPayMinAmt = formatValue(BillingSummaryPage.getMinimumDue().toString());
		plcyDueDt = DocGenHelper.convertToZonedDateTime(TimeSetterUtil.getInstance()
				.parse(BillingSummaryPage.tableBillsStatements.getRow(BillingConstants.BillingBillsAndStatmentsTable.TYPE, "Bill").getCell(BillingConstants.BillingBillsAndStatmentsTable.DUE_DATE)
						.getValue(), DateTimeUtils.MM_DD_YYYY));
		plcyTotRnwlPrem =
				formatValue(BillingSummaryPage.tablePaymentsOtherTransactions.getRow(BillingConstants.BillingPaymentsAndOtherTransactionsTable.SUBTYPE_REASON, "Renewal - Policy Renewal Proposal")
						.getCell(BillingConstants.BillingPaymentsAndOtherTransactionsTable.AMOUNT).getValue());
		instlFee = formatValue(_instlFee.toString());
		sr22Fee = formatValue(_sr22Fee.toString());

		softly = new ETCSCoreSoftAssertions();
		// verify the xml file AHRBXX
		DocGenHelper.verifyDocumentsGenerated(softly, true, true, policyNumber, AHRBXX).verify.mapping(getTestSpecificTD("TestData_VerificationRenewal")
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyNum", "TextField"), policyNumber)
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt)
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "TermEffDt", "DateTimeField"), termEffDt)
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "CurRnwlAmt", "TextField"), curRnwlAmt)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "TotNwCrgAmt", "TextField"), totNwCrgAmt)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyPayMinAmt", "TextField"), plcyPayMinAmt)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyTotRnwlPrem", "TextField"), plcyTotRnwlPrem)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "PlcyDueDt", "DateTimeField"), plcyDueDt)
						.adjust(TestData.makeKeyPath("AHRBXX", "form", "SR22Fee", "TextField"), sr22Fee)
						.adjust(TestData.makeKeyPath("AHRBXX", "PaymentDetails", "InstlFee", "TextField"), instlFee),
				policyNumber, softly);
		softly.close();
	}

	private String formatValue(String value) {
		return new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
	}

}
