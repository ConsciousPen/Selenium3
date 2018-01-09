package aaa.modules.regression.sales.auto_ca.select;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaSelectBaseTest;
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
 * 2. Initiate new Auto CA quote creation
 * 3. Validate Not Ordered Error after pressing Continue button [NB Quote]
 * 4. Validate Not Ordered Error after pressing on other Tab [NB Quote]
 * 5. Validate Not Ordered Error after pressing Calculate Premium Button [NB Quote]
 * 6. Validate Not Ordered Error after pressing Continue button [Endorsement Quote]
 * 7. Validate Not Ordered Error after pressing on other Tab [Endorsement Quote]
 * @details
 **/
public class TestNotOrderedMembershipError extends AutoCaSelectBaseTest {

    private GeneralTab generalTab = new GeneralTab();
    private MembershipTab membershipTab = new MembershipTab();
    private ErrorTab errorTab = new ErrorTab();
    private PremiumAndCoveragesTab premiumsAndCoveragesTab = new PremiumAndCoveragesTab();
    private VehicleTab vehicleTab = new VehicleTab();

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM}, description = "Membership Report order validation should be thrown on continue, Tab Out on Reports Tab as well as Premium Calc.")
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-6142")
    public void pas6142_checkNotOrderedMembershipErrors(@Optional("CA") String state) {

        TestData tdMembershipQuote = getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_CA");
        TestData tdEndorsementStart = getPolicyTD("Endorsement", "TestData_Plus1Month");
        TestData tdMembershipEndorsement = getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_CA_Endorsement");
        TestData tdMembershipEndorsementChanges = getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_CA_Endorsement_Changes");

        String notOrderedMembershipFirstMessage = "You must order the Membership report.";
        String notOrderedMembershipSecondMessage = "Please order membership report. (AAA_SS171219-yBKH1) [for AAAAzMembershipRepo...";

        mainApp().open();
        createCustomerIndividual();

        // Errors validation during NB Quote
        log.info("Not Ordered Membership Errors Validation for NB Quote Started..");
        policy.initiate();

        // Validating first error condition [NB quote]
        policy.getDefaultView().fillUpTo(tdMembershipQuote, MembershipTab.class);

        validateFirstError(notOrderedMembershipFirstMessage);

        // Validating second error condition [NB quote]
        validateSecondError(notOrderedMembershipFirstMessage);

        // Validating third error condition [NB quote]
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
        vehicleTab.getAssetList().fill(getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_CA")); // order all reports except Membership, and then select No
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipSecondMessage).verify.present();
        errorTab.cancel();
        premiumsAndCoveragesTab.saveAndExit();
        log.info("Not Ordered Membership Errors Validation for NB Quote Successfully Completed..");

        // Errors validation during Endorsement Quote
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Started..");
        createPolicy(tdMembershipEndorsement);
        policy.endorse().perform(tdEndorsementStart);

        // Validating first error condition [Endorsement Quote]
        policy.getDefaultView().fillFromTo(tdMembershipEndorsementChanges, GeneralTab.class, MembershipTab.class);
        validateFirstError(notOrderedMembershipFirstMessage);

        // Validating second error condition [Endorsement Quote]
        validateSecondError(notOrderedMembershipFirstMessage);

        // Validating third error condition [Endorsement Quote]
        PolicySummaryPage.buttonPendedEndorsement.click();
        NavigationPage.comboBoxListAction.setValue("Data Gathering");
        Tab.buttonGo.click();
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.MEMBERSHIP.get());
        membershipTab.getAssetList().fill(getTestSpecificTD("TestData_DontOrderMembership")); // Select 'No' for AAAMembership before ordering
        membershipTab.getAssetList().fill(getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_CA"));
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.PREMIUM_AND_COVERAGES.get());
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipFirstMessage).verify.present();
        errorTab.cancel();
        membershipTab.saveAndExit();
        log.info("Not Ordered Membership Errors Validation for Endorsement Quote Successfully Completed..");

        mainApp().close();
    }

    /*
    Method validates that first type error is being thrown after pressing Continue
    */
    private void validateFirstError(String notOrderedMembershipFirstMessage){
        membershipTab.getAssetList().fill(getTestSpecificTD("TestData_DontOrderMembership")); // Select 'No' for AAAMembership before ordering
        membershipTab.getAssetList().fill(getTestSpecificTD("TestData_NotOrderedMembershipValidationAU_CA"));
        membershipTab.submitTab();
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipFirstMessage).verify.present();
        errorTab.cancel();
    }

    /*
    Method validates that first type error is being thrown after pressing on other Tab
    */
    private void validateSecondError(String notOrderedMembershipFirstMessage){
        NavigationPage.toViewTab(NavigationEnum.AutoCaTab.VEHICLE.get());
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, notOrderedMembershipFirstMessage).verify.present();
        errorTab.cancel();
        membershipTab.saveAndExit();
    }
}
