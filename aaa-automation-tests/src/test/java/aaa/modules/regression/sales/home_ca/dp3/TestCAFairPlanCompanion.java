package aaa.modules.regression.sales.home_ca.dp3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Table;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * @author Tyrone C Jemison
 * @name Test CA Fair Plan Companion
 */
public class TestCAFairPlanCompanion extends HomeCaDP3BaseTest {
    // Class Variables
    TestData defaultPolicyData;
    TestData ho3TestData;
    HelperCommon myHelper = new HelperCommon();

    /**
     * @scenario
     * 1. Initiate a HO3 Quote.
     * 2. Bind HO3 Quote.
     * 3. Initiate a DP3 Quote.
     * 2. Observe FPCECA is visible.
     * 3. After adding FPCECA, FPCECA removed from Optional Endorsements
     * 4. After Adding FPCECA, FPCECA visible in Documents Tab and Quote Tab.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC1AC4_Quote_VisibleFPCECADP(@Optional("") String state) {

        // Must adjust DP3 Data on Applicant Tab.
        defaultPolicyData = getPolicyTD();
        TestData adjustedDP3ApplicantData = getTestSpecificTD("ApplicantTab_DP3").resolveLinks();
        TestData adjustedDP3ReportsData = getTestSpecificTD("ReportsTab_DP3").resolveLinks();
        defaultPolicyData.adjust(ApplicantTab.class.getSimpleName(), adjustedDP3ApplicantData);
        defaultPolicyData.adjust(ReportsTab.class.getSimpleName(), adjustedDP3ReportsData);
        ho3TestData = getTestSpecificTD("HO3PolicyData");

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        createPolicy(ho3TestData);

        // After Creating HO3 Policy, Begin DP3.
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement("dp3");

        // Verify FPCECA now present on Documents Tab & Quote Tab
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        myHelper.verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms, PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECADP");

        // Verify Document Tab populates Endorsement
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
        assertThat(new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECA.getLabel()).isPresent()).isTrue();
    }

    /**
     * @scenario
     * 1. Create a DP3 Quote.
     * 2. Bind Quote.
     * 3. Initiate Mid-Term Endorsement.
     * 4. Observe FPCECA is visible.
     * 5. After opting to add FPCECA, a message is displayed.
     * 6. Verify message will match mock data.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC2AC5_Endorsement_VisibleFPCECA(@Optional("") String state) {

        TestData endorsementTestData = getTestSpecificTD("Endorsement_AC2AC5").resolveLinks();

        // Must adjust DP3 Data on Applicant Tab.
        defaultPolicyData = getPolicyTD();
        TestData adjustedDP3ApplicantData = getTestSpecificTD("ApplicantTab_DP3").resolveLinks();
        TestData adjustedDP3ReportsData = getTestSpecificTD("ReportsTab_DP3").resolveLinks();
        defaultPolicyData.adjust(ApplicantTab.class.getSimpleName(), adjustedDP3ApplicantData);
        defaultPolicyData.adjust(ReportsTab.class.getSimpleName(), adjustedDP3ReportsData);
        ho3TestData = getTestSpecificTD("HO3PolicyData");

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        createPolicy(ho3TestData);

        createPolicy(defaultPolicyData);

        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, EndorsementTab.class, false);
        myHelper.verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms, PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECADP");

        // Click FPCECADP Endorsement
        myHelper.addFAIRPlanEndorsement("dp3");
    }

    /**
     * @scenario
     * 1. Create a DP3 Quote.
     * 2. Bind Quote.
     * 3. Advance JVM to TP1, run renewal jobs.
     * 4. Advance JVM to TP2, run renewal jobs.
     * 5. Retrieve Renewal Image
     * 6. Observe FPCECA is visible.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC3_Renewal_VisibleFPCECA(@Optional("") String state) {

        // Must adjust DP3 Data on Applicant Tab.
        defaultPolicyData = getPolicyTD();
        TestData adjustedDP3ApplicantData = getTestSpecificTD("ApplicantTab_DP3").resolveLinks();
        TestData adjustedDP3ReportsData = getTestSpecificTD("ReportsTab_DP3").resolveLinks();
        defaultPolicyData.adjust(ApplicantTab.class.getSimpleName(), adjustedDP3ApplicantData);
        defaultPolicyData.adjust(ReportsTab.class.getSimpleName(), adjustedDP3ReportsData);
        ho3TestData = getTestSpecificTD("HO3PolicyData");

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        createPolicy(ho3TestData);

        createPolicy(defaultPolicyData);

        // Open App, Create Customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        String policyNumber = myHelper.createCustomerPolicyReturnPN(getCustomerIndividualTD("DataGather","TestData"), defaultPolicyData);
        mainApp().close();

        // Determine Time Values
        LocalDateTime policyCreationDate = TimeSetterUtil.getInstance().getCurrentTime();
        LocalDateTime policyExpirationDate  = policyCreationDate.plusYears(1);
        LocalDateTime timePoint1 = policyExpirationDate.minusDays(73);
        LocalDateTime timePoint2 = policyExpirationDate.minusDays(59);

        // Move JVM to TP1 (R-73) & run Renewal jobs
        myHelper.moveJVMToDateAndRunRenewalJobs(timePoint1);
        // Move JVM to TP2 (R-59) & run Renewal jobs
        myHelper.moveJVMToDateAndRunRenewalJobs(timePoint2);

        // Open App, get renewal image.
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        policy.renew().start().submit();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_AC3"), EndorsementTab.class, false);

        // Click FPCECADP Endorsement
        myHelper.addFAIRPlanEndorsement("dp3");

    }
}
