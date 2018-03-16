package aaa.modules.regression.document_fulfillment.auto_ss.functional;

import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.product.ProductRenewalsVerifier;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractAutoSS;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

import java.time.LocalDateTime;

import static java.util.Arrays.asList;

public class TestCinRenewalAutoSS extends TestCinAbstractAutoSS{
    /**
     * @name Test CIN Document generation (MVR activity)
     *
     * Depends on ChoicePointMvrMockData mocksheet was updated with Applicant: First Name: MvrChargeable Last Name: Activity
     *
     * @scenario
     * 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal with Name Insured having chargeable MVR property violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7515")
    public void testCinMVR(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD()
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAProductOwned"));

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_MVR").resolveLinks()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_940"));

        renewPolicy(policyNumber, renewalTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        verifyCinGenerated(cinDocument, policyNumber);
    }

    /**
     * @name Test CIN Document generation (CLUE activity)
     *
     * Depends on ChoicePointClueMockData mocksheet was updated with Applicant: First Name: ClueChargeable Last Name: Activity
     *
     * @param state any except MD, CO
     * @scenario
     * 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal with Name Insured having chargeable CLUE property violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7515")
    public void testCinCLUE(@Optional("AZ") String state) {
        assertStateNotEquals(state, "MD", "CO");
        TestData policyTD = getPolicyDefaultTD()
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAProductOwned"));

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_CLUE").resolveLinks()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_940"));

        renewPolicy(policyNumber, renewalTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        verifyCinGenerated(cinDocument, policyNumber);
    }

    /*******************************
     *
     * INSURANCE SCORE TRIGGERS
     *
     *******************************/

    /**
     * Tests Insurance Score trigger
     * CIN document should be generated if insurance score was re-ordered and the new score was better but not in the best band
     *
     * @param state any except MD, CO has its own test
     * @scenario
     * 1. Issue a NB policy (score 650)
     * 2. Initiate renewal
     * 3. Add a new driver who has a better insurance score
     * 4. Reorder insurance score report (best score changes to 840) + reorder clue report
     * 5. Do renewal proposal
     * 6. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testNewDriverBetterScore(@Optional("AZ") String state) {
        assertStateNotEquals(state, "MD", "CO");

        TestData policyTD = getPolicyDefaultTD()
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAProductOwned"));

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_CLUE").resolveLinks()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("NamedInsured_Score_840"));

        renewPolicy(policyNumber, renewalTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        verifyCinGenerated(cinDocument, policyNumber);

//        PolicyCINTestingTemplate test = getTestInstance();
//        test.createPolicyForTest();
//        test.loadTestData(TEST_DATA_RENEWAL);
//        ScenarioAdjustments betterScoreAdjustments = new ScenarioAdjustments()
//                .merge(GENERAL_TAB, "NamedInsured_Score_840")
//                .merge(TEST_DATA, "Driver_Score_840")
//                .merge(RATING_DETAILS_REPORTS_TAB, "Reorder_InsuranceScore")
//                .merge(ERROR_TAB, "ErrorTab_ForeignLicense")
//                .merge(DRIVER_ACTIVITY_REPORTS_TAB, "Reorder_CLUE");
//
//        if (asList("UT").contains(state)) {
//            betterScoreAdjustments.merge(ERROR_TAB, "ErrorTab_ForeignLicense");
//        }
//
//        test.adjustPolicyTestData(betterScoreAdjustments);
//        test.renewPolicy();
//        test.verifyCINIsGenerated();
//        test.verifyDisplayPremiumDifferenceIsNotGenerated();
//        test.verifyDocumentOrder(getDocumentSequence(AUTO + state + RENEWAL + GENERAL_SCENARIO));
    }

    /**
     * Tests Insurance Score trigger
     * CIN document should be generated if insurance score was re-ordered and the new score was better but not in the best band
     *
     * @param state any except MD, CO has its own test
     * @scenario
     * 1. Issue a NB policy (score 650)
     * 2. Initiate renewal
     * 3. Update NI first name so that we are forced to reorder insurance score
     * 4. Reorder insurance score report (score changes to 840) + reorder clue report
     * 5. Do renewal proposal
     * 6. Verify that CIN document is generated
     */
//    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
//    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
//    @Parameters({STATE_PARAM})
//    public void testReorderBetterScore(@Optional("AZ") String state) {
//        assertStateNotEquals(state, "MD", "CO");
//        PolicyCINTestingTemplate test = getTestInstance();
//        test.adjustPolicyTestData(DISABLE_MEMBERSHIP);
//        test.createPolicyForTest();
//        test.loadTestData(TEST_DATA_RENEWAL);
//        ScenarioAdjustments betterScoreAdjustments = new ScenarioAdjustments()
//                .merge(GENERAL_TAB, "Update_FNI_840")
//                .merge(PRODUCT_OWNED, "Membership_NI_840")
//                .merge(RATING_DETAILS_REPORTS_TAB, "Reorder_InsuranceScore")
//                .merge(DRIVER_ACTIVITY_REPORTS_TAB, "Reorder_CLUE");
//
//        test.adjustPolicyTestData(betterScoreAdjustments);
//        test.renewPolicy();
//        test.verifyCINIsGenerated();
//        test.verifyDisplayPremiumDifferenceIsNotGenerated();
//        test.verifyDocumentOrder(getDocumentSequence(AUTO + state + RENEWAL + GENERAL_SCENARIO));
//    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }
}
