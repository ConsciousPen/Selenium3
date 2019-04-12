package aaa.modules.regression.document_fulfillment.auto_ss.functional;

import static java.util.Arrays.asList;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.document_fulfillment.template.functional.TestCinAbstractAutoSS;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.ETCSCoreSoftAssertions;

public class TestCinRenewalAutoSS extends TestCinAbstractAutoSS {
    /**
     * @name Test CIN Document generation (MVR activity)
     * <p>
     * Depends on ChoicePointMvrMockData mocksheet was updated with Applicant: First Name: MvrChargeable Last Name: Activity
     * @scenario 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal with Name Insured having chargeable MVR property violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.DOCGEN, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7515")
    public void testCinMVR(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD();

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_MVR").resolveLinks()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_940"));

        renewPolicy(policyNumber, renewalTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER)).isNotNull();
        });
    }

    /**
     * @param state any except MD, CO
     * @name Test CIN Document generation (CLUE activity)
     * <p>
     * Depends on ChoicePointClueMockData mocksheet was updated with Applicant: First Name: ClueChargeable Last Name: Activity
     * @scenario 1. Create Customer
     * 2. Create Policy
     * 3. Change time to R-35
     * 4. Create Renewal with Name Insured having chargeable CLUE property violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA, States.CO, States.MD})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7515")
    public void testCinCLUE(@Optional("AZ") String state) {
        Assertions.assertThat(asList("MD", "CO").contains(state)).as("Test does not support this state: " + state).isFalse();
        TestData policyTD = getPolicyDefaultTD();

        String policyNumber = createPolicy(policyTD);

        TestData renewalTD = getTestSpecificTD("TestData_CLUE").resolveLinks()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_940"));

        //Workaround with Do NotRenew action added
        TestData doNotRenewTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DoNotRenew", "TestData");
        
        renewPolicy(policyNumber, renewalTD, doNotRenewTD);

        //Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, true);

        // SoftAssertions.assertSoftly(softly -> {
        //     softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER)).isNotNull();
        //});
        
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, true, false, policyNumber, DocGenEnum.Documents.AHAUXX);
		
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
     * @scenario 1. Issue a NB policy (score 650)
     * 2. Initiate renewal
     * 3. Add a new driver who has a better insurance score
     * 4. Reorder insurance score report (best score changes to 840) + reorder clue report
     * 5. Do renewal proposal
     * 6. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA, States.CO, States.MD})
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testNewDriverBetterScore(@Optional("AZ") String state) {
        Assertions.assertThat(asList("MD", "CO").contains(state)).as("Test does not support this state: " + state).isFalse();
        TestData policyTD = getPolicyDefaultTD();

        TestData renewalTD = getTestSpecificTD("TestData_Renewal")
                .adjust(getTestSpecificTD("Driver_Score_840").resolveLinks())
                .adjust(NAMED_INSURED_OVERRIDE, getTestSpecificTD("NamedInsured_Score_840").resolveLinks().getTestDataList("NamedInsuredInformation"))
                .adjust(RATING_DETAILS_REPORTS_TAB, getTestSpecificTD("RatingDetailReportsTab_InsuranceScore"))
                .adjust(ERROR_TAB, getTestSpecificTD("ErrorTab_ForeignLicense"));

        String policyNumber = createPolicy(policyTD);
        
        //Workaround with Do Not Renew action added 
        TestData doNotRenewTD = getStateTestData(testDataManager.policy.get(getPolicyType()), "DoNotRenew", "TestData");

        renewPolicy(policyNumber, renewalTD, doNotRenewTD);

        //Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, true);

        //SoftAssertions.assertSoftly(softly -> {
        //    softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER)).isNotNull();
        // });
        
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, true, false, policyNumber, DocGenEnum.Documents.AHAUXX);
		
    }

    /**
     * Tests Insurance Score trigger
     * CIN document should be generated if insurance score was re-ordered and the new score was better but not in the best band
     *
     * @param state any except MD, CO has its own test
     * @scenario 1. Issue a NB policy (score 650)
     * 2. Initiate renewal
     * 3. Update NI first name so that we are forced to reorder insurance score
     * 4. Reorder insurance score report (score changes to 840) + reorder clue report
     * 5. Do renewal proposal
     * 6. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = {States.CA, States.CO, States.MD})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testReorderBetterScore(@Optional("AZ") String state) {
        Assertions.assertThat(asList("MD", "CO").contains(state)).as("Test does not support this state: " + state).isFalse();
        TestData policyTD = getPolicyDefaultTD();

        TestData renewalTD = getTestSpecificTD("TestData_Renewal")
                .adjust(NAMED_INSURED_OVERRIDE, getTestSpecificTD("NamedInsured_Update_Score_840").resolveLinks().getTestDataList("NamedInsuredInformation"))
                .adjust(RATING_DETAILS_REPORTS_TAB, getTestSpecificTD("RatingDetailReportsTab_InsuranceScore"));

        String policyNumber = createPolicy(policyTD);

        renewPolicy(policyNumber, renewalTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER)).isNotNull();
        });
    }

    /*******************************
     *
     * PRIOR BI LIMIT TRIGGERS
     *
     *******************************/

    /**
     * Tests Prior BI Limit trigger
     * Creates a policy where there is no prior carrier
     * BI Limit trigger should be suppressed for renewal
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Make sure that prior carrier is 'None'
     * 3. Bind the policy
     * 4. Renew the policy
     * 5. Make sure that CIN document is not generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testPriorBILimitNoPriorCarrier(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_940"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("Dont_Override_CurrentCarrierInformation"))
                .adjust(REQUIRED_TO_ISSUE, getTestSpecificTD("RequiredToIssue_No_PriorBI"));

        TestData renewalTD = getTestSpecificTD("TestData_Renewal");

        String policyNumber = createPolicy(policyTD);

        renewPolicy(policyNumber, renewalTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_REDUNDANT_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.RENEWAL_OFFER)).isNull();
        });
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }
}
