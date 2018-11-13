package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Mantas Garsvinskas
 * @name Test Membership Error when Membership is not ordered during New Business (NB) and during Endorsement
 * @scenario
 * 1. Create Customer
 * 2. Initiate new Auto SS quote creation
 * 3. Validate Not Ordered Error after pressing Continue button [NB Quote]
 * 4. Validate Not Ordered Error after pressing on other Tab [NB Quote]
 * 6. Validate Not Ordered Error after pressing Continue button [Endorsement Quote]
 * 7. Validate Not Ordered Error after pressing on other Tab [Endorsement Quote]
 * @details
 **/
public class TestNotOrderedMembershipError extends AutoSSBaseTest {

    private RatingDetailReportsTab ratingDetailsReportsTab = new RatingDetailReportsTab();
    private ErrorTab errorTab = new ErrorTab();
    private PremiumAndCoveragesTab premiumsAndCoveragesTab = new PremiumAndCoveragesTab();
    private VehicleTab vehicleTab = new VehicleTab();

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM}, description = "Membership Report order validation should be thrown on continue, Tab Out on Reports Tab as well as Premium Calc.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6142")
    public void pas6142_checkNotOrderedMembershipErrors(@Optional("AZ") String state) {

        TestData tdMembershipQuote = getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_SS");
        TestData tdEndorsementStart = getPolicyTD("Endorsement", "TestData_Plus1Month");
        TestData tdMembershipEndorsement = getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_SS_Endorsement");
        TestData tdMembershipEndorsementChanges = getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_SS_Endorsement_Changes");

        String notOrderedMembershipMessage = "You must order the Membership report.";

        mainApp().open();
        createCustomerIndividual();

        // Errors validation during NB Quote
        log.info("Not Ordered Membership Errors Validation for NB Quote Started..");
        policy.initiate();

        // Validating first error condition [NB quote]
        policy.getDefaultView().fillUpTo(tdMembershipQuote, RatingDetailReportsTab.class);
        ratingDetailsReportsTab.getAssetList().fill(getTestSpecificTD("TestData_DontOrderMembership")); // order all reports except Membership before ordering
        ratingDetailsReportsTab.getAssetList().fill(getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_SS")); // order all reports except Membership, and then select No
        validateFirstError(notOrderedMembershipMessage);

        // Validating second error condition [NB quote]
        validateSecondError(notOrderedMembershipMessage);

        log.info("Not Ordered Membership Errors Validation for NB Quote Successfully Completed..");

        // Errors validation during Endorsement Quote
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Started..");
        createPolicy(tdMembershipEndorsement);
        policy.endorse().perform(tdEndorsementStart);

        // Validating first error condition [Endorsement Quote]
        policy.getDefaultView().fillFromTo(tdMembershipEndorsementChanges, GeneralTab.class, RatingDetailReportsTab.class);
        validateFirstError(notOrderedMembershipMessage);

        // Validating second error condition [Endorsement Quote]
        validateSecondError(notOrderedMembershipMessage);

        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Successfully Completed..");

        mainApp().close();
    }

    /*
    Method validates that first type error is being thrown after pressing Continue
    */
    private void validateFirstError(String notOrderedMembershipFirstMessage){
        Tab.buttonNext.click();
        //Changed verify to contains to confirm to AWS PROD mode for regression runs.
	    assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipFirstMessage)).exists();
        errorTab.cancel();
    }

    /*
    Method validates that first type error is being thrown after pressing on other Tab
    */
    private void validateSecondError(String notOrderedMembershipFirstMessage){
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.VEHICLE.get());
        //Changed verify to contains to confirm to AWS PROD mode for regression runs.
	    assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipFirstMessage)).exists();
        errorTab.cancel();
        ratingDetailsReportsTab.saveAndExit();
    }
}