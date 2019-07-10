package aaa.modules.regression.sales.template.functional;

import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_BILL;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER;
import static aaa.helpers.docgen.AaaDocGenEntityQueries.GET_DOCUMENT_RECORD_COUNT_BY_EVENT_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.BatchJob;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho3.TestPolicyPaymentPlansAndDownpayments;
import toolkit.datax.TestData;
import toolkit.db.DBService;

public class TestRenewalBillDiscardAndMsgOnPaymentPlanChangeTemplate extends PolicyBaseTest {

	private LocalDateTime policyExpirationDate;

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BillingAccount billingAccount = new BillingAccount();
	private MortgageesTab mortgageesTab = new MortgageesTab();
	private BindTab bindTab = new BindTab();

	//Messages info is in 'PAS-16401'
	public String notAutomaticPaymentMessage = "As you requested, we have changed your payment plan from EXISTINGPAYMENTPLAN to CHANGEDPAYMENTPLAN "
			+ "and your minimum due has changed. You will need to pay the updated minimum amount to renew your policy. An updated renewal "
			+ "statement will not be available. Do you agree to these changes?";

	public String automaticPaymentMessage = "As you requested, we have changed your payment plan from EXISTINGPAYMENTPLAN to CHANGEDPAYMENTPLAN "
			+ "and your minimum due has changed. Your policy is set up on automatic payment and the new minimum due will be withdrawn from your "
			+ "account on or after your renewal date. An updated renewal statement will not be available. Do you agree to these changes?";

	public String billToInsuredToBillToMortgageeMessage = "As you requested, we have changed your payment plan from EXISTINGPAYMENTPLAN to bill to mortgagee "
			+ "and payment will be requested from your mortgage company, MortgageeCompany. If your mortgage company does not make the renewal "
			+ "payment, you will receive notification and be responsible for the payment. Do you agree to these changes?";

	public String billToMortgageeToBillToInsuredMessage = "As you requested, we have changed your payment plan from bill to mortgagee to CHANGEDPAYMENTPLAN "
			+ "and your minimum due has changed. You will need to pay the updated minimum amount to renew your policy. An updated renewal "
			+ "statement will not be available. If a payment is received from your mortgage company it will be applied to your policy. "
			+ "Do you agree to these changes?";

	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange(String initialPaymentPlan, Boolean isOnAutopay, Boolean generateBillManually, String renewalPaymentPlan, String message, String initialPaymentPlanInRenewal) {
		String policyNumber = createPolicy(initialPaymentPlan, isOnAutopay);
		createProposedRenewal();

		if (generateBillManually) {
			generatePaperBillManually(policyNumber);
		} else {
			generatePaperBillViaJob();
			checkThatPaperBillIsGeneratedInDB(policyNumber);
		}

		navigateToRenewal(policyNumber);
		changePaymentPlanOnRenewal(renewalPaymentPlan);
		checkMessageInBindTab(message, initialPaymentPlanInRenewal, renewalPaymentPlan);
		checkNewBillIsGeneratedOnRenewal();
		checkDocGenIsNotTriggered(policyNumber, RENEWAL_OFFER, DocGenEnum.Documents.AHRBXX.getIdInXml());
	}

	private String createPolicy(String paymentPlan, Boolean isOnAutoPay) {
		TestData policyTd =  getPolicyTD();
		policyTd = policyTd.adjust(TestData.makeKeyPath(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
				HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), paymentPlan);

		if (isOnAutoPay) {
			policyTd.adjust(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()), "@home_ss_ho3@DataGather@PurchaseTab_WithAutopay");
		}

		if (paymentPlan == BillingConstants.PaymentPlan.MORTGAGEE_BILL) {
			TestData mortgageesTabTd = testDataManager.getDefault(TestPolicyPaymentPlansAndDownpayments.class).getTestData("TestData");
			policyTd = policyTd.adjust(mortgageesTabTd).resolveLinks();
		}

		mainApp().open();
		createCustomerIndividual();
		return createPolicy(policyTd);
	}

	private void createProposedRenewal() {
		//Move time to R-35
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));

		//Create Proposed Renewal
		JobUtils.executeJob(BatchJob.renewalOfferGenerationPart2);
	}

	private void generatePaperBillViaJob() {
		//Move time to R-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));//-35 days

		//Generate Renewal bill
		JobUtils.executeJob(BatchJob.aaaRenewalNoticeBillAsyncJob);
	}

	private void generatePaperBillManually(String policyNumber) {
		//Move time to R-20
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getBillGenerationDate(policyExpirationDate));//-20 days

		//Generate Renewal bill
		mainApp().open();
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
		assertThat(Integer.parseInt(DBService.get().getValue(numberOfDocumentsRecordsInDbQuery).get())).isEqualTo(1);

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
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		if (paymentPlan == BillingConstants.PaymentPlan.MORTGAGEE_BILL_RENEWAL) {
			premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.BILL_TO_AT_RENEWAL).setValue("Mortgagee");
			premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue(BillingConstants.PaymentPlan.MORTGAGEE_BILL_RENEWAL);
			premiumsAndCoveragesQuoteTab.submitTab();
			mortgageesTab.getAssetList().getAsset(HomeSSMetaData.MortgageesTab.MORTGAGEE).setValue("Yes");
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.NAME).setValue("MortgageeCompany");
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.ZIP_CODE)
					.setValue(getCustomerIndividualTD("DataGather", "GeneralTab_" + getState()).getValue("Zip Code"));
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.STREET_ADDRESS_1)
					.setValue(getCustomerIndividualTD("DataGather", "GeneralTab_" + getState()).getValue("Address Line 1"));
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.VALIDATE_ADDRESS_BTN).click();
			mortgageesTab.getValidateAddressDialogAssetList().getAsset(DialogsMetaData.AddressValidationMetaData.BTN_OK).click();
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeSSMetaData.MortgageesTab.MortgageeInformation.LOAN_NUMBER).setValue("12345678");
		} else {
			if (getPolicyType() != PolicyType.HOME_SS_HO4 && getPolicyType() != PolicyType.HOME_SS_HO6) {
				premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.BILL_TO_AT_RENEWAL).setValue("Insured");
			}
			premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue(paymentPlan);
		}
		premiumsAndCoveragesQuoteTab.calculatePremium();
	}

	private void checkMessageInBindTab(String message, String existinPaymentPlan, String changedPaymentPlan) {
		//Navigate to Bind tab
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());

		//Check New Message is shown in popup
		bindTab.btnPurchase.click();
		message = message.replace("EXISTINGPAYMENTPLAN", existinPaymentPlan).replace("CHANGEDPAYMENTPLAN", changedPaymentPlan);
		assertThat(bindTab.confirmEndorsementPurchase.labelMessage.getValue()).isEqualTo(message);

		bindTab.confirmEndorsementPurchase.confirm();
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
