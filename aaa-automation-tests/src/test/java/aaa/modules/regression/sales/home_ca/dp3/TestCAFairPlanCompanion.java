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
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import aaa.modules.regression.sales.template.AbstractFAIRPlanTestMethods;
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
public class TestCAFairPlanCompanion extends AbstractFAIRPlanTestMethods {
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

        defaultPolicyData = adjustDP3TestData(defaultPolicyData);
        setupHO3policy(ho3TestData);

        // After Creating HO3 Policy, Begin DP3 Quote.
        policy.initiate();
        policy.getDefaultView().fillUpTo(defaultPolicyData, EndorsementTab.class, false);

        // Click FPCECA Endorsement
        myHelper.addFAIRPlanEndorsement("dp3");

        // Verify FPCECA now present on Documents Tab & Quote Tab
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        verifyEndorsementAvailable("dp3");

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

        defaultPolicyData = adjustDP3TestData(defaultPolicyData);
        setupHO3policy(ho3TestData);
        createPolicy(defaultPolicyData);

        policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        policy.getDefaultView().fillUpTo(endorsementTestData, EndorsementTab.class, false);
        verifyEndorsementAvailable("dp3");

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
     * @Runtime - 16min
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_DP3)
    public void AC3_Renewal_VisibleFPCECA(@Optional("") String state) {

        defaultPolicyData = adjustDP3TestData(defaultPolicyData);
        setupHO3policy(ho3TestData);
        createPolicy(defaultPolicyData);

        handleRenewalTesting(defaultPolicyData);

        myHelper.addFAIRPlanEndorsement("dp3");
    }
}
