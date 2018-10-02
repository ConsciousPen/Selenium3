package aaa.modules.docgen.auto_ss;

import static toolkit.verification.CustomAssertions.assertThat;
import static aaa.main.enums.DocGenEnum.Documents.*;
import java.time.LocalDateTime;
import org.mortbay.log.Log;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.Constants.States;
import aaa.common.pages.NavigationPage;
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
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.TextBox;

public class TestScenario4 extends AutoSSBaseTest {
	private String policyNumber;
	private String termEffDt;
	private String plcyEffDt;
	private String plcyExprDt;
	private String curRnwlAmt;
	private String totNwCrgAmt;
	private String plcyPayMinAmt;
	private String plcyPayFullAmt;
	private String plcyDueDt;
	private String plcyTotRnwlPrem;
	private String instlFee;
	private String sr22Fee;
	private String cancEffDt;
	private String plcyRnwlExprDt;
	private String rnwlDnPayAmt;
	private LocalDateTime policyExpirationDate;
	private LocalDateTime policyEffectiveDate;

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
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL})
	public void TC01_EndorsementOne(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();
			createCustomerIndividual();
			policyNumber = createPolicy(getPolicyTD().adjust(getTestSpecificTD("TestData").resolveLinks()));
			policyExpirationDate = PolicySummaryPage.getExpirationDate();
			policyEffectiveDate = PolicySummaryPage.getEffectiveDate();
			log.info("Policy Effective date" + policyEffectiveDate);
			log.info("Make first endorsement for Policy #" + policyNumber);

			TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementOne");
			policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);

			termEffDt = DocGenHelper.convertToZonedDateTime(policyEffectiveDate);

			//		verify the xml file AASR22 and AAGCAZ
			//SR22 is tested in Scenario2
			/*
			DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, AASR22).verify.mapping(getTestSpecificTD("TestData_AASR22")
							.adjust(TestData.makeKeyPath("AASR22", "form", "PlcyNum", "TextField"), policyNumber)
							.adjust(TestData.makeKeyPath("AASR22", "form", "TermEffDt", "DateTimeField"), termEffDt),
					policyNumber, softly);*/
			DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, AAGCAZ).verify.mapping(getTestSpecificTD("TestData_VerificationEDOne")
							.adjust(TestData.makeKeyPath("AAGCAZ", "form", "PlcyNum", "TextField"), policyNumber),
					policyNumber, softly);
		});
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
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_EndorsementOne")
	public void TC02_EndorsementTwo(@Optional("") String state) {
		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();
			SearchPage.openPolicy(policyNumber);
			log.info("Make second endorsement for Policy #" + policyNumber);
			TestData tdEndorsement = getTestSpecificTD("TestData_EndorsementTwo");
			policy.createEndorsement(tdEndorsement.adjust(getPolicyTD("Endorsement", "TestData")));
			softly.assertThat(PolicySummaryPage.buttonPendedEndorsement).isDisabled();
			softly.assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
			//		verify the xml file AA41XX

			DocGenHelper.verifyDocumentsGenerated(softly, policyNumber, AA41XX).verify.mapping(getTestSpecificTD("TestData_VerificationEDTWO")
							.adjust(TestData.makeKeyPath("AA41XX", "form", "PlcyNum", "TextField"), policyNumber)
							.adjust(TestData.makeKeyPath("AA41XX", "form", "TermEffDt", "DateTimeField"), termEffDt),
					policyNumber, softly);
		});
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

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_EndorsementOne")
	public void TC03_RenewalImageGeneration(@Optional("") String state) {

		LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Image Generation Date" + renewImageGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		HttpStub.executeAllBatches();
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_EndorsementOne")
	public void TC04_RenewaPreviewGeneration(@Optional("") String state) {

		LocalDateTime renewPreviewGenDate = getTimePoints().getRenewPreviewGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Preview Generation Date" + renewPreviewGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewPreviewGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PREMIUM_CALCULATED).verify(1);
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_EndorsementOne")
	public void TC05_RenewaOfferGeneration(@Optional("") String state) {
		LocalDateTime renewOfferGenDate = getTimePoints().getRenewOfferGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Generation Date" + renewOfferGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferGenDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		assertThat(PolicySummaryPage.buttonRenewals).isEnabled();
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.PROPOSED).verifyRowWithEffectiveDate(policyExpirationDate);
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_EndorsementOne")
	public void TC06_RenewaOfferBillGeneration(@Optional("") String state) {
		LocalDateTime renewOfferBillGenDate = getTimePoints().getBillGenerationDate(policyExpirationDate);
		Log.info("Policy Renewal Offer Bill Generation Date" + renewOfferBillGenDate);
		TimeSetterUtil.getInstance().nextPhase(renewOfferBillGenDate);
		JobUtils.executeJob(Jobs.aaaRenewalNoticeBillAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();
			SearchPage.openPolicy(policyNumber);
			PolicySummaryPage.buttonRenewals.click();
			policy.policyInquiry().start();
			LocalDateTime plcyEffDtRenewal = TimeSetterUtil.getInstance()
					.parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel(), TextBox.class)
							.getValue(), DateTimeUtils.MM_DD_YYYY);
			LocalDateTime plcyExprDtRenewal = TimeSetterUtil.getInstance()
					.parse(policy.getDefaultView().getTab(GeneralTab.class).getPolicyInfoAssetList().getAsset(AutoSSMetaData.GeneralTab.PolicyInformation.EXPIRATION_DATE.getLabel(), TextBox.class)
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
			Dollar _sr22Fee = new Dollar(0);
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
		});
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_EndorsementOne")
	public void TC07_UpdatePolicyStatus(@Optional("") String state) {
		LocalDateTime updatePolicyStatusDate = getTimePoints().getUpdatePolicyStatusDate(policyExpirationDate);
		Log.info("Policy Update Status Date" + updatePolicyStatusDate);
		TimeSetterUtil.getInstance().nextPhase(updatePolicyStatusDate);
		JobUtils.executeJob(Jobs.policyStatusUpdateJob);

		mainApp().open();
		SearchPage.openBilling(policyNumber);
		new BillingAccountPoliciesVerifier().setPolicyStatus(ProductConstants.PolicyStatus.POLICY_EXPIRED).verifyRowWithEffectiveDate(policyEffectiveDate);
	}

	@Parameters({"state"})
	@StateList(states = States.AZ)
	@Test(groups = {Groups.DOCGEN, Groups.TIMEPOINT, Groups.CRITICAL}, dependsOnMethods = "TC01_EndorsementOne")
	public void TC08_InsuranceRenewalReminder(@Optional("") String state) {
		LocalDateTime insuranceRenewalReminderDate = getTimePoints().getInsuranceRenewalReminderDate(policyExpirationDate);
		Log.info("Policy Insurance Renewal Reminder Notice Date" + insuranceRenewalReminderDate);
		TimeSetterUtil.getInstance().nextPhase(insuranceRenewalReminderDate);
		JobUtils.executeJob(Jobs.lapsedRenewalProcessJob);
		JobUtils.executeJob(Jobs.aaaRenewalReminderGenerationAsyncJob);
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);

		CustomSoftAssertions.assertSoftly(softly -> {
			mainApp().open();
			SearchPage.openBilling(policyNumber);
			plcyEffDt = DocGenHelper.convertToZonedDateTime(policyEffectiveDate);
			plcyExprDt = DocGenHelper.convertToZonedDateTime(policyExpirationDate);
			cancEffDt = DocGenHelper.convertToZonedDateTime(policyExpirationDate.plusDays(20));
			plcyRnwlExprDt = DocGenHelper.convertToZonedDateTime(policyExpirationDate.plusDays(20));
			rnwlDnPayAmt = formatValue(BillingSummaryPage.getMinimumDue().toString());
			plcyPayFullAmt = formatValue(BillingSummaryPage.getTotalDue().toString());

			//		Verify the xml for AH64XX
			DocGenHelper.verifyDocumentsGenerated(softly, true, true, policyNumber, AH64XX).verify.mapping(getTestSpecificTD("TestData_AH64XX")
							//		        .adjust(TestData.makeKeyPath("AH64XX", "form", "PlcyNum", "TextField"), policyNumber)  //TODO the field is absent, defect 44754
							//				.adjust(TestData.makeKeyPath("AH64XX", "form", "PlcyEffDt", "DateTimeField"), plcyEffDt) //TODO the field is absent, defect 44754
							//				.adjust(TestData.makeKeyPath("AH64XX", "form", "PlcyExprDt", "DateTimeField"), plcyExprDt) //TODO the field is incorrect, defect 44754
							.adjust(TestData.makeKeyPath("AH64XX", "form", "CancEffDt", "DateTimeField"), cancEffDt)
							.adjust(TestData.makeKeyPath("AH64XX", "form", "PlcyRnwlExprDt", "DateTimeField"), plcyRnwlExprDt)
							.adjust(TestData.makeKeyPath("AH64XX", "PaymentDetails", "RnwlDnPayAmt", "TextField"), rnwlDnPayAmt)
							.adjust(TestData.makeKeyPath("AH64XX", "PaymentDetails", "PlcyPayFullAmt", "TextField"), plcyPayFullAmt),
					policyNumber, softly);
		});
	}

	private String formatValue(String value) {
		return new Dollar(value.replace("\n", "")).toString().replace("$", "").replace(",", "");
	}

}
