package aaa.modules.regression.sales.home_ss.ho3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Mantas Garsvinskas
 * @name Test Membership Error when Membership is not ordered during New Business (NB) and during Endorsement
 * @scenario
 * 1. Create Customer
 * 2. Initiate new Homeowners SS HO3 quote creation
 * 3. Validate Not Ordered Error after pressing Continue button [NB Quote]
 * 4. Validate Not Ordered Error after pressing on other Tab [NB Quote]
 * 5. Validate Not Ordered Error after pressing Calculate Premium Button [NB Quote]
 * 6. Validate Not Ordered Error after pressing Continue button [Endorsement Quote]
 * 7. Validate Not Ordered Error after pressing on other Tab [Endorsement Quote]
 * 8. Validate Not Ordered Error after pressing Calculate Premium Button [Endorsement Quote]
 * @details
 **/
public class TestNotOrderedMembershipError extends HomeSSHO3BaseTest {

    private ReportsTab reportsTab = new ReportsTab();
    private ErrorTab errorTab = new ErrorTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM}, description = "Membership Report order validation should be thrown on continue, Tab Out on Reports Tab as well as Premium Calc.")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-7524")
    public void pas7524_checkNotOrderedMembershipErrors(@Optional("AZ") String state) {

        TestData tdMembershipQuote = getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3");
        TestData tdEndorsementStart = getPolicyTD("Endorsement", "TestData_Plus1Month");
        TestData tdMembershipEndorsement = getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3_Endorsement");

        String notOrderedMembershipFirstMessage = "Member Since Date must be entered (AAA_HO_SS12170000) [for AAAHOMembershipRuleComponent.attributeForEligibilityRules]";
        String notOrderedMembershipSecondMessage = "You must order the Membership report.";
        String notOrderedMembershipThirdMessage = "Please order membership report. (AAA_HO_SS171221-K2nGJ) [for AAAHOMembershipR...";

        mainApp().open();
        createCustomerIndividual();

        // Errors validation during NB Quote
        log.info("Not Ordered Membership Errors Validation for NB Quote Started..");
        policy.initiate();

        // Validating first error condition [NB quote]
        policy.getDefaultView().fillUpTo(tdMembershipQuote, ReportsTab.class);
        reportsTab.getAssetList().fill(getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3"));
        reportsTab.submitTab();
        reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.WARNING_MESSAGE_BOX).verify.value(notOrderedMembershipFirstMessage);

        // Validating second error condition [NB quote]
        validateSecondError(notOrderedMembershipSecondMessage);

        // Validating third error condition [NB quote]
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        policy.getDefaultView().fillFromTo(tdMembershipQuote, PropertyInfoTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        validateThirdError(notOrderedMembershipThirdMessage);
        log.info("Not Ordered Membership Errors Validation for NB Quote Successfully Completed..");

        // Errors validation during Endorsement Quote
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Started..");
        createPolicy(tdMembershipEndorsement);
        policy.endorse().perform(tdEndorsementStart);

        // Validating first error condition [Endorsement Quote]
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        policy.getDefaultView().fillFromTo(tdMembershipQuote, ApplicantTab.class, ReportsTab.class);
        reportsTab.submitTab();
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipSecondMessage).verify.present();
        errorTab.cancel();

        // Validating second error condition [Endorsement Quote]
        validateSecondError(notOrderedMembershipSecondMessage);

        // Validating third error condition [Endorsement Quote]
        policy.endorse().start();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        validateThirdError(notOrderedMembershipThirdMessage);
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Successfully Completed..");

        mainApp().close();
    }

    /*
    Method validates that second type error is being thrown after pressing on other Tab
    */
    private void validateSecondError(String notOrderedMembershipSecondMessage){
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipSecondMessage).verify.present();
        errorTab.cancel();
        reportsTab.saveAndExit();
    }

    /*
    Method validates that third type error is being thrown after pressing on Premium and Coverages Tab and after pressing Calculate Premium button
    */
    private void validateThirdError(String notOrderedMembershipThirdMessage){
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipThirdMessage).verify.present();
        errorTab.cancel();
        premiumsAndCoveragesQuoteTab.saveAndExit();
    }
}
