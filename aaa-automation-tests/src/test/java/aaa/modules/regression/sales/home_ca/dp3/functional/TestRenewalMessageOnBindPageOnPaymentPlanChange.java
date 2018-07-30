package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.modules.regression.sales.home_ss.ho3.TestPolicyPaymentPlansAndDownpayments;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

import static toolkit.verification.CustomAssertions.assertThat;

@StateList(states = Constants.States.CA)
public class TestRenewalMessageOnBindPageOnPaymentPlanChange extends HomeCaDP3BaseTest {

	private LocalDateTime policyExpirationDate;
	private String policyNumber;

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private BillingAccount billingAccount = new BillingAccount();
	private MortgageesTab mortgageesTab = new MortgageesTab();
	private BindTab bindTab = new BindTab();

	//Messages info is in 'PAS-16401'
	private String notAutomaticPaymentMessage = "As you requested, we have changed your payment plan from EXISTINGPAYMENTPLAN to CHANGEDPAYMENTPLAN "
			+ "and your minimum due has changed. You will need to pay the updated minimum amount to renew your policy. An updated renewal "
			+ "statement will not be available. Do you agree to these changes?";

	private String automaticPaymentMessage = "As you requested, we have changed your payment plan from EXISTINGPAYMENTPLAN to CHANGEDPAYMENTPLAN "
			+ "and your minimum due has changed. Your policy is set up on automatic payment and the new minimum due will be withdrawn from your "
			+ "account on or after your renewal date. An updated renewal statement will not be available. Do you agree to these changes?";

	private String billToInsuredToBillToMortgageeMessage = "As you requested, we have changed your payment plan from EXISTINGPAYMENTPLAN to bill to mortgagee "
			+ "and payment will be requested from your mortgage company, MortgageeCompany. If your mortgage company does not make the renewal "
			+ "payment, you will receive notification and be responsible for the payment. Do you agree to these changes?";

	private String billToMortgageeToBillToInsuredMessage = "As you requested, we have changed your payment plan from bill to mortgagee to CHANGEDPAYMENTPLAN "
			+ "and your minimum due has changed. You will need to pay the updated minimum amount to renew your policy. An updated renewal "
			+ "statement will not be available. If a payment is received from your mortgage company it will be applied to your policy. "
			+ "Do you agree to these changes?";

	///-----------Payment plan: Quarterly -> Semi-Annual, Not on Automatic Payment, Bill generated via scheduler job --------------
	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Quarterly' payment plan (Not on Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. On Renewal change payment plan to 'Semi-Annual'
	 * 6. Go to Bind Page
	 * 7. Click Propose
	 * 8. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-16405, PAS-16526")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_QuarterlyToSemiAnnual(@Optional("") String state) {

		createPolicy(BillingConstants.PaymentPlan.QUARTERLY, false);
		createProposedRenewal();
		navigateToRenewal();
		changePaymentPlanOnRenewal(BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
		checkMessageInBindTab(notAutomaticPaymentMessage, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
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
	 * 5. On Renewal change payment plan to 'Quarterly'
	 * 6. Go to Bind Page
	 * 7. Click Propose
	 * 8. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_SemiAnnualToQuarterly(@Optional("") String state) {

		createPolicy(BillingConstants.PaymentPlan.SEMI_ANNUAL, false);
		createProposedRenewal();
		navigateToRenewal();
		changePaymentPlanOnRenewal(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
		checkMessageInBindTab(notAutomaticPaymentMessage, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
	}

	///-----------Payment plan: Quarterly -> Mortgagee Bill, Not on Automatic Payment, Bill generated via scheduler job --------------

	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Quarterly' payment plan (Not on Automatic Payment)
	 * 3. Move time to R-35
	 * 4. Create Renewal in 'Proposed' status
	 * 5. On Renewal change payment plan to 'Mortgagee Bill'
	 * 6. Go to 'Mortgagee & Additional interests' tab
	 * 7. Set Mortgagee radio button to - Yes
	 * 8. Fill all information
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_QuarterlyToMortgageeBill(@Optional("") String state) {

		createPolicy(BillingConstants.PaymentPlan.QUARTERLY, false);
		createProposedRenewal();
		navigateToRenewal();
		changePaymentPlanOnRenewal(BillingConstants.PaymentPlan.MORTGAGEE_BILL_RENEWAL);
		checkMessageInBindTab(billToInsuredToBillToMortgageeMessage, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, BillingConstants.PaymentPlan.MORTGAGEE_BILL_RENEWAL);
	}

	///-----------Payment plan changed: Mortgagee Bill - >Monthly, Not on Automatic Payment, Bill generated via scheduler job --------------

	/**
	 * @author Rokas Lazdauskas
	 * @name Test Message on Payment plan change during Renewal
	 * @scenario
	 * 1. Create Customer
	 * 2. Create Policy with 'Mortgagee Bill' payment plan (Not on Automatic Payment)
	 * 3. Go to 'Mortgagee & Additional interests' tab
	 * 4. Set Mortgagee radio button to - Yes
	 * 5. Fill all information
	 * 6. Move time to R-35
	 * 7. Check that Bill was generated in DB
	 * 8. On Renewal change payment plan to 'Monthly'
	 * 9. Go to Bind Page
	 * 10. Click Propose
	 * 11. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_MortgageeBillToMonthly(@Optional("") String state) {

		createPolicy(BillingConstants.PaymentPlan.MORTGAGEE_BILL, false);
		createProposedRenewal();
		navigateToRenewal();
		changePaymentPlanOnRenewal(BillingConstants.PaymentPlan.MONTHLY_STANDARD_RENEWAL);
		checkMessageInBindTab(billToMortgageeToBillToInsuredMessage, BillingConstants.PaymentPlan.MORTGAGEE_BILL_RENEWAL, BillingConstants.PaymentPlan.MONTHLY_STANDARD_RENEWAL);
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
	 * 5. On Renewal change payment plan to 'Semi-Annual'
	 * 6. Go to Bind Page
	 * 7. Click Propose
	 * 8. Check that popup shown up with the specific message
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL}, description = "Display message when changing payment plans")
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-16405, PAS-16526, PAS-16883")
	public void testRenewalBillDiscardAndMessageOnPaymentPlanChange_AutoPay_ManualBillGeneration(@Optional("") String state) {

		createPolicy(BillingConstants.PaymentPlan.QUARTERLY, true);
		createProposedRenewal();
		navigateToRenewal();
		changePaymentPlanOnRenewal(BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
		checkMessageInBindTab(automaticPaymentMessage, BillingConstants.PaymentPlan.QUARTERLY_RENEWAL, BillingConstants.PaymentPlan.SEMI_ANNUAL_RENEWAL);
	}

	private void createPolicy(String paymentPlan, Boolean isOnAutoPay) {
		TestData policyTd = getPolicyTD().adjust(TestData.makeKeyPath(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
				HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel()), paymentPlan);

		if (isOnAutoPay) {
			policyTd.adjust(TestData.makeKeyPath(PurchaseMetaData.PurchaseTab.class.getSimpleName()), "@home_ca_ho3@DataGather@PurchaseTab_WithAutopay");
		}

		if (paymentPlan == BillingConstants.PaymentPlan.MORTGAGEE_BILL) {
			TestData mortgageesTabTd = testDataManager.getDefault(TestPolicyPaymentPlansAndDownpayments.class).getTestData("TestData");
			policyTd = policyTd.adjust(mortgageesTabTd).resolveLinks();
		}

		mainApp().open();
		createCustomerIndividual();
		policyNumber = createPolicy(policyTd);
	}

	private void createProposedRenewal() {
		//Move time to R-35
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));//-35 days

		//Create Proposed Renewal
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	private void generatePaperBillManually() {
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

	private void navigateToRenewal() {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		new ProductRenewalsVerifier().setStatus(ProductConstants.PolicyStatus.PROPOSED).verify(1);
		policy.dataGather().start();
	}

	private void changePaymentPlanOnRenewal(String paymentPlan) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
		NavigationPage.toViewSubTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
		if (paymentPlan == BillingConstants.PaymentPlan.MORTGAGEE_BILL_RENEWAL) {
			premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.BILL_TO_AT_RENEWAL).setValue("Mortgagee");
			premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue(paymentPlan);
			premiumsAndCoveragesQuoteTab.submitTab();
			mortgageesTab.getAssetList().getAsset(HomeCaMetaData.MortgageesTab.MORTGAGEE).setValue("Yes");
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.NAME).setValue("MortgageeCompany");
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.ZIP_CODE)
					.setValue(getCustomerIndividualTD("DataGather", "GeneralTab_" + getState()).getValue("Zip Code"));
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.STREET_ADDRESS_1)
					.setValue(getCustomerIndividualTD("DataGather", "GeneralTab_" + getState()).getValue("Address Line 1"));
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.VALIDATE_ADDRESS_BTN).click();
			mortgageesTab.getValidateAddressDialogAssetList().getAsset(DialogsMetaData.AddressValidationMetaData.BTN_OK).click();
			mortgageesTab.getMortgageeInfoAssetList().getAsset(HomeCaMetaData.MortgageesTab.MortgageeInformation.LOAN_NUMBER).setValue("12345678");
		} else {
			premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue(paymentPlan);
		}
		premiumsAndCoveragesQuoteTab.calculatePremium();
	}

	private void checkMessageInBindTab(String message, String existinPaymentPlan, String changedPaymentPlan) {
		//Navigate to Bind tab
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.BIND.get());

		//Check New Message is shown in popup
		bindTab.btnPurchase.click();
		message = message.replace("EXISTINGPAYMENTPLAN", existinPaymentPlan).replace("CHANGEDPAYMENTPLAN", changedPaymentPlan);
		assertThat(bindTab.confirmEndorsementPurchase.labelMessage.getValue()).isEqualTo(message);

		bindTab.confirmEndorsementPurchase.confirm();
	}
}
