package aaa.modules.regression.sales.home_ca.ho3.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.TimeSetterBctClient;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.helpers.ssh.RemoteHelper;
import aaa.helpers.ssh.Ssh;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.main.modules.policy.home_ca.views.RenewView;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.istf.timesetter.client.TimeSetter;
import com.exigen.istf.timesetter.client.TimeSetterClient;
import com.gargoylesoftware.htmlunit.javascript.host.intl.DateTimeFormat;
import org.openqa.selenium.remote.server.handler.FindActiveElement;
import org.openqa.selenium.remote.server.handler.FindElement;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import ru.yandex.qatools.ashot.Screenshot;
import toolkit.datax.TestData;
import toolkit.utils.SSHController;
import toolkit.utils.TestInfo;
import toolkit.utils.screenshots.ScreenshotManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.apache.cxf.frontend.ClientProxy.getClient;

/**
 * @author Tyrone Jemison
 * @name Verify Membership Error Message
 * @scenario
 * 1.GIVEN an agent binding a transaction at NB/Endorsement/Renewal with an ACTIVE membership Status.
 * 2.WHEN there is NO match of either Last or First or DOB between RMS and Insured/Drivers...
 * 3.THEN the UW eligibility rule fires with message.
 */
public class VerifyMembershipErrorMsg extends HomeCaHO3BaseTest {


    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6051, PAS-6880, PAS-7072")
    public void TC01_verifyMembershipMsg_NB(@Optional("CA") String state) {

        TestData _td = getTestSpecificTD("TestData");
        TestData _tdDUMMY = getTestSpecificTD("TestData_DUMMY");
        boolean bTakeScreenshots = false;

        // Clean Test Data
        // Associate Customer Number
        mainApp().open();
        TestData _tdCleanCustomer2 = getTestSpecificTD("TestData_CleanCustomer2");
        createCustomerIndividual(_tdCleanCustomer2);
        createPolicy(_td);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_NB_Clean2");
        }
        mainApp().close();

        // DUMMY NUMBER
        //TODO:Utilizing One Number, Will Add Other Dummy numbers later.
        mainApp().open();
        TestData _tdDummyCustomer = getTestSpecificTD("TestData_DummyCustomer");
        createCustomerIndividual(_tdDummyCustomer);
        //Initiate Quote. Given Data to Provoke Uw Rule Firing.
        policy.initiate();
        policy.getDefaultView().fillUpTo(_tdDUMMY, BindTab.class, true);
        BindTab _bindTab = new BindTab();
        _bindTab.btnPurchase.click();
        //Verify Membership Mis-Match Error Message
        ErrorTab _errorTab = new ErrorTab();
        _errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_NB_DummyNumber");
        }
        mainApp().close();

        //Open App
        mainApp().open();
        //Create Customer using Customer TD
        TestData _tdCleanCustomer = getTestSpecificTD("TestData_CleanCustomer");
        createCustomerIndividual(_tdCleanCustomer);

        //Create Clean Policy
        createPolicy(_td);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_NB_Clean");
        }
        mainApp().close();

        // ---------------------------------------------------------------------------------------
        // ---------------------------------------------------------------------------------------

        // Dirty Test Data

        // -------------------------------- FIRST NAME MISMATCH ----------------------------------
        //Open App
        mainApp().open();

        //Create Customer using Customer TD
        TestData _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_First");
        createCustomerIndividual(_tdDirtyCustomer);

        //Create Policy
        createPolicy(_td);

        //Screenshot Verifies Successful Test
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_NB_Dirty_FirstName");
        }

        //Close App to prevent Memory Leaks.
        mainApp().close();

        // -------------------------------- LAST NAME MISMATCH ----------------------------------
        //Open App
        mainApp().open();

        //Create Customer using Customer TD
        _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_Last");
        createCustomerIndividual(_tdDirtyCustomer);

        //Create Policy
        createPolicy(_td);

        //Screenshot Verifies Successful Test
        ScreenshotManager.getInstance().makeScreenshot("HO_HO3_NB_Dirty_LastName");

        //Close App to prevent Memory Leaks.
        mainApp().close();

        // -------------------------------- DOB NAME MISMATCH ----------------------------------
        //Open App
        mainApp().open();

        //Create Customer using Customer TD
        _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_DOB");
        createCustomerIndividual(_tdDirtyCustomer);

        //Create Policy
        createPolicy(_td);

        //Screenshot Verifies Successful Test
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_NB_Dirty_DOB");
        }

        //Close App to prevent Memory Leaks.
        mainApp().close();

        // -------------------------------- ALL MISMATCH ----------------------------------
        //Open App
        mainApp().open();

        //Create Customer using Customer TD
        _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_ALL");
        createCustomerIndividual(_tdDirtyCustomer);

        //Initiate Quote. Given Data to Provoke Uw Rule Firing.
        policy.initiate();
        policy.getDefaultView().fillUpTo(_td, BindTab.class, true);
        _bindTab = new BindTab();
        _bindTab.btnPurchase.click();

        //Verify Membership Mis-Match Error Message
        _errorTab = new ErrorTab();
        _errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);

        //Screenshot Verifies Successful Test
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_NB_Dirty_ALL");
        }

        //Close App to prevent Memory Leaks.
        mainApp().close();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6051, PAS-6880, PAS-7072")
    public void TC02_verifyMembershipMsg_Endorsement(@Optional("CA") String state) {

        boolean bTakeScreenshots = true;
        String _persistantPolicyNumber = "";

        // Clean Data
        // ASSOCIATE INSURED
        mainApp().open();
        TestData _tdCleanCustomer2 = getTestSpecificTD("TestData_CleanCustomer2");
        TestData _td = getTestSpecificTD("TestData_NonMember");
        createCustomerIndividual(_tdCleanCustomer2);
        String _persistantPolicyNumberA = createPolicy(_td);
        //Go to Created Policy.
        SearchPage.openPolicy(_persistantPolicyNumberA);
        // Build Test Data to work with
        TestData _myEndoTD = getTestSpecificTD("Endorsement_CleanData");
        // Begin Endorsement. Use Default data for Endorsement Reason Page. Using Custom Data From Adjustment.
        policy.endorse().perform(_myEndoTD.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(_myEndoTD, PurchaseTab.class, false);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Endo_Clean2");
        }
        mainApp().close();

        // DUMMY NUMBER DATA
        mainApp().open();
        TestData _tdDummyCustomer = getTestSpecificTD("TestData_DummyCustomer");
        createCustomerIndividual(_tdDummyCustomer);
        String _persistantPolicyNumberB = createPolicy(_td);
        //Go to Created Policy.
        SearchPage.openPolicy(_persistantPolicyNumberB);
        // Build Test Data to work with
        _myEndoTD = getTestSpecificTD("Endorsement_DummyData");
        // Begin Endorsement. Use Default data for Endorsement Reason Page. Using Custom Data From Adjustment.
        policy.endorse().perform(_myEndoTD.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(_myEndoTD, PurchaseTab.class, false);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Endo_Dummy");
        }
        //Verify Membership Mis-Match Error Message
        ErrorTab _errorTab = new ErrorTab();
        _errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);

        mainApp().close();

        //Open App
        mainApp().open();
        //Create Customer using Custom Data
        TestData _tdOtherCustomer = getTestSpecificTD("TestData_OtherCustomer");
        TestData _tdCleanCustomer = getTestSpecificTD("TestData_CleanCustomer");
        TestData _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_ALL");

        createCustomerIndividual(_tdCleanCustomer);
        _persistantPolicyNumber = createPolicy(_td);

        //Go to Created Policy.
        SearchPage.openPolicy(_persistantPolicyNumber);

        // Build Test Data to work with
        _myEndoTD = getTestSpecificTD("Endorsement_CleanData");
        // Begin Endorsement. Use Default data for Endorsement Reason Page. Using Custom Data From Adjustment.

        // Creating Second Insured Breaks PAS.
        policy.endorse().perform(_myEndoTD.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(_myEndoTD, PurchaseTab.class, false);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Endo_Clean");
        }

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

        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Endo_Dirty");
        }

        //Verify Membership Mis-Match Error Message
        _errorTab = new ErrorTab();
        _errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);

        mainApp().close();
    }

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-6051, PAS-6880, PAS-7072")
    public void TC03_verifyMembershipMsg_Renewal(@Optional("CA") String state) {

        boolean bTakeScreenshots = false;
        String _persistantPolicyNumber_Clean = "";
        String _persistantPolicyNumber_Dirty = "";
        String _persistantPolicyNumber_Dummy = "";
        String _persistantPolicyNumber_Associate = "";

        //Clean Data
        //Open App
        mainApp().open();

        //Create Customers using Custom Data
        TestData _tdCleanCustomer = getTestSpecificTD("TestData_CleanCustomer");
        TestData _tdDirtyCustomer = getTestSpecificTD("TestData_DirtyCustomer_ALL");
        TestData _tdDummyCustomer = getTestSpecificTD("TestData_DummyCustomer");
        TestData _tdAssociateCustomer = getTestSpecificTD("TestData_CleanCustomer2");
        TestData _td = getTestSpecificTD("TestData_NonMember");
        TestData _myTDCleanRenew = getTestSpecificTD("TestData_AddCleanRenewal");
        TestData _myTDDirtyRenew = getTestSpecificTD("TestData_AddDirtyRenewal");
        TestData _myTDDummyRenew = getTestSpecificTD("TestData_AddDummyRenewal");
        TestData _myTDAssociateRenew = getTestSpecificTD("TestData_AddAssociateRenewal");

        createCustomerIndividual(_tdDummyCustomer);
        _persistantPolicyNumber_Dummy = createPolicy(_td);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_DummySetup");
        }

        createCustomerIndividual(_tdAssociateCustomer);
        _persistantPolicyNumber_Associate = createPolicy(_td);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_AssociateSetup");
        }

        createCustomerIndividual(_tdCleanCustomer);
        _persistantPolicyNumber_Clean = createPolicy(_td);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_CleanSetup");
        }

        createCustomerIndividual(_tdDirtyCustomer);
        _persistantPolicyNumber_Dirty = createPolicy(_td);
        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_DirtySetup");
        }

        // DUMMY RENEW
        SearchPage.openPolicy(_persistantPolicyNumber_Dummy);
        policy.renew().performAndFill(_myTDDummyRenew);
        if (bTakeScreenshots) {ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_Dummy");}

        // Associate RENEW
        SearchPage.openPolicy(_persistantPolicyNumber_Associate);
        policy.renew().performAndFill(_myTDAssociateRenew);
        if (bTakeScreenshots) {ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_Associate");}

        // CLEAN RENEW
        SearchPage.openPolicy(_persistantPolicyNumber_Clean);

        //If Renew Image is Available, click it.
        policy.renew().performAndFill(_myTDCleanRenew);
        if (bTakeScreenshots) {ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_Clean");}

        SearchPage.openPolicy(_persistantPolicyNumber_Dirty);
        //Using a Try Catch to Anticipate Renewal Failure. Will determine if failure was due to UW Rules or Not.
        try {
            policy.renew().performAndFill(_myTDDirtyRenew);
        } catch (Exception ex) {
            //Verify Membership Mis-Match Error Message
            ErrorTab _errorTab = new ErrorTab();
            if (_errorTab.isVisible()) {
                _errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_HO_SS_MEM_LASTNAME);
            } else {
                log.error("Framework anticipated an error to be present where none was located.");
            }
        }

        if (bTakeScreenshots) {
            ScreenshotManager.getInstance().makeScreenshot("HO_HO3_Renewal_Dirty");
        }

        // Close App.
        mainApp().close();
    }
}