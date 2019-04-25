package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.common.enums.Constants;
import aaa.common.enums.PrivilegeEnum;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.*;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoCaChoiceBaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import static toolkit.verification.CustomAssertions.assertThat;

@StateList(states = Constants.States.CA)
public class TestAgentAuthorityToOverrideDLRule extends AutoCaChoiceBaseTest {
    private DriverTab driverTab = new DriverTab();
    private ErrorTab errorTab = new ErrorTab();

     /**
     * @author Sreekanth Kopparapu
     * @name Test Agent is able to Override the 10004001 rule when no DL is provided on Driver Page for certain statuses
     * @scenario Precondition: Login with Agent id: qa_roles
     * Login with Agent credentials and create a Customer
     * Create a CA Auto Quote and navigate to Driver Page
     * On the Driver Page choose either of the options: Never Licensed, Not Licensed , Temporarily Suspended and others
     * Calculate Premium
     * Navigate to Dar Page - Can click on Continue or Tab on to DAR page
     * Override Rule is displayed for the Agent and proceed to Bind the Policy
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-26653 Validate Agent Authority to Override Rule for DL 10004001")
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = "PAS-26653")

    public void pas26653_testAgentAuthorityToOverrideDLRuleNB(@Optional("CA") String state) {

        TestData td = getPolicyTD().adjust(TestData.makeKeyPath(driverTab.getMetaKey(), AutoCaMetaData.DriverTab.LICENSE_TYPE.getLabel()), "Licensed (Canadian)")
                .mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()))
                .adjust(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(), AutoCaMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel()),
                        DataProviderFactory.dataOf(AutoCaMetaData.DocumentsAndBindTab.RequiredToIssue.CANADIAN_MVR_FOR_DRIVER.getLabel(),"Yes"));

        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(td, DriverActivityReportsTab.class);
        overrideErrorTab();
        policy.getDefaultView().fillFromTo(td, DriverActivityReportsTab.class, PurchaseTab.class, true);
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Agent is able to Override the 10004001 rule when no DL is provided on Driver Page for certain statuses
     * @scenario Precondition: Login with Agent id: qa_roles
     * Login with Agent credentials and create a Customer
     * Create a CA Auto Quote and navigate to Driver Page
     * On the Driver Page choose either of the options: Never Licensed, Not Licensed , Temporarily Suspended and others
     * Calculate Premium
     * Navigate to Dar Page - Can click on Continue or Tab on to DAR page
     * Override Rule is displayed for the Agent and proceed to Bind the Policy
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-26653 Validate Agent Authority to Override Rule for DL 10004001")
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = "PAS-26653")

    public void pas26653_testAgentAuthorityToOverrideDLRuleEnd(@Optional("CA") String state) {
        String policyNumber = openAppAndCreatePolicy();

        //Open application as non privileged user "A30"
        mainApp().close();
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);

        //Initiate Endorsement Transaction
        searchForPolicy(policyNumber);
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
        addDrivers();
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Test Agent is able to Override the 10004001 rule when no DL is provided on Driver Page for certain statuses
     * @scenario Precondition: Login with Agent id: qa_roles
     * Login with Agent credentials and create a Customer
     * Create a CA Auto Choice Quote and navigate to Driver Page
     * On the Driver Page choose either of the options: Never Licensed, Not Licensed , Temporarily Suspended and others
     * Calculate Premium
     * Navigate to Dar Page - Can click on Continue or Tab on to DAR page
     * Override Rule is displayed for the Agent and proceed to Bind the Policy
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "PAS-26653 Validate Agent Authority to Override Rule for DL 10004001")
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = "PAS-26653")

    public void pas26653_testAgentAuthorityToOverrideDLRuleRen(@Optional("CA") String state) {
        String policyNumber = openAppAndCreatePolicy();

        //Initiate Renewal
        policy.renew().performAndExit();
        mainApp().close();
       //Open application as non privileged user "A30"
        openAppNonPrivilegedUser(PrivilegeEnum.Privilege.A30);
        SearchPage.openPolicy(policyNumber);
        PolicySummaryPage.buttonRenewals.click();
        policy.dataGather().start();
        addDrivers();
    }

    private void overrideErrorTab() {

        if (errorTab.tableErrors.isPresent()) {
            errorTab.overrideErrors(ErrorEnum.Errors.ERROR_AAA_10004001_CA, ErrorEnum.Errors.ERROR_AAA_CAC7100525_CA_CHOICE);
            errorTab.override();
            new PremiumsAndCoveragesQuoteTab().submitTab();
        }
    }

    protected void addDrivers() {

        policy.getDefaultView().fillUpTo(getTestSpecificTD("Add_Driver_Endorsement_CA"), DriverTab.class, true);
        driverTab.submitTab();
        new PremiumAndCoveragesTab().calculatePremium();
        new PremiumAndCoveragesTab().submitTab();
        TestData td2 = getPolicyTD()
                .mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(),
                        AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT_DMV.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DriverActivityReportsTab.class.getSimpleName(),
                        AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()))
                .mask(TestData.makeKeyPath(AutoCaMetaData.DocumentsAndBindTab.class.getSimpleName(),
                        AutoCaMetaData.DocumentsAndBindTab.VEHICLE_INFORMATION.getLabel()));
        policy.getDefaultView().fillFromTo(td2,DriverActivityReportsTab.class, DocumentsAndBindTab.class,true);
        new DocumentsAndBindTab().submitTab();
        assertThat(errorTab.tableErrors.getRowsCount()).isEqualTo(2);
        assertThat(errorTab.tableErrors.getRow(1).getCell("Code").getValue()).isEqualTo(ErrorEnum.Errors.ERROR_AAA_10004001_CA.getCode());
        assertThat(errorTab.tableErrors.getRow(2).getCell("Code").getValue()).isEqualTo(ErrorEnum.Errors.ERROR_AAA_10004001_CA.getCode());
        errorTab.overrideAllErrors();
        errorTab.override();
        new DocumentsAndBindTab().submitTab();
    }

}

