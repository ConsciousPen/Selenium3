package aaa.modules.regression.sales.home_ss.ho3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.waiters.Waiters;

/**
 * @author Mantas Garsvinskas
 * @name Test Membership Validation and override in NB, Endorsement & Renewal
 * @scenario
 * 1. Create Customer
 * 2. Initiate new Homeowners HO3 quote creation
 * 3. Do Full Membership validation (First Name, Last Name, DOB) for NB Quote
 * 4. Purchase quote with overridden Membership error
 * 5. Do Full Membership validation (First Name, Last Name, DOB) for Endorsement Quote
 * 6. Purchase endorsement quote with overridden Membership error
 * 7. Do Full Membership validation (First Name, Last Name, DOB) for Renewal Quote
 * 8. Save & Exit renewal quote with overridden Membership error
 * @details
 **/
public class TestMembershipValidation extends HomeSSHO3BaseTest {

	private ApplicantTab applicantTab = new ApplicantTab();
	private BindTab bindTab = new BindTab();
	private ReportsTab reportsTab = new ReportsTab();
	private ErrorTab errorTab = new ErrorTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
	private PurchaseTab purchaseTab = new PurchaseTab();

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "30504: Membership Validation Critical Defect Stabilization")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
	public void testMembershipValidation(@Optional("AZ") String state) {

		TestData td_HO_SS_Membership = getTestSpecificTD("TestData_MembershipValidationHO3");
		TestData td_endorsement_start = getPolicyTD("Endorsement", "TestData_Plus1Month");
		TestData td_renewal_start = getPolicyTD("Renew", "TestData");

		mainApp().open();

		createCustomerIndividual();
		log.info("Quote Creation Started...");

		// NB Quote Membership Validation
		policy.initiate();
		policy.getDefaultView().fillUpTo(td_HO_SS_Membership, BindTab.class, true);
		log.info("Membership Full Validation for NB Quote Started..");
		fullMembershipMatchValidation(td_HO_SS_Membership);
		log.info("Membership Full Validation for NB Quote Completed.");

		// Renewal Quote Membership Validation
		policy.renew().perform(td_renewal_start);
		log.info("Membership Full Validation for Renewal Quote Started..");
		fullMembershipMatchValidation(td_HO_SS_Membership);
		log.info("Membership Full Validation for Renewal Quote Completed.");

		// Endorsement Quote Membership Validation
		createPolicy(td_HO_SS_Membership);
		policy.endorse().perform(td_endorsement_start);
		log.info("Membership Full Validation for Endorsement Quote Started..");
		fullMembershipMatchValidation(td_HO_SS_Membership);
		log.info("Membership Full Validation for Endorsement Quote Completed.");

		mainApp().close();
	}

	/*
	Method checks Functionality with all cases for Membership Match
	 */
	private void fullMembershipMatchValidation(TestData td) {
		changeToInitialData(td);

		// All three fields MATCH Membership info
		validateMembership();

		// only Last Name MATCHES Membership info
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
				.getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME).setValue("ChangedFirstName");
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
				.getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue("05/17/1990");
		validateMembership();

		// only First Name MATCHES Membership info
		changeToInitialData(td);
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
				.getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME).setValue("ChangedLastName");
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP)
				.getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME).setValue("ChangedLastName");
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
				.getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue("05/17/1990");
		validateMembership();

		// only DOB MATCHES Membership info
		changeToInitialData(td);
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
				.getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME).setValue("ChangedLastName");
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP)
				.getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME).setValue("ChangedLastName");
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
				.getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME).setValue("ChangedFirstName");
		validateMembership();

		// NO MATCH/Validation Error Check & Verify that Policy can be bound
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
				.getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue("05/17/1990");

		verifyMembershipErrorAndBind(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
	}

	/*
	Method checks that Membership error is not fired
	 */
	private void validateMembership() {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		reportsTab.reorderReports();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.btnPurchase.click();

		if (errorTab.isVisible()) {
			errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
			log.info("Membership Error Validation Passed. Moving on to the next condition.");
			errorTab.cancel();
		} else if (bindTab.confirmPurchase.isVisible() && bindTab.confirmPurchase.isPresent()) {
			log.info("[NB Quote] Membership Error Validation Passed. Moving on to the next condition.");
			bindTab.confirmPurchase.buttonNo.click(Waiters.AJAX);
		} else if (bindTab.confirmEndorsementPurchase.isVisible() && bindTab.confirmEndorsementPurchase.isPresent()) {
			log.info("[Endorsement Quote] Membership Error Validation Passed. Moving on to the next condition.");
			bindTab.confirmEndorsementPurchase.buttonNo.click(Waiters.AJAX);
		} else if (bindTab.confirmRenewPurchase.isVisible() && bindTab.confirmRenewPurchase.isPresent()) {
			log.info("[Renewal Quote] Membership Error Validation Passed. Moving on to the next condition.");
			bindTab.confirmRenewPurchase.buttonNo.click(Waiters.AJAX);
		}
	}

	/*
	Method changes First Name, Last Name, DOB to initial data from Test Data file
	 */
	private void changeToInitialData(TestData tdMembership) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME)
				.setValue(tdMembership.getTestData("ApplicantTab", "NamedInsured").getValue("First name"));
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME)
				.setValue(tdMembership.getTestData("ApplicantTab", "NamedInsured").getValue("Last name"));
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME)
				.setValue(tdMembership.getTestData("ApplicantTab", "AAAMembership").getValue("Last name"));
		applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH)
				.setValue(tdMembership.getTestData("ApplicantTab", "NamedInsured").getValue("Date of birth"));
	}

	/*
	Method validates that required error is present in Error Page & Binds the policy with overridden error
	*/
	private void verifyMembershipErrorAndBind(ErrorEnum.Errors errorCode) {
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
		reportsTab.reorderReports();
		premiumsAndCoveragesQuoteTab.calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
		bindTab.btnPurchase.click();

		// Condition to pass: ErrorTab appears after confirming purchase dialog (Renewal & Endorsement)
		if (!errorTab.isVisible()) {
			if (bindTab.confirmPurchase.isVisible() && bindTab.confirmPurchase.isPresent()) {
				bindTab.confirmPurchase.buttonYes.click(Waiters.AJAX);
			} else if (bindTab.confirmEndorsementPurchase.isVisible() && bindTab.confirmEndorsementPurchase.isPresent()) {
				bindTab.confirmEndorsementPurchase.buttonYes.click(Waiters.AJAX);
			} else if (bindTab.confirmRenewPurchase.isVisible() && bindTab.confirmRenewPurchase.isPresent()) {
				bindTab.confirmRenewPurchase.buttonYes.click(Waiters.AJAX);
			}
		}

		errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
		errorTab.overrideErrors(ErrorEnum.Duration.TERM, ErrorEnum.ReasonForOverride.AAA_ERROR, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
		log.info("Last Condition passed in current Quote. Membership Error is successfully overridden..");

		bindTab.submitTab();

		// Purchase screen [NB Quote]
		if (purchaseTab.isVisible()) {
			purchaseTab.payRemainingBalance(BillingConstants.AcceptPaymentMethod.CASH).submitTab();
		}
	}
}