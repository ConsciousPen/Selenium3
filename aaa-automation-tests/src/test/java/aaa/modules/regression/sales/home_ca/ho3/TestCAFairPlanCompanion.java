package aaa.modules.regression.sales.home_ca.ho3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ca.defaulttabs.EndorsementTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.webdriver.controls.composite.table.Table;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

/**
 * @author Tyrone C Jemison
 * @name Test CA Fair Plan Companion
 */
public class TestCAFairPlanCompanion extends HomeCaHO3BaseTest {
    // Class Variables
    TestData defaultPolicyData;
    HelperCommon myHelper = new HelperCommon();

    /**
     * @scenario
     * 1. Initiate a HO3 Quote.
     * 2. Observe FPCECA is visible.
     * 3. After adding FPCECA, FPCECA removed from Optional Endorsements
     * 4. After Adding FPCECA, FPCECA visible in Documents Tab and Quote Tab.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC1AC4_Quote_VisibleFPCECA(@Optional("") String state) {
        defaultPolicyData = getPolicyTD();

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        addEndorsement();

        // Verify FPCECA now present on Documents Tab & Quote Tab
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms, PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECA");

        // Verify Document Tab populates Endorsement
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
        assertThat(new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECA.getLabel()).isPresent()).isTrue();
    }

    /**
     * @scenario
     * 1. Create a HO3 Quote.
     * 2. Bind Quote.
     * 3. Initiate Mid-Term Endorsement.
     * 4. Observe FPCECA is visible.
     * 5. After opting to add FPCECA, a message is displayed.
     * 6. Verify message will match mock data.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC2AC5_Endorsement_VisibleFPCECA(@Optional("") String state) {

        defaultPolicyData = getPolicyTD();
        TestData endorsementTestData = getTestSpecificTD("Endorsement_AC2AC5").resolveLinks();

        // Open App, Create customer and Policy.
        mainApp().open();
        createCustomerIndividual();
        createPolicy(defaultPolicyData);
        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, EndorsementTab.class, false);
        verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms, PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECA");

        // Click FPCECA Endorsement
        addEndorsement();
    }

    /**
     * @scenario
     * 1. Create a HO3 Quote.
     * 2. Bind Quote.
     * 3. Advance JVM to TP1, run renewal jobs.
     * 4. Advance JVM to TP2, run renewal jobs.
     * 5. Retrieve Renewal Image
     * 6. Observe FPCECA is visible.
     * @param state
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void AC3_Renewal_VisibleFPCECA(@Optional("") String state) {

        defaultPolicyData = getPolicyTD();

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
        moveJVMToDateAndRunRenewalJobs(timePoint1);
        // Move JVM to TP2 (R-59) & run Renewal jobs
        moveJVMToDateAndRunRenewalJobs(timePoint2);

        // Open App, get renewal image.
        mainApp().open();
        SearchPage.openPolicy(policyNumber);

        policy.renew().start().submit();
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_AC3"), EndorsementTab.class, false);
        verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms, PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECA");

        // Click FPCECA Endorsement
        addEndorsement();
    }

    public static void moveJVMToDateAndRunRenewalJobs(LocalDateTime desiredJVMLocalDateTime)
    {
        LocalDateTime policyCreationDate = TimeSetterUtil.getInstance().getCurrentTime();
        printToDebugLog(" -- Current Date = " + policyCreationDate + ". Moving JVM to input time = "
                + desiredJVMLocalDateTime.toString() + " -- ");

        // Advance JVM to Generate Renewal Image.
        TimeSetterUtil.getInstance().nextPhase(desiredJVMLocalDateTime);
        printToDebugLog("Current Date is now = " + TimeSetterUtil.getInstance().getCurrentTime());

        JobUtils.executeJob(Jobs.aaaBatchMarkerJob);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        printToDebugLog(" -- Renewal Offer Generation Jobs Completed -- ");
    }

    private void verifySelectedEndorsementsPresent(Table tableForms, String columnName, String endorsementToFind) {
        assertThat(tableForms.getRowContains(columnName, endorsementToFind)).isNotNull();
    }

    private void addEndorsement() {
        // Click FPCECA Endorsement
        EndorsementTab endorsementTab = new EndorsementTab();
        endorsementTab.getAddEndorsementLink(HomeCaMetaData.EndorsementTab.FPCECA.getLabel()).click();

        // Verify Endorsement Confirmation Appears
        Page.dialogConfirmation.confirm();
        endorsementTab.btnSaveForm.click();
    }
}
