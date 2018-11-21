package aaa.modules.regression.sales.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.modules.policy.HomeSSHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
//TODO: This is fixed and passing
/**
 * @author Mantas Garsvinskas
 * @name Test Membership Error when Membership is not ordered during New Business (NB) and during Endorsement
 * @scenario
 * 1. Create Customer
 * 2. Initiate new Homeowners SS HO3 quote creation
 * 3. Validate Not Ordered Error after pressing Continue button [NB Quote]
 * 4. Validate Not Ordered Error after pressing on other Tab [NB Quote]
 * 5. Validate Not Ordered Error after pressing Continue button [Endorsement Quote]
 * 6. Validate Not Ordered Error after pressing on other Tab [Endorsement Quote]
 * @details
 **/
@StateList(states = Constants.States.AZ)
public class TestNotOrderedMembershipError extends HomeSSHO3BaseTest {

    private ReportsTab reportsTab = new ReportsTab();
    private ErrorTab errorTab = new ErrorTab();

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM}, description = "Membership Report order validation should be thrown on continue, Tab Out on Reports Tab as well as Premium Calc.")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-7524")
    public void pas7524_checkNotOrderedMembershipErrors(@Optional("AZ") String state) {
        TestData tdMembershipQuote = getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3");
        TestData tdEndorsementStart = getPolicyTD("Endorsement", "TestData_Plus1Month");
        TestData tdMembershipEndorsement = getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3_Endorsement");

        String notOrderedMembershipFirstMessage = "Member Since Date must be entered";
        String notOrderedMembershipSecondMessage = "You must order the Membership report.";

        mainApp().open();
        createCustomerIndividual();

        // Errors validation during NB Quote
        log.info("Not Ordered Membership Errors Validation for NB Quote Started..");
        policy.initiate();

        // Validating first error condition [NB quote]
        policy.getDefaultView().fillUpTo(tdMembershipQuote, ReportsTab.class);
        reportsTab.getAssetList().fill(getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3"));
        reportsTab.submitTab();
        //Modifying verify to contains to confirm to AWS PROD mode for regression runs.
        assertThat(reportsTab.getAssetList().getAsset(HomeSSMetaData.ReportsTab.WARNING_MESSAGE_BOX)).valueContains(notOrderedMembershipFirstMessage);

        // Validating second error condition [NB quote]
        validateSecondError(notOrderedMembershipSecondMessage);

        // Validating third error condition [NB quote]
        policy.dataGather().start();
        validateSecondError(notOrderedMembershipSecondMessage);
        log.info("Not Ordered Membership Errors Validation for NB Quote Successfully Completed..");

        // Errors validation during Endorsement Quote
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Started..");
        createPolicy(tdMembershipEndorsement);
        policy.endorse().perform(tdEndorsementStart);

        // Validating first error condition [Endorsement Quote]
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        policy.getDefaultView().fillFromTo(tdMembershipQuote, ApplicantTab.class, ReportsTab.class);
        reportsTab.submitTab();
        //Modifying verify to contains to confirm to AWS PROD mode for regression runs.
        assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipSecondMessage)).exists();
        errorTab.cancel();

        // Validating second error condition [Endorsement Quote]
        validateSecondError(notOrderedMembershipSecondMessage);

        policy.endorse().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        //Modifying verify to contains to confirm to AWS PROD mode for regression runs.
        assertThat(errorTab.tableErrors.getRow(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipSecondMessage)).exists();
        errorTab.cancel();
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Successfully Completed..");
        mainApp().close();
    }

    /*
    Method validates that second type error is being thrown after pressing on other Tab
    */
    private void validateSecondError(String notOrderedMembershipSecondMessage){
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        //Modifying verify to contains to confirm to AWS PROD mode for regression runs.
        assertThat(errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipSecondMessage)).exists();
        errorTab.cancel();
        reportsTab.saveAndExit();
    }
}
