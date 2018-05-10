package aaa.modules.regression.sales.home_ca.ho3;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestCAFairPlanCanItBind extends HomeCaHO3BaseTest {
    static TestData DEFAULTPOLICYDATA;

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC1_Quote_HighFL_YesFPCECA_Bind(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(DEFAULTPOLICYDATA, EndorsementTab.class, false);

        // TODO: Add FPCECA Endorsement
        // TODO: Complete Bind Quote
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC2_Quote_LowFL_YesFPCECA_NoBind(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(DEFAULTPOLICYDATA, EndorsementTab.class, false);

        // TODO: Add FPCECA Endorsement
        // TODO: Attempt to Complete Bind Quote
        // TODO: Verify cannot bind.
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC3_Quote_LowFL_NoFPCECA_NoBind(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(DEFAULTPOLICYDATA, EndorsementTab.class, false);

        // TODO: Attempt to Complete Bind Quote
        // TODO: Verify cannot bind.
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC4_Endorsement_HighFL_YesFPCECA_Purchase(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        TestData endorsementTestData = getTestSpecificTD("Endorsement_AC2AC5").resolveLinks();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        createPolicy(DEFAULTPOLICYDATA);
        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, PremiumsAndCoveragesQuoteTab.class, false);

        // TODO: Add FPCECA Endorsement
        // TODO: Purchase Endorsement
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC5_Endorsement_LowFL_YesFPCECA_NoPurchase(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        TestData endorsementTestData = getTestSpecificTD("Endorsement_AC2AC5").resolveLinks();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        createPolicy(DEFAULTPOLICYDATA);
        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, PremiumsAndCoveragesQuoteTab.class, false);

        // TODO: Add FPCECA Endorsement
        // TODO: Attempt to Complete Bind Quote
        // TODO: Verify cannot bind.
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC6_Endorsement_LowFL_NoFPCECA_NoPurchase(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        TestData endorsementTestData = getTestSpecificTD("Endorsement_AC2AC5").resolveLinks();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        createPolicy(DEFAULTPOLICYDATA);
        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, PremiumsAndCoveragesQuoteTab.class, false);

        // TODO: Attempt to Complete Bind Quote
        // TODO: Verify cannot bind.
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC7_Quote_3FL_WoodRoof_YesFPCECA_YesBind(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();

        // TODO: Add FPCECA Endorsement
        // TODO: Attempt to Complete Bind Quote
        // TODO: Verify cannot bind.
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC8_Quote_3FL_WoodRoof_NoFPCECA_NoBind(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        createPolicy(DEFAULTPOLICYDATA);

        // TODO: Attempt to Complete Bind Quote
        // TODO: Verify cannot bind.
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC9_Quote_ZipMatch_YesFPCECA_YesBind(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        createPolicy(DEFAULTPOLICYDATA);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC10_Quote_ZipMatch_NoFPCECA_NoBind(@Optional("") String state) {
        DEFAULTPOLICYDATA = getPolicyTD();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        // TODO: Attempt to Bind.
        // TODO: Verify Cannot Bind.
    }
}
