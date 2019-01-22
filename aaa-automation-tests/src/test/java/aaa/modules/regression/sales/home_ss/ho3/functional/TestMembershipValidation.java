package aaa.modules.regression.sales.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.BillingConstants;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.waiters.Waiters;

import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT;

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
@StateList(states = Constants.States.AZ)
public class TestMembershipValidation extends HomeSSHO3BaseTest {

    private ApplicantTab applicantTab = new ApplicantTab();
    private BindTab bindTab = new BindTab();
    private ReportsTab reportsTab = new ReportsTab();
    private ErrorTab errorTab = new ErrorTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private PurchaseTab purchaseTab = new PurchaseTab();

    //Test Data Helper
    TestDataHelper _myTestDataHelper = new TestDataHelper();

    @Parameters({"state"})
    @Test(enabled = true, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
    public void pas3786_validateMembership(@Optional("AZ") String state) {
        TestData tdEndorsementStart = getPolicyTD("Endorsement", "TestData_Plus1Day");
        TestData tdRenewalStart = getPolicyTD("Renew", "TestData");

//      TODO: Find a way to construct this TestData object.
        TestData tdMembershipOverride = getTestSpecificTD("TestData_MembershipValidationHO3_OverrideErrors");

        TestData _tdPolicy = getPolicyDefaultTD();
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), "Ronhald");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), "Ronaldjo");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "01/02/1960");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.SOCIAL_SECURITY_NUMBER.getLabel(), "1234567890");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.CURRENT_AAA_MEMBER.getLabel(), "Yes");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "9436258506738011");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), HomeSSMetaData.ApplicantTab.DwellingAddress.STREET_ADDRESS_1.getLabel(), "267 CHIPMAN AVE");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), HomeSSMetaData.ApplicantTab.DwellingAddress.ZIP_CODE.getLabel(), "85003");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.DWELLING_ADDRESS.getLabel(), HomeSSMetaData.ApplicantTab.DwellingAddress.CITY.getLabel(), "Phoenix");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.PREVIOUS_DWELLING_ADDRESS.getLabel(), HomeSSMetaData.ApplicantTab.PreviousDwellingAddress.HAS_PREVIOUS_DWELLING_ADDRESS.getLabel(), "No");
        _myTestDataHelper.adjustTD(_tdPolicy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.MAILING_ADDRESS.getLabel(), HomeSSMetaData.ApplicantTab.MailingAddress.IS_DIFFERENT_MAILING_ADDRESS.getLabel(), "No");

        TestData tdMembershipDummy = getPolicyTD();
        _myTestDataHelper.adjustTD(tdMembershipDummy, ApplicantTab.class, HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "9999999999999995");

        TestData tdMembershipSecondMember = getPolicyTD();
        _myTestDataHelper.adjustTD(tdMembershipSecondMember, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), "Honor");
        _myTestDataHelper.adjustTD(tdMembershipSecondMember, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), "McGrager");
        _myTestDataHelper.adjustTD(tdMembershipSecondMember, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "01/04/1962");
        _myTestDataHelper.adjustTD(tdMembershipSecondMember, ApplicantTab.class, HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "9436258506738011");

        TestData tdMembershipThirdMember = getPolicyTD();
        _myTestDataHelper.adjustTD(tdMembershipThirdMember, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), "Anthonio");
        _myTestDataHelper.adjustTD(tdMembershipThirdMember, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), "Bandero");
        _myTestDataHelper.adjustTD(tdMembershipThirdMember, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "01/05/1966");
        _myTestDataHelper.adjustTD(tdMembershipThirdMember, ApplicantTab.class, HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), "9436258506738011");

        mainApp().open();

        createCustomerIndividual();
        log.info("Quote Creation Started...");

        // NB Quote Membership Validation
        policy.initiate();
        policy.getDefaultView().fillUpTo(_tdPolicy, BindTab.class, true);
        log.info("Membership Full Validation for NB Quote Started..");
        fullMembershipMatchValidation(_tdPolicy, tdMembershipOverride, tdMembershipSecondMember, tdMembershipThirdMember);
        log.info("Membership Full Validation for NB Quote Completed.");
        //BUG PAS-12369: Membership Validation UW eligibility rule is not fired at Midterm Endorsement/Renewal
        // Renewal Quote Membership Validation
        policy.renew().perform(tdRenewalStart);
        log.info("Membership Full Validation for Renewal Quote Started..");
        fullMembershipMatchValidation(_tdPolicy, tdMembershipOverride, tdMembershipSecondMember, tdMembershipThirdMember);
        log.info("Membership Full Validation for Renewal Quote Completed.");

        // Renewal Quote Membership Validation with Dummy Number
        createPolicy(_tdPolicy);
        policy.renew().perform(tdRenewalStart);
        log.info("Membership Validation for Renewal Quote with Dummy Number Started..");
        verifyDummyNumber(tdMembershipDummy);
        log.info("Membership Validation for Renewal Quote with Dummy Number Completed..");
        //BUG PAS-12369: Membership Validation UW eligibility rule is not fired at Midterm Endorsement/Renewal
        // Endorsement Quote Membership Validation
        createPolicy(_tdPolicy);
        policy.endorse().perform(tdEndorsementStart);
        log.info("Membership Full Validation for Endorsement Quote Started..");
        fullMembershipMatchValidation(_tdPolicy, tdMembershipOverride, tdMembershipSecondMember, tdMembershipThirdMember);
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
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue("05/17/1990");
        validateMembership();

        // only DOB MATCHES Membership info
        changeToInitialData(tdMembershipInitial);
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME).setValue("ChangedLastName");
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
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME).setValue("ChangedFirstName");
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED)
                .getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue("05/17/1990");

        verifyMembershipErrorAndBind(tdMembershipOverride);
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /*
    Method checks that Membership error is not fired
     */
    private void validateMembership(){
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());

        reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).waitForAccessible(5000);
        reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");

        reportsTab.tblInsuranceScoreReport.getRow(1).getCell(11).click();

        if (reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().getValue().equals("Order report")|| reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().getValue().equals("Re-order report")) {
            reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().click();
        }

        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click();

        if (errorTab.isVisible()){
            errorTab.verify.errorsPresent(false, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
            log.info("Membership Error Validation Passed. Moving on to the next condition.");
            errorTab.cancel();
        } else if (bindTab.confirmPurchase.isPresent() && bindTab.confirmPurchase.isVisible()){
            log.info("[NB Quote] Membership Error Validation Passed. Moving on to the next condition.");
            bindTab.confirmPurchase.buttonNo.click(Waiters.AJAX);
        } else if (bindTab.confirmEndorsementPurchase.isPresent() && bindTab.confirmEndorsementPurchase.isVisible()){
            log.info("[Endorsement Quote] Membership Error Validation Passed. Moving on to the next condition.");
            bindTab.confirmEndorsementPurchase.buttonNo.click(Waiters.AJAX);
        } else if (bindTab.confirmRenewPurchase.isPresent() && bindTab.confirmRenewPurchase.isVisible()) {
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
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH)
                .setValue(tdMembership.getTestData("ApplicantTab", "NamedInsured").getValue("Date of birth"));
    }

    /*
    Method validates that required error is present in Error Page & Binds the policy with overridden error
    */
    private void verifyMembershipErrorAndBind(TestData tdMembershipOverride) {
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
        reportsTab.tblInsuranceScoreReport.getRow(1).getCell(11).click();
        if (reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().getValue().equals("Order report")|| reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().getValue().equals("Re-order report")) {
            reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().click();
        }
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click();

        // Condition to pass: ErrorTab appears after confirming purchase dialog (Renewal & Endorsement)
        if (!errorTab.isVisible()){
            bindTab.confirmPurchase();
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
        reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
        reportsTab.tblInsuranceScoreReport.getRow(1).getCell(11).click();
        if (reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().getValue().equals("Order report")|| reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().getValue().equals("Re-order report")) {
            reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().click();
        }
        if (!reportsTab.tblAAAMembershipReport.getRow(1).getCell("Report").controls.links.getFirst().getValue().equals("View report")) {
            reportsTab.tblAAAMembershipReport.getRow(1).getCell("Report").controls.links.getFirst().click();
        }
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click();

        if (bindTab.confirmRenewPurchase.isPresent() && bindTab.confirmRenewPurchase.isVisible()) {
            bindTab.confirmRenewPurchase.buttonYes.click(Waiters.AJAX);
        }

            errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME); //PAS-3786 PAS-8815
            errorTab.cancel();
            bindTab.submitTab();

        log.info("Membership Error Validation with Dummy Number Passed.");
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
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

        validateMembership();
    }
}
