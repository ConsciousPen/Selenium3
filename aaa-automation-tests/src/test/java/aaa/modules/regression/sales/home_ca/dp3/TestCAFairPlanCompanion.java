package aaa.modules.regression.sales.home_ca.dp3;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

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

        defaultPolicyData = buildTD(defaultPolicyData);

        setupHO3Policy(ho3TestData);

        // After Creating HO3 Policy, Begin DP3 Quote.
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());

        // Verify FPCECA now present on Documents Tab & Quote Tab
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        myHelper.verifySelectedEndorsementsPresent(PremiumsAndCoveragesQuoteTab.tableEndorsementForms, PolicyConstants.PolicyEndorsementFormsTable.DESCRIPTION, "FPCECADP");

        // Verify Document Tab populates Endorsement
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.DOCUMENTS.get());
        assertThat(new DocumentsTab().getDocumentsToIssueAssetList().getAsset(HomeCaMetaData.DocumentsTab.DocumentsToIssue.FPCECADP.getLabel()).isPresent()).isTrue();
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

        defaultPolicyData = buildTD(defaultPolicyData);

        setupHO3Policy(ho3TestData);

        createPolicy(defaultPolicyData);

        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, EndorsementTab.class, false);

        myHelper.verifyFPCECAEndorsementAvailable(getPolicyType().getShortName());

        // Click FPCECADP Endorsement
        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());
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
     * @Runtime - 16min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC3_Renewal_VisibleFPCECA(@Optional("") String state) {

        defaultPolicyData = buildTD(defaultPolicyData);

        setupHO3Policy(ho3TestData);

        createPolicy(defaultPolicyData);

        myHelper.handleRenewalTesting(defaultPolicyData);
        policy.getDefaultView().fillUpTo(getTestSpecificTD("Renewal_AC3"), EndorsementTab.class, false);

        myHelper.addFAIRPlanEndorsement(getPolicyType().getShortName());
    }

    private TestData buildTD(TestData in_defaultPolicyData) {
        in_defaultPolicyData = getPolicyTD();
        TestData adjustedDP3ApplicantData = getTestSpecificTD("ApplicantTab_DP3").resolveLinks();
        TestData adjustedDP3ReportsData = getTestSpecificTD("ReportsTab_DP3").resolveLinks();
        in_defaultPolicyData.adjust(ApplicantTab.class.getSimpleName(), adjustedDP3ApplicantData);
        in_defaultPolicyData.adjust(ReportsTab.class.getSimpleName(), adjustedDP3ReportsData);

        return in_defaultPolicyData;
    }

    public void setupHO3Policy(TestData inputHO3TestData) {

        // Open App, Create Customer and Initiate Quote
        mainApp().open();
        createCustomerIndividual();
        inputHO3TestData = getTestSpecificTD("HO3PolicyData");
        createPolicy(inputHO3TestData);
    }
}