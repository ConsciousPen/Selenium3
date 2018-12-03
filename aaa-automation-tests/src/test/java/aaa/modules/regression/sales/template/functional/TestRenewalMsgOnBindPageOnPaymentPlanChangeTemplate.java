package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.DialogsMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.PurchaseMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho3.TestPolicyPaymentPlansAndDownpayments;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class TestRenewalMsgOnBindPageOnPaymentPlanChangeTemplate extends PolicyBaseTest {

	private LocalDateTime policyExpirationDate;

	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
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

	public void testRenewalMessageOnBindPageOnPaymentPlanChange(String initialPaymentPlan, Boolean isOnAutopay, String renewalPaymentPlan, String message, String initialPaymentPlanInRenewal) {
		String policyNumber = createPolicy(initialPaymentPlan, isOnAutopay);
		createProposedRenewal();
		navigateToRenewal(policyNumber);
		changePaymentPlanOnRenewal(renewalPaymentPlan);
		checkMessageInBindTab(message, initialPaymentPlanInRenewal, renewalPaymentPlan);
	}

	private String createPolicy(String paymentPlan, Boolean isOnAutoPay) {
		TestData policyTd =  getPolicyTD();
		policyTd = policyTd.adjust(TestData.makeKeyPath(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.class.getSimpleName(),
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
		return createPolicy(policyTd);
	}

	private void createProposedRenewal() {
		//Move time to R-35
		policyExpirationDate = PolicySummaryPage.getExpirationDate();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewOfferGenerationDate(policyExpirationDate));

		//Create Proposed Renewal
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}

	private void navigateToRenewal(String policyNumber) {
		mainApp().reopen();
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
			premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.BILL_TO_AT_RENEWAL).setValue("Insured");
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
