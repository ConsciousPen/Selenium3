package aaa.modules.regression.sales.home_ss.ho3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.modules.policy.HomeSSHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Mantas Garsvinskas
 * @name Test Membership Error when Membership is not ordered
 * @scenario
 * 1. Create Customer
 * 2. Initiate new Homeowners HO3 quote creation
 * 3. ...
 * @details
 **/
public class TestNotOrderedMembershipError extends HomeSSHO3BaseTest {

    private ApplicantTab applicantTab = new ApplicantTab();
    private BindTab bindTab = new BindTab();
    private ReportsTab reportsTab = new ReportsTab();
    private ErrorTab errorTab = new ErrorTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private PurchaseTab purchaseTab = new PurchaseTab();

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.MEDIUM}, description = "Membership Report order validation should be thrown on continue, Tab Out on Reports Tab as well as Premium Calc.")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-7524")
    public void testMembershipValidation(@Optional("AZ") String state) {

        TestData td_HO_SS_Membership = getTestSpecificTD("TestData_NotOrderedMembershipValidationHO3");
        String NotOrderedMembershipMessage = "You must order the Membership report.";


        mainApp().open();

        createCustomerIndividual();
        log.info("Quote Creation Started...");

        // NB Quote Membership Validation
        policy.initiate();
        policy.getDefaultView().fillUpTo(td_HO_SS_Membership, ReportsTab.class, true);

        //TODO Asset that error is visible

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        // At this point ErrorTab should appear with required message
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, NotOrderedMembershipMessage).verify.present();

        errorTab.cancel();
        reportsTab.saveAndExit();
        policy.dataGather().start();

        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PROPERTY_INFO.get());
        policy.getDefaultView().fillFromTo(td_HO_SS_Membership, PropertyInfoTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        // TODO premiumsAndCoveragesQuoteTab.calculatePremium(); maybe its not needed???
        // At this point ErrorTab should appear with required message
        errorTab.tableErrors.getRowContains(PolicyConstants.PolicyErrorsTable.MESSAGE, NotOrderedMembershipMessage).verify.present();



    }
}


