package aaa.modules.regression.sales.home_ca.ho3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaHO3BaseTest;
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
 * 2. Initiate new Homeowners CA HO3 quote creation
 * 3. Validate Not Ordered Error after pressing Continue button [NB Quote]
 * 4. Validate Not Ordered Error after pressing on other Tab [NB Quote]
 * 5. Validate Not Ordered Error after pressing Calculate Premium Button [NB Quote]
 * 6. Validate Not Ordered Error after pressing Continue button [Endorsement Quote]
 * 7. Validate Not Ordered Error after pressing on other Tab [Endorsement Quote]
 * 8. Validate Not Ordered Error after pressing Calculate Premium Button [Endorsement Quote]
 * @details
 **/
public class TestNotOrderedMembershipError extends HomeCaHO3BaseTest {

    private ReportsTab reportsTab = new ReportsTab();
    private ErrorTab errorTab = new ErrorTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM}, description = "Membership Report order validation should be thrown on continue, Tab Out on Reports Tab as well as Premium Calc.")
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-7524")
    public void pas7524_checkNotOrderedMembershipErrors(@Optional("CA") String state) {

        TestData tdMembershipQuote = getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3");
        TestData tdEndorsementStart = getPolicyTD("Endorsement", "TestData_Plus1Month");
        TestData tdMembershipEndorsement = getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3_Endorsement");

        String notOrderedMembershipFirstMessage = "You must order the Membership report.";
        String notOrderedMembershipSecondMessage = "Please order membership report. (AAA_HO_CA171221-1PsQ6) [for AAAHOMembershipR...";

        mainApp().open();
        createCustomerIndividual();

        // Errors validation during NB Quote
        log.info("Not Ordered Membership Errors Validation for NB Quote Started..");
        policy.initiate();

        // Validating first error condition [NB Quote]
        policy.getDefaultView().fillUpTo(tdMembershipQuote, ReportsTab.class);
        reportsTab.getAssetList().fill(getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3"));
        validateFirstError(notOrderedMembershipFirstMessage);

        // Validating second error condition [NB Quote]
        validateSecondError(notOrderedMembershipFirstMessage);

        // Validating third error condition [NB Quote]
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        policy.getDefaultView().fillFromTo(tdMembershipQuote, PropertyInfoTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        validateThirdError(notOrderedMembershipSecondMessage);
        log.info("Not Ordered Membership Errors Validation for NB Quote Successfully Completed..");

        // Errors validation during Endorsement Quote
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Started..");
        createPolicy(tdMembershipEndorsement);
        policy.endorse().perform(tdEndorsementStart);

        // Validating first error condition [Endorsement Quote]
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        policy.getDefaultView().fillFromTo(tdMembershipQuote, ApplicantTab.class, ReportsTab.class);
        validateFirstError(notOrderedMembershipFirstMessage);

        // Validating second error condition [Endorsement Quote]
        validateSecondError(notOrderedMembershipFirstMessage);

        // Validating third error condition [Endorsement Quote]
        policy.endorse().start();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        validateThirdError(notOrderedMembershipSecondMessage);
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Successfully Completed..");

        mainApp().close();
    }

    /*
    Method validates that first type error is being thrown after pressing Continue
    */
    private void validateFirstError(String notOrderedMembershipFirstMessage){
        reportsTab.submitTab();
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipFirstMessage).verify.present();
        errorTab.cancel();
    }

    /*
    Method validates that first type error is being thrown after pressing on other Tab
    */
    private void validateSecondError(String notOrderedMembershipFirstMessage){
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipFirstMessage).verify.present();
        errorTab.cancel();
        reportsTab.saveAndExit();
    }

    /*
    Method validates that second type error is being thrown after pressing on Premium and Coverages Tab and after pressing Calculate Premium button
    */
    private void validateThirdError(String notOrderedMembershipSecondMessage){
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipSecondMessage).verify.present();
        errorTab.cancel();
        premiumsAndCoveragesQuoteTab.saveAndExit();
    }
}

