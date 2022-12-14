package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_BILL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestRenewalBillDiscardAndMessageOnPaymentPlanChange extends AutoSSBaseTest {

	private LocalDateTime policyExpirationDate;

	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private BillingAccount billingAccount = new BillingAccount();


	//Messages info is in 'PAS-16401'
	private String notAutomaticPaymentMessage = "As you requested, we have changed your payment plan from EXISTINGPAYMENTPLAN to CHANGEDPAYMENTPLAN "
			+ "and your minimum due has changed. You will need to pay the updated minimum amount to renew your policy. An updated renewal "
			+ "statement will not be available. Do you agree to these changes?";

	private String automaticPaymentMessage = "As you requested, we have changed your payment plan from EXISTINGPAYMENTPLAN to CHANGEDPAYMENTPLAN "
			+ "and your minimum due has changed. Your policy is set up on automatic payment and the new minimum due will be withdrawn from your "
			+ "account on or after your renewal date. An updated renewal statement will not be available. Do you agree to these changes?";

	///-----------Payment plan: Quarterly -> Semi-Annual, Not on Automatic Payment, Bill generated via scheduler job --------------
	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Quarterly' payment plan (Not on Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. Move time to R-20
	 * 6. Create Bill via job
	 * 7. Check that Bill was generated in DB
	 * 8. On Renewal change payment plan to 'Semi-Annual'
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * 12. Click Ok
	 * 13. Go to Billing page
	 * 14. Check that original bill is discarded and a new bill is displayed on the Billing tab
	 * 15. Check that another Bill on Renewal is not generated in DB
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16405, PAS-16526")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_QuarterlyToSemiAnnual(@Optional("") String state) {

		String policyNumber = createPolicy(BillingConstants.PaymentPlan.QUARTERLY, false);
		createProposedRenewal();
		generatePaperBillViaJob();
		checkThatPaperBillIsGeneratedInDB(policyNumber);
		navigateToRenewal(policyNumber);
		changePaymentPlanOnRenewal(BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
		checkMessageInBindTab(notAutomaticPaymentMessage, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
		checkNewBillIsGeneratedOnRenewal();
		checkDocGenIsNotTriggered(policyNumber, RENEWAL_OFFER, DocGenEnum.Documents.AHRBXX.getIdInXml());
	}

	///-----------Payment plan: Semi-Annual -> Quarterly, Not on Automatic Payment, Bill generated via scheduler job --------------

	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Semi-Annual' payment plan (Not on Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. Move time to R-20
	 * 6. Create Bill via job
	 * 7. Check that Bill was generated in DB
	 * 8. On Renewal change payment plan to 'Quarterly'
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * 12. Click Ok
	 * 13. Go to Billing page
	 * 14. Check that original bill is discarded and a new bill is displayed on the Billing tab
	 * 15. Check that another Bill on Renewal is not generated in DB
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16405, PAS-16526")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_SemiAnnualToQuarterly(@Optional("") String state) {

		String policyNumber = createPolicy(BillingConstants.PaymentPlan.SEMI_ANNUAL, false);
		createProposedRenewal();
		generatePaperBillViaJob();
		checkThatPaperBillIsGeneratedInDB(policyNumber);
		navigateToRenewal(policyNumber);
		changePaymentPlanOnRenewal(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
		checkMessageInBindTab(notAutomaticPaymentMessage, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
		checkNewBillIsGeneratedOnRenewal();
		checkDocGenIsNotTriggered(policyNumber, RENEWAL_OFFER, DocGenEnum.Documents.AHRBXX.getIdInXml());
	}

	///-----------Payment plan: Quarterly -> Semi-Annual,On Automatic Payment, Bill generated manually --------------
	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Quarterly' payment plan (On Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. Move time to R-20
	 * 6. Navigate to Billing page of the Policy
	 * 7. Generate Paper Bill manually
	 * 8. On Renewal change payment plan to 'Semi-Annual'
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * 12. Click Ok
	 * 13. Go to Billing page
	 * 14. Check that original bill is discarded and a new bill is displayed on the Billing tab
	 * 15. Check that another Bill on Renewal is not generated in DB
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL, Groups.TIMEPOINT}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-16405, PAS-16526")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_AutoPay_ManualBillGeneration(@Optional("") String state) {

		String policyNumber = createPolicy(BillingConstants.PaymentPlan.QUARTERLY, true);
		createProposedRenewal();
		generatePaperBillManually(policyNumber);
		navigateToRenewal(policyNumber);
		changePaymentPlanOnRenewal(BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
		checkMessageInBindTab(automaticPaymentMessage, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
		checkNewBillIsGeneratedOnRenewal();
		checkDocGenIsNotTriggered(policyNumber, RENEWAL_OFFER, DocGenEnum.Documents.AHRBXX.getIdInXml());
	}

	private String createPolicy(String paymentPlan, Boolean isOnAutoPay) {
		TestData policyTd = getPolicyTD().adjust(TestData.makeKeyPath(AutoSSMetaData.PremiumAndCoveragesTab.class.getSimpleName(),
				AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN.getLabel()), paymentPlan);

		if (isOnAutoPay) {
			policyTd.adjust(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()), "@auto_ss@DataGather@PurchaseTab_WithAutopay");
		}

		mainApp().open();
		createCustomerIndividual();
		return createPolicy(policyTd);
	}

	private void createProposedRenewal() {
		//Move time to R-35
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));//-35 days

		//Create Proposed Renewal
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
	}

	private void generatePaperBillViaJob() {
		//Move time to R-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));//-20 days

		//Generate Renewal bill
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
	}

	private void generatePaperBillManually(String policyNumber) {
		//Move time to R-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));//-20 days

		//Generate Renewal bill
		mainApp().reopen();
		SearchPage.openBilling(policyNumber);
		billingAccount.generateFutureStatement().perform();

		//Check that new Bill is generated
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE)
				.getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.BILL);
	}

	/**
	 * Check that paper bill document was generated in DB (RENEWAL BILL event is generated and it has AHRBXX document (paper bill))
	 */
	private void checkThatPaperBillIsGeneratedInDB(String policyNumber) {
		String numberOfDocumentsRecordsInDbQuery = String.format(GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME, policyNumber, "%%", RENEWAL_BILL);
		assertThat(Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get())).as("Renewal Bill (Paper Bill) is not generated.").isEqualTo(1);

		checkDocGenTriggered(policyNumber, RENEWAL_BILL, DocGenEnum.Documents.AHRBXX.getIdInXml());
	}

	private void navigateToRenewal(String policyNumber) {
		mainApp().reopen();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		policy.dataGather().start();
	}

	private void changePaymentPlanOnRenewal(String paymentPlan) {
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.getAssetList().getAsset(AutoSSMetaData.PremiumAndCoveragesTab.PAYMENT_PLAN).setValue(paymentPlan);
		premiumAndCoveragesTab.calculatePremium();
	}

	private void checkMessageInBindTab(String message, String existinPaymentPlan, String changedPaymentPlan) {
		//Navigate to Bind tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DOCUMENTS_AND_BIND.get());

		//Check New Message is shown in popup
		DocumentsAndBindTab.btnPurchase.click();
		message = message.replace("EXISTINGPAYMENTPLAN", existinPaymentPlan).replace("CHANGEDPAYMENTPLAN", changedPaymentPlan);
		assertThat(DocumentsAndBindTab.confirmEndorsementPurchase.labelMessage.getValue()).isEqualTo(message);

		DocumentsAndBindTab.confirmEndorsementPurchase.confirm();
	}

	private void checkNewBillIsGeneratedOnRenewal() {
		//Navigate to billing tab
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.BILLING.get());

		//Check that original bill is discarded and a new bill is displayed on the Billing tab
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(1).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE)
				.getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.BILL);
		assertThat(BillingSummaryPage.tableBillsStatements.getRow(2).getCell(BillingConstants.BillingBillsAndStatmentsTable.TYPE)
				.getValue()).isEqualTo(BillingConstants.BillsAndStatementsType.DISCARDED_BILL);
	}
}
