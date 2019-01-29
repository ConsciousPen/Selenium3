package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.renewal.RenewalHelper_Home;
import aaa.helpers.TestDataHelper;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssertions;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

import java.time.LocalDateTime;

import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT;

/**
 *
 * Test Membership Validation and override in NB, Endorsement & Renewal <br>
 * 1. New Business <br>
 * 2. Endorsement <br>
 * 3. Flat Endorsement <br>
 * 4. Renewal <br>
 * 5. Dummy Number NB, Endorsement, and Renewal
 * @author Tyrone Jemison
 **/
@StateList(states = Constants.States.AZ)
public class TestMembershipValidation extends HomeSSHO3BaseTest {

    private ApplicantTab applicantTab = new ApplicantTab();
    private BindTab bindTab = new BindTab();
    private ReportsTab reportsTab = new ReportsTab();
    private ErrorTab errorTab = new ErrorTab();

    //Test Data Helper
    TestDataHelper _myTestDataHelper = new TestDataHelper();

    /**
     * This is the new version of pas3786_validateMembership that is targeted at improving the previous version in several ways to include: <br>
     *     1. Improved Stability; 2. Improved Stability; 3. Improved Traceability; 4. Reduced Upkeep. <br>
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, priority = 1, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
    public void pas3786_validateMembership_NewBusiness(@Optional("AZ") String state) {

        // Create Test Data for New Business IN-ACTIVE Member first. Will not match and will throw error.
        TestData _inActiveMemberTD = getPolicyDefaultTD();
        _myTestDataHelper.adjustTD(_inActiveMemberTD, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), "ABC");
        _myTestDataHelper.adjustTD(_inActiveMemberTD, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), "XYZ");
        _myTestDataHelper.adjustTD(_inActiveMemberTD, ApplicantTab.class, HomeSSMetaData.ApplicantTab.NAMED_INSURED.getLabel(), HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), "01/01/1942");
        createQuoteAndFillUpTo(_inActiveMemberTD, BindTab.class, true);
        bindTab.btnPurchase.click();

        // Validate there is an error due NO MATCHING First Name, Last Name and DOB.
        errorTab.verify.errorsPresent(true, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
        errorTab.cancel();

        // Change ONLY First Name data to match an active member from STUB.
        ChangeDataOrderReportsBind("John", "XYZ", "01/01/1942", true);

        // Change ONLY Last Name data to match an active member from STUB.
        ChangeDataOrderReportsBind("ABC", "Smith", "01/01/1942", false);

        // Change ONLY DOB data to match an active member from STUB.
        ChangeDataOrderReportsBind("ABC", "XYZ", "11/10/1968", true);

        // Change ALL data to match an active member from STUB.
        GoToApplicantTabAndChangeApplicantData("John", "Smith", "11/10/1968");
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);
    }

    /**
     * This test will leverage a policy created by 'pas3786_validateMembership_NewBusiness' if available and perform
     *     an endorsement test for Membership Validation. <br>
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, priority = 2, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
    public void pas3786_validateMembership_Endorsement(@Optional("AZ") String state) {
        TestData _testData = getPolicyDefaultTD();

        mainApp().open();
        createCustomerIndividual();
        createPolicy(_testData);

        MoveJVMAndOpenPolicyInPAS(TimeSetterUtil.getInstance().getCurrentTime().plusDays(1L));

        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        // Change NOTHING so that NO DATA MATCHES Stub.
        ChangeDataOrderReportsBind("ABC", "XYZ", "01/01/1942", true);

        // Change ONLY First Name data to match an active member from STUB.
        ChangeDataOrderReportsBind("John", "XYZ", "01/01/1942", true);

        // Change ONLY Last Name data to match an active member from STUB.
        GoToApplicantTabAndChangeApplicantData("ABC", "Smith", "01/01/1942");
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);

        // Change ONLY DOB data to match an active member from STUB.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData")); // Reopen Endorsement From Binding
        GoToApplicantTabAndChangeApplicantData("ABC", "XYZ", "11/10/1968");
        ReorderReportsFromReportsTab();
        AttemptToBindValidateResults(true);

        // Change ALL data to match an active member from STUB.
        GoToApplicantTabAndChangeApplicantData("John", "Smith", "11/10/1968");
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);

    }

    /**
     * This test will leverage a policy created by 'pas3786_validateMembership_NewBusiness' if available and perform
     *     an endorsement test for Membership Validation. <br>
     * @param state
     */
    @Parameters({"state"})
    @Test(enabled = true, priority = 3, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
    public void pas3786_validateMembership_FlatEndorsement(@Optional("AZ") String state) {
        TestData _testData = getPolicyDefaultTD();

        mainApp().open();
        createCustomerIndividual();
        createPolicy(_testData);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

        // Change NOTHING so that NO DATA MATCHES Stub.
        ChangeDataOrderReportsBind("ABC", "XYZ", "01/01/1942", true);

        // Change ONLY First Name data to match an active member from STUB.
        ChangeDataOrderReportsBind("John", "XYZ", "01/01/1942", true);

        // Change ONLY Last Name data to match an active member from STUB.
        GoToApplicantTabAndChangeApplicantData("ABC", "Smith", "01/01/1942");
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);

        // Change ONLY DOB data to match an active member from STUB.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData")); // Reopen Endorsement From Binding
        GoToApplicantTabAndChangeApplicantData("ABC", "XYZ", "11/10/1968");
        ReorderReportsFromReportsTab();
        AttemptToBindValidateResults(true);

        // Change ALL data to match an active member from STUB.
        GoToApplicantTabAndChangeApplicantData("John", "Smith", "11/10/1968");
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);

    }

    /**
     *  Handles Membership Validation Testing during Renewals.
     * @param state
     * @runTime ~15min.
     */
    @Parameters({"state"})
    @Test(enabled = true, priority = 4, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
    public void pas3786_validateMembership_Renewal(@Optional("AZ") String state){
        TestData _testData = getPolicyDefaultTD();

        mainApp().open();
        createCustomerIndividual();
        createPolicy(_testData);
        String policyNumber = PolicySummaryPage.getPolicyNumber();

        // Get Policy to Renewal Timepoint.
        RenewalHelper_Home renewalHelper = new RenewalHelper_Home(getState(), true);
        renewalHelper.moveToEndOfFirstTerm(false);

        // Retrieve Policy
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        Tab.buttonGo.click();
        Tab.buttonOk.click();
        Page.dialogConfirmation.buttonOk.click();

        // Change NOTHING so that NO DATA MATCHES Stub.
        ChangeDataOrderReportsBind("ABC", "XYZ", "01/01/1942", true);

        // Change ONLY First Name data to match an active member from STUB.
        ChangeDataOrderReportsBind("John", "XYZ", "01/01/1942", true);

        // Change ONLY Last Name data to match an active member from STUB.
        GoToApplicantTabAndChangeApplicantData("ABC", "Smith", "01/01/1942");
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);

        // Change ONLY DOB data to match an active member from STUB.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData")); // Reopen Endorsement From Binding
        ChangeDataOrderReportsBind("ABC", "XYZ", "11/10/1968", true);

        // Change ALL data to match an active member from STUB.
        GoToApplicantTabAndChangeApplicantData("John", "Smith", "11/10/1968");
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);
    }

    @Parameters({"state"})
    @Test(enabled = true, priority = 5, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
    public void pas3786_validateMembership_DummyMembershipNumber(@Optional("") String state){
        final String DUMMYNUMBER = "9999999999999995";

        // Create NB Quote and go to Bind Tab.
        TestData _dummyMemberTD = getPolicyDefaultTD();
        _myTestDataHelper.adjustTD(_dummyMemberTD, ApplicantTab.class, HomeSSMetaData.ApplicantTab.AAA_MEMBERSHIP.getLabel(), HomeSSMetaData.ApplicantTab.AAAMembership.MEMBERSHIP_NUMBER.getLabel(), DUMMYNUMBER);
        createQuoteAndFillUpTo(_dummyMemberTD, BindTab.class, true);

        // Bind and Purchase with No UW Rule Errors.
        bindTab.submitTab();
        new PurchaseTab().fillTab(_dummyMemberTD);
        new PurchaseTab().submitTab();

        // Assert Policy Bound
        CustomAssertions.assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualToIgnoringCase("Policy Active");

        // Start Endorsement.
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);

        // Start Renewal
        String policyNumber = PolicySummaryPage.getPolicyNumber();
        RenewalHelper_Home renewalHelper = new RenewalHelper_Home(getState(), true);
        renewalHelper.moveToEndOfFirstTerm(false);

        // Retrieve Policy
        mainApp().open();
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        Tab.buttonGo.click();
        Tab.buttonOk.click();
        Page.dialogConfirmation.buttonOk.click();

        // Validate No Error to Purchase Renewal
        ReorderReportsFromReportsTab();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        new PremiumsAndCoveragesQuoteTab().calculatePremium();
        AttemptToBindValidateResults(false);
    }

    public void ChangeDataOrderReportsBind(String in_firstName, String in_lastName, String in_dateOfBirth, boolean in_bErrorShouldOccur) {
        GoToApplicantTabAndChangeApplicantData(in_firstName, in_lastName, in_dateOfBirth);
        ReorderReportsFromReportsTab();
        AttemptToBindValidateResults(in_bErrorShouldOccur);
    }

    public void ReorderReportsFromReportsTab(){
        // Go to Reports Tab and Reorder Insurance and CLUE Data
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());

        try {
            reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
        }catch(Exception ex){
            log.error("<QADEBUG-ERROR> Could not click 'I Agree' on Reports Tab. Repeating steps to ensure test is on Reports Tab and didn't somehow slip. <QADEBUG-ERROR>");
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
            reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
        }
        reportsTab.tblInsuranceScoreReport.getRow(1).getCell(11).click();
        reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.getFirst().click();
    }

    public void AttemptToBindValidateResults(boolean bShouldErrorOccur){
        boolean bIsQuote = bindTab.getPolicyNumber().startsWith("Q"); // <-- determine via policy number before potentially throwing the error.

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.btnPurchase.click();

        // Check if we're in an endorsement. If so, there's an extra button to click to keep the test on the rails.
        if(!bIsQuote){
            bindTab.confirmEndorsementPurchase.buttonYes.click();
        }

        // The Validation Itself
        if(bShouldErrorOccur){
            errorTab.verify.errorsPresent(bShouldErrorOccur, ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
            errorTab.cancel();
        }else {
            if (bIsQuote) { // If policy Number begins with Q, we click the New Business Button
                CustomAssertions.assertThat(bindTab.confirmPurchase.buttonNo.getWebElement().isDisplayed()).isTrue();
                bindTab.confirmPurchase.buttonNo.click();
            }
        }
    }

    public void GoToApplicantTabAndChangeApplicantData(String firstName, String lastName, String DOB){
        // Change data so ONLY First Name matches an Active Member from STUB Data.
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.getNamedInsuredAssetList().getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME.getLabel(), TextBox.class).setValue(firstName);
        applicantTab.getNamedInsuredAssetList().getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME.getLabel(), TextBox.class).setValue(lastName);
        applicantTab.getNamedInsuredAssetList().getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH.getLabel(), TextBox.class).setValue(DOB);
    }

    public void MoveJVMAndOpenPolicyInPAS(LocalDateTime desiredTime){
        String generatedPolicyNumber = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        TimeSetterUtil.getInstance().nextPhase(desiredTime);
        mainApp().open();
        SearchPage.openPolicy(generatedPolicyNumber);
    }


    /*    DEPRECIATED. Retired for reference or recall.


    @Parameters({"state"})
    @Test(enabled = false, groups = { Groups.FUNCTIONAL, Groups.CRITICAL }, description = "30504: Membership Validation Critical Defect Stabilization")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-3786")
    public void pas3786_validateMembership_OLD(@Optional("AZ") String state) {
        TestData tdEndorsementStart = getPolicyTD("Endorsement", "TestData_Plus1Day");
        TestData tdRenewalStart = getPolicyTD("Renew", "TestData");

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
        createPolicy(getPolicyDefaultTD());
        policy.endorse().perform(tdEndorsementStart);
        log.info("Membership Full Validation for Endorsement Quote Started..");
        fullMembershipMatchValidation(_tdPolicy, tdMembershipOverride, tdMembershipSecondMember, tdMembershipThirdMember);
        log.info("Membership Full Validation for Endorsement Quote Completed.");

        mainApp().close();
    }


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

    private void validateMembership(){
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());

        if (!reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).isPresent()){
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        }

        if (!reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).isPresent()){
            NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        }

        reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).waitForAccessible(10000);
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

    private void changeToInitialData(TestData tdMembership){
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME)
                .setValue(tdMembership.getTestData("ApplicantTab", "NamedInsured").getValue("First name"));
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.LAST_NAME)
                .setValue(tdMembership.getTestData("ApplicantTab", "NamedInsured").getValue("Last name"));
        applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.NAMED_INSURED).getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH)
                .setValue(tdMembership.getTestData("ApplicantTab", "NamedInsured").getValue("Date of birth"));
    }

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

    */
}
