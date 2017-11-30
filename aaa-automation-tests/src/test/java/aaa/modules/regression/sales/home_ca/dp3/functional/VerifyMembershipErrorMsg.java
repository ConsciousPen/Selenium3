package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.ErrorEnum;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.modules.policy.HomeCaHO6BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.screenshots.ScreenshotManager;

import java.time.LocalDateTime;

/**
 * @author Tyrone Jemison
 * @contact (tyrone.jemison@csaa.com)
 * @name Verify Membership Error Message
 * @scenario
 * 1.GIVEN an agent binding a transaction at NB/Endorsement/Renewal with an ACTIVE membership Status.
 * 2.WHEN there is NO match of either Last and First and DOB between RMS and Insured/Drivers...
 * 3.THEN the UW eligibility rule fires with error message.
 */
public class VerifyMembershipErrorMsg extends HomeCaDP3BaseTest
{


    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3, testCaseId = "PAS-6052")
    public void TC01_verifyMembershipMsg_NB(@Optional("CA") String state) {

        TestData _td = getTestSpecificTD("TestData");
        boolean bTakeScreenshots = true;

        // Clean Test Data
        //Open App
        mainApp().open();

        //Create Customer using Customer TD
        TestData _tdCleanCustomer = getTestSpecificTD("TestData_CleanCustomer");
        createCustomerIndividual(_tdCleanCustomer);

        //Create Clean Policy
        createPolicy(_td);
        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_DP3_NB_Clean");}
        mainApp().close();

        // ---------------------------------------------------------------------------------------
        // ---------------------------------------------------------------------------------------

        // Dirty Test Data

        // -------------------------------- ALL MISMATCH ----------------------------------
        //Open App
        mainApp().open();

        //Create Customer using Customer TD
        TestData _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_ALL");
        createCustomerIndividual(_tdDirtyCustomer);

        //Initiate Quote. Given Data to Provoke Uw Rule Firing.
        policy.initiate();
        policy.getDefaultView().fillUpTo(_td, BindTab.class, true);
        BindTab _bindTab = new BindTab();
        _bindTab.btnPurchase.click();

        //Verify Membership Mis-Match Error Message
        ErrorTab _errorTab = new ErrorTab();
        _errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);

        //Screenshot Verifies Successful Test
        ScreenshotManager.getInstance().makeScreenshot("HO_DP3_NB_Dirty_ALL");

        //Close App to prevent Memory Leaks.
        mainApp().close();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6052")
    public void TC02_verifyMembershipMsg_Endorsement(@Optional("CA") String state) {

        boolean bTakeScreenshots = true;
        String _persistantPolicyNumber = "";

        //Clean Data
        //Open App
        mainApp().open();

        //Create Customer using Custom Data
        TestData _tdOtherCustomer = getTestSpecificTD("TestData_OtherCustomer");
        TestData _tdCleanCustomer = getTestSpecificTD("TestData_CleanCustomer");
        TestData _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_ALL");
        TestData _td = getTestSpecificTD("TestData_NonMember");

        createCustomerIndividual(_tdCleanCustomer);
        _persistantPolicyNumber = createPolicy(_td);



        //Go to Created Policy.
        SearchPage.openPolicy(_persistantPolicyNumber);

        // Build Test Data to work with
        TestData _myEndoTD = getTestSpecificTD("Endorsement_CleanData");
        // Begin Endorsement. Use Default data for Endorsement Reason Page. Using Custom Data From Adjustment.
        //policy.createEndorsement(_myEndoTD.adjust(getPolicyTD("Endorsement", "TestData")));

        // Creating Second Insured Breaks PAS.
        policy.endorse().perform(_myEndoTD.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(_myEndoTD, PurchaseTab.class, false);
        //policy.getDefaultView().fillFromTo(_myEndoTD, ReportsTab.class, PurchaseTab.class, true);
        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Endo_Clean");}

        mainApp().close();

        // ------------------------------------------------------------
        // ------------------------------------------------------------

        // Dirty Data
        //Open App
        mainApp().open();

        createCustomerIndividual(_tdDirtyCustomer);
        String _persistantPolicyNumber2 = createPolicy(_td);

        //Go to Created Policy.
        SearchPage.openPolicy(_persistantPolicyNumber2);

        // Build Test Data to work with
        TestData _myDirtyEndoTD = getTestSpecificTD("Endorsement_DirtyData");

        // Begin Endorsement. Use Default data for Endorsement Reason Page. Using Custom Data From Adjustment.
        policy.endorse().perform(_myDirtyEndoTD.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(_myDirtyEndoTD, PurchaseTab.class, false);

        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Endo_Dirty");}

        //Verify Membership Mis-Match Error Message
        ErrorTab _errorTab = new ErrorTab();
        _errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);

        mainApp().close();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6052")
    public void TC03_verifyMembershipMsg_Renewal(@Optional("CA") String state) {

        boolean bTakeScreenshots = true;
        String _persistantPolicyNumber_Clean = "";
        String _persistantPolicyNumber_Dirty = "";

        //Clean Data
        //Open App
        mainApp().open();

        //Create Customer using Custom Data
        TestData _tdCleanCustomer = getTestSpecificTD("TestData_CleanCustomer");
        TestData _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_ALL");
        TestData _td = getTestSpecificTD("TestData_NonMember");

        createCustomerIndividual(_tdCleanCustomer);
        _persistantPolicyNumber_Clean = createPolicy(_td);
        LocalDateTime policyExpirationDate_CleanPolicy = PolicySummaryPage.getExpirationDate();
        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_CleanSetup");}

        createCustomerIndividual(_tdDirtyCustomer);
        _persistantPolicyNumber_Dirty = createPolicy(_td);
        LocalDateTime policyExpirationDate_DirtyPolicy = PolicySummaryPage.getExpirationDate();
        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_DirtySetup");}

        mainApp().close();

        //Move JVM Time Forward & Save Original Date
        LocalDateTime originalDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime renewImageGenDate = getTimePoints().getRenewImageGenerationDate(policyExpirationDate_CleanPolicy);
        TimeSetterUtil.getInstance().nextPhase(renewImageGenDate);
        //Run Jobs
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1,true);
        //HttpStub.executeAllBatches();
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2,true);

        // Do Renewals
        //Clean
        mainApp().open();
        SearchPage.openPolicy(_persistantPolicyNumber_Clean);
        //If Renew Image is Available, click it.
        PolicySummaryPage.buttonRenewals.verify.enabled();
        PolicySummaryPage.buttonRenewals.click();
        //Go into DataGathering Mode to Fill Out Tabs.
        policy.dataGather().start();
        policy.getDefaultView().fill(getTestSpecificTD("TestData_AddRenewal"));
        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_Clean");}


        SearchPage.openPolicy(_persistantPolicyNumber_Dirty);
        //If Renew Image is Available, click it.
        PolicySummaryPage.buttonRenewals.verify.enabled();
        PolicySummaryPage.buttonRenewals.click();
        //Go into DataGathering Mode to Fill Out Tabs.
        policy.dataGather().start();
        policy.getDefaultView().fill(getTestSpecificTD("TestData_AddRenewal"));
        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_Dirty");}

        // Move JVM Back to Original Date and Close App.
        mainApp().close(); //Close App before NextPhase
        TimeSetterUtil.getInstance().nextPhase(originalDate);
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6052")
    public void TCXX_UnitTest(@Optional("CA") String state) {
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1, true);

    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6052")
    public void TCXX_UnitTest_ManualRenew(@Optional("CA") String state) {

        boolean bTakeScreenshots = true;
        String _persistantPolicyNumber_Clean = "";
        String _persistantPolicyNumber_Dirty = "";

        //Clean Data
        //Open App
        mainApp().open();

        //Create Customer using Custom Data
        TestData _tdCleanCustomer = getTestSpecificTD("TestData_CleanCustomer");
        TestData _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_ALL");
        TestData _td = getTestSpecificTD("TestData_NonMember");

        createCustomerIndividual(_tdCleanCustomer);
        _persistantPolicyNumber_Clean = createPolicy(_td);
        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_CleanSetup");}

        createCustomerIndividual(_tdDirtyCustomer);
        _persistantPolicyNumber_Dirty = createPolicy(_td);
        LocalDateTime policyExpirationDate_DirtyPolicy = PolicySummaryPage.getExpirationDate();
        if (bTakeScreenshots){ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_DirtySetup");}

        mainApp().close();
    }
}
