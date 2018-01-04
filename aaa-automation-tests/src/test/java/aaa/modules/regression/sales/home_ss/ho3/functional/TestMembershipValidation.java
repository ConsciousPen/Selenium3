package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
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
 * 4. Validate that membership works for other members with the same Membership number on NB
 * 5. Purchase quote with overridden Membership error
 * 6. Do Full Membership validation (First Name, Last Name, DOB) for Renewal Quote
 * 7. Validate that membership works for other members with the same Membership number on Renewal
 * 8. Save & Exit renewal quote with overridden Membership error
 * 9. Do Membership validation with Dummy Number on Renewal
 * 10. Save & Exit renewal quote
 * 11. Do Full Membership validation (First Name, Last Name, DOB) for Endorsement Quote
 * 12. Validate that membership works for other members with the same Membership number on Endorsement
 * 13. Purchase endorsement quote with overridden Membership error
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
    @Test(groups = { Groups.REGRESSION, Groups.CRITICAL }, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
    public void pas3786_validateMembership(@Optional("AZ") String state) {

        TestData tdPolicy = getTestSpecificTD("TestData_MembershipValidationHO3");
        TestData tdEndorsementStart = getPolicyTD("Endorsement", "TestData_Plus1Month");
        TestData tdRenewalStart = getPolicyTD("Renew", "TestData");
        TestData tdMembershipOverride = getTestSpecificTD("TestData_MembershipValidationHO3_OverrideErrors");
        TestData tdMembershipDummy = getTestSpecificTD("TestData_MembershipValidationHO3_Dummy");
        TestData tdMembershipSecondMember = getTestSpecificTD("TestData_MembershipValidationHO3_SecondMember");
        TestData tdMembershipThirdMember = getTestSpecificTD("TestData_MembershipValidationHO3_ThirdMember");

        mainApp().open();

        createCustomerIndividual();
        log.info("Quote Creation Started...");

        // NB Quote Membership Validation
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPolicy, BindTab.class, true);
        log.info("Membership Full Validation for NB Quote Started..");
        fullMembershipMatchValidation(tdPolicy, tdMembershipOverride, tdMembershipSecondMember, tdMembershipThirdMember);
        log.info("Membership Full Validation for NB Quote Completed.");

        // Renewal Quote Membership Validation
        policy.renew().perform(tdRenewalStart);
        log.info("Membership Full Validation for Renewal Quote Started..");
        fullMembershipMatchValidation(tdPolicy, tdMembershipOverride, tdMembershipSecondMember, tdMembershipThirdMember);
        log.info("Membership Full Validation for Renewal Quote Completed.");

        // Renewal Quote Membership Validation with Dummy Number
        createPolicy(tdPolicy);
        policy.renew().perform(tdRenewalStart);
        log.info("Membership Validation for Renewal Quote with Dummy Number Started..");
        verifyDummyNumber(tdMembershipDummy);
        log.info("Membership Validation for Renewal Quote with Dummy Number Completed..");

        // Endorsement Quote Membership Validation
        createPolicy(tdPolicy);
        policy.endorse().perform(tdEndorsementStart);
        log.info("Membership Full Validation for Endorsement Quote Started..");
        fullMembershipMatchValidation(tdPolicy, tdMembershipOverride, tdMembershipSecondMember, tdMembershipThirdMember);
        log.info("Membership Full Validation for Endorsement Quote Completed.");

        mainApp().close();
    }

    /*
    Method checks Functionality with all cases for Membership Match
     */
    private void fullMembershipMatchValidation(TestData tdMembershipInitial, TestData tdMembershipOverride, TestData tdMembershipSecondMember, TestData tdMembershipThirdMember){
        changeToInitialData(tdMembershipInitial);

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
        changeToInitialData(tdMembershipInitial);
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME).setValue("ChangedLastName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP)
                .getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME).setValue("ChangedLastName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue("05/17/1990");
        validateMembership();

        // only DOB MATCHES Membership info
        changeToInitialData(tdMembershipInitial);
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME).setValue("ChangedLastName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP)
                .getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME).setValue("ChangedLastName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME).setValue("ChangedFirstName");
        validateMembership();

        // Validate that membership works for other members with the same Membership number
        validateMembershipWithOtherMembers(tdMembershipInitial, tdMembershipSecondMember);
        validateMembershipWithOtherMembers(tdMembershipInitial, tdMembershipThirdMember);

        // NO MATCH/Validation Error Check & Verify that Policy can be bound
        changeToInitialData(tdMembershipInitial);
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME).setValue("ChangedLastName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP)
                .getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME).setValue("ChangedLastName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME).setValue("ChangedFirstName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue("05/17/1990");

        verifyMembershipErrorAndBind(tdMembershipOverride);
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /*
    Method checks that Membership error is not fired
     */
    private void validateMembership(){
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.reorderReports();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click();

        if (errorTab.isVisible()){
            errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
            log.info("Membership Error Validation Passed. Moving on to the next condition.");
            errorTab.cancel();
        } else if (bindTab.confirmPurchase.isVisible() && bindTab.confirmPurchase.isPresent()){
            log.info("[NB Quote] Membership Error Validation Passed. Moving on to the next condition.");
            bindTab.confirmPurchase.buttonNo.click(Waiters.AJAX);
        } else if (bindTab.confirmEndorsementPurchase.isVisible() && bindTab.confirmEndorsementPurchase.isPresent()){
            log.info("[Endorsement Quote] Membership Error Validation Passed. Moving on to the next condition.");
            bindTab.confirmEndorsementPurchase.buttonNo.click(Waiters.AJAX);
        } else if (bindTab.confirmRenewPurchase.isVisible() && bindTab.confirmRenewPurchase.isPresent()) {
            log.info("[Renewal Quote] Membership Error Validation Passed. Moving on to the next condition.");
            bindTab.confirmRenewPurchase.buttonNo.click(Waiters.AJAX);
        }
    }

    /*
    Method changes First Name, Last Name, DOB to initial data of Active Membership from Test Data file
     */
    private void changeToInitialData(TestData tdMembership){
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
    private void verifyMembershipErrorAndBind(TestData tdMembershipOverride) {
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.reorderReports();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click();

        // Condition to pass: ErrorTab appears after confirming purchase dialog (Renewal & Endorsement)
        if (!errorTab.isVisible()){
            if (bindTab.confirmPurchase.isVisible() && bindTab.confirmPurchase.isPresent()){
                bindTab.confirmPurchase.buttonYes.click(Waiters.AJAX);
            } else if (bindTab.confirmEndorsementPurchase.isVisible() && bindTab.confirmEndorsementPurchase.isPresent()){
                bindTab.confirmEndorsementPurchase.buttonYes.click(Waiters.AJAX);
            } else if (bindTab.confirmRenewPurchase.isVisible() && bindTab.confirmRenewPurchase.isPresent()) {
                bindTab.confirmRenewPurchase.buttonYes.click(Waiters.AJAX);
            }
        }

        errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
        errorTab.getAssetList().fill(tdMembershipOverride);
        log.info("Last Condition passed in current Quote. Membership Error is successfully overridden..");

        errorTab.submitTab();

        // Purchase screen [NB Quote]
        if (purchaseTab.isVisible()) {
            purchaseTab.payRemainingBalance(BillingConstants.AcceptPaymentMethod.CASH).submitTab();
        }
    }

    /*
    Method validates that validation error is not thrown for dummy number
    */
    private void verifyDummyNumber(TestData tdMembership) {
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER)
                .setValue(tdMembership.getTestData("ApplicantTab", "AAAMembership").getValue("Membership number"));

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.reorderReports();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click();

        if (bindTab.confirmRenewPurchase.isVisible() && bindTab.confirmRenewPurchase.isPresent()) {
            bindTab.confirmRenewPurchase.buttonYes.click(Waiters.AJAX);
        }

        if (errorTab.isVisible()){
            errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
            errorTab.cancel();
            bindTab.submitTab();
        }

        log.info("Membership Error Validation with Dummy Number Passed.");
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /*
    Method validates that validation error is not thrown using same membership number but other Member's data (First Name)
    */
    private void validateMembershipWithOtherMembers(TestData tdMembershipInitial, TestData tdOtherMembers) {
        changeToInitialData(tdMembershipInitial);
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME)
                .setValue(tdOtherMembers.getTestData("ApplicantTab", "NamedInsured").getValue("First name"));
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME)
                .setValue("ChangedLastName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH)
                .setValue("05/17/1990");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP).getAsset(HomeSSMetaData.ApplicantTab.AAAMembership.LAST_NAME)
                .setValue("ChangedLastName");

        validateMembership();
    }
}
