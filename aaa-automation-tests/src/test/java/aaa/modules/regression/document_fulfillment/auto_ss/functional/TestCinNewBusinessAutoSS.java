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

public class TestCinNewBusinessAutoSS extends TestCinAbstractAutoSS {


    /*******************************
     *
     *      CLUE/MVR TRIGGERS
     *
     *******************************/

    /**
     * @name Test CIN Document generation (MVR activity)
     * <p>
     * Depends on ChoicePointMvrMockData mocksheet was updated with Driver Info: First Name: MVRChargeable Last Name: Activity
     * @scenario 1. Create Customer
     * 2. Create Policy with Driver having chargeable MVR violation
     * 3. Check that CIN document is getting generated
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7515")
    public void testCinNewBusinessMVR(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD().adjust(getTestSpecificTD("TestData_MVR").resolveLinks())
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /**
     * @name Test CIN Document generation (CLUE activity)
     * <p>
     * Depends on ChoicePointClueMockData mocksheet was updated with Driver Info: First Name: ClueChargeable Last Name: Activity
     * @scenario 1. Create Customer
     * 2. Create Policy with Driver having chargeable CLUE violation
     * 3. Check that CIN document is getting generated
     * 4. Verify document sequence
     * @details
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7515")
    public void testCinNewBusinessCLUE(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD().adjust(getTestSpecificTD("TestData_CLUE").resolveLinks())
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /*******************************
     *
     *   INSURANCE SCORE TRIGGERS
     *
     *******************************/

    /**
     * Tests Insurance Score trigger
     * Creates a policy with the default insurance score which is 650
     * The default insurance score is always out of best band so it should always generate CIN document
     *
     * @param state any
     * @scenario 1. Issue a policy with insurance score of 650
     * 2. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testInsuranceScoreNotInBestBand(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD()
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /**
     * Tests Insurance Score trigger
     * Creates a policy with insurance score which is in the best band
     * We should not generate CIN document if insurance score is in the best band
     * <p>
     * Depends on 130-220CL-25 Martinez Clark data from ChoicePointNCFMockData
     *
     * @param state any
     * @scenario 1. Issue a policy with score insurance score of 970
     * 2. Verify that CIN document is not generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testInsuranceScoreInBestBand(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_BestBand")
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_REDUNDANT_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNull();
        });
    }


    /**
     * Tests Insurance Score trigger
     * Creates a policy with 'No Hit' insurance score
     * CIN document should be generated for 'No Hit' insurance score
     * <p>
     * Depends on 130-220CL-23 Lee Hill data from ChoicePointNCFMockData
     *
     * @param state any
     * @scenario 1. Issue a policy with 'No Hit' insurance score
     * 2. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testInsuranceScoreNoHit(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_NoHit")
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /**
     * Tests Insurance Score trigger
     * Creates a policy with 'No Score' insurance score
     * CIN document should be generated for 'No Score' insurance score
     * <p>
     * Depends on 130-220CL-48 Thomas Wu data from ChoicePointNCFMockData
     *
     * @param state any
     * @scenario 1. Issue a policy with 'No Score' insurance score
     * 2. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testInsuranceScoreNoScore(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_NoScore")
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /**
     * Tests Insurance Score trigger
     * Creates a policy with the default insurance score which is 650 and overrides it to higher (651)
     * No CIN document should be generated if insurance score which is used in rating came from an override instead of NCF report
     *
     * @param state any
     * @scenario 1. Start a quote, retrieve insurance score of 650 from NCF report
     * 2. Override the insurance score to 651
     * 3. Bind the policy
     * 4. Verify that CIN document is not generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testInsuranceScoreOverrideUp(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_651"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_REDUNDANT_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNull();
        });
    }

    /**
     * Tests Insurance Score trigger
     * Creates a policy with the default insurance score which is 650 and overrides it to lower (649)
     * No CIN document should be generated if insurance score which is used in rating came from an override instead of NCF report
     *
     * @param state any
     * @scenario 1. Start a quote, retrieve insurance score of 650 from NCF report
     * 2. Override the insurance score to 649
     * 3. Bind the policy
     * 4. Verify that CIN document is not generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testInsuranceScoreOverrideDown(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_REDUNDANT_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNull();
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
     * CIN document should be generated if prior BI is less than 500000
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Make sure that prior carrier is 'None'
     * 3. Bind the policy
     * 4. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testPriorBILimitNoPriorCarrier(@Optional("AZ") String state) {
        TestData policyTD = getPolicyDefaultTD()
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("Dont_Override_CurrentCarrierInformation"))
                .adjust(REQUIRED_TO_ISSUE, getTestSpecificTD("RequiredToIssue_No_PriorBI"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /**
     * Tests Prior BI Limit trigger
     * Creates a policy where the prior BI limit for the previous carrier was 'None'
     * CIN document should be generated if prior BI is less than 500000
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Make sure that prior BI is 'None'
     * 3. Bind the policy
     * 4. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testPriorBILimitPrefillNone(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_Prior_BI_None")
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("Dont_Override_CurrentCarrierInformation"))
                .adjust(REQUIRED_TO_ISSUE, getTestSpecificTD("RequiredToIssue_No_PriorBI"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /**
     * Tests Prior BI Limit trigger
     * Manually enters prior BI limit to be None
     * CIN document should be generated if prior BI is less than 500000
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Prefill prior BI with value 100000
     * 3. Manually override prior BI to None
     * 4. Bind the policy
     * 5. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testPriorBILimitNone(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_Prior_BI_1000")
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("CurrentCarrierInformation_None"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /**
     * Tests Prior BI Limit trigger
     * Pre-fills a quote with prior BI that's less than 500000
     * CIN document should be generated if prior BI is less than 500000
     * <p>
     * Depends on Shannon Adams (AZ-Q)test data from ChoicePointPrefillMockData
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Prefill policy with prior BI limit of 100000/300000
     * 3. Bind the policy
     * 4. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testPriorBILimitPrefillLessThan500k(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_Prior_BI_100_300")
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("Dont_Override_CurrentCarrierInformation"))
                .adjust(REQUIRED_TO_ISSUE, getTestSpecificTD("RequiredToIssue_No_PriorBI"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        });
    }

    /**
     * Tests Prior BI Limit trigger
     * Manually enters prior BI limit to be less than 500000
     * CIN document should be generated if prior BI is less than 500000
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Prefill prior BI with value $1,000,000/$1,000,000
     * 3. Manually override prior BI to $25,000/$50,000
     * 4. Bind the policy
     * 5. Verify that CIN document is generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testPriorBILimitLessThan500k(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_Prior_BI_1000")
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("CurrentCarrierInformation_25_50"));

        String policyNumber = createPolicy(policyTD);

        //Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);
        //SoftAssertions.assertSoftly(softly -> {
        //   softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
        //});
        
		ETCSCoreSoftAssertions softly = new ETCSCoreSoftAssertions();
		DocGenHelper.verifyDocumentsGenerated(softly, true, false, policyNumber, DocGenEnum.Documents.AHAUXX);

    }

    /**
     * Tests Prior BI Limit trigger
     * Pre-fills a quote with prior BI Limit of 500000
     * CIN document should not be generated if prior BI limit is 500000 or more
     * <p>
     * Depends on Shannon BILimit500k (SNT-1) test data from ChoicePointPrefillMockData
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Prefill policy with prior BI limit of 500000
     * 3. Bind the policy
     * 4. Verify that CIN document is not generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testPriorBILimitPrefill500k(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_Prior_BI_500")
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("Dont_Override_CurrentCarrierInformation"))
                .adjust(REQUIRED_TO_ISSUE, getTestSpecificTD("RequiredToIssue_No_PriorBI"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_REDUNDANT_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNull();
        });
    }

    /**
     * Tests Prior BI Limit trigger
     * Manually enters prior BI limit to be 500000
     * CIN document should not be generated if prior BI limit is 500000 or more
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Prefill prior BI with value $100,000/$300,000
     * 3. Manually override prior BI to $500,000/$500,000
     * 4. Bind the policy
     * 5. Verify that CIN document is not generated
     */
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testPriorBILimit500k(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_Prior_BI_100_300")
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("CurrentCarrierInformation_500_500"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_REDUNDANT_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNull();
        });
    }

    /**
     * Tests Prior BI Limit trigger
     * Pre-fills a quote with prior BI that is more than 500000
     * CIN document should not be generated if prior BI limit is 500000 or more
     * <p>
     * Depends on Shannon BILimit1000k (SNT-2) test data from ChoicePointPrefillMockData
     *
     * @param state any
     * @scenario 1. Start a quote
     * 2. Prefill policy with prior BI limit of 1000000
     * 3. Bind the policy
     * 4. Verify that CIN document is not generated
     */
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    @Parameters({STATE_PARAM})
    @StateList(statesExcept = States.CA)
    public void testPriorBILimitPrefillMoreThan500k(@Optional("AZ") String state) {
        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NameInsured_Prior_BI_1000")
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(DRIVER_ACTIVITY_REPORTS_PATH, getTestSpecificTD("DriverActivityReportsTab"))
                .adjust(CURRENT_CARRIER_INFORMATION, getTestSpecificTD("Dont_Override_CurrentCarrierInformation"))
                .adjust(REQUIRED_TO_ISSUE, getTestSpecificTD("RequiredToIssue_No_PriorBI"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_REDUNDANT_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNull();
        });
    }

    /*******************************
     *
     * DELTAS
     *
     *******************************/

    /**
     * Tests CLUE trigger for VA
     * Tests at fault activity document section trigger
     * <p>
     * Depends on ClueChargeable Activity mock data from ChoicePointClueMockData
     *
     * @param state VA
     * @scenario 1. Create a quote with chargeable not-at-fault clue
     * 2. Verify that CIN document is generated
     * 3. Verify that at fault activity flag is set to false in CIN document request
     * 4. Verify document sequence
     */
    @Parameters({STATE_PARAM})
    @StateList(states = States.VA)
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-7515")
    public void testCinNotAtFaultCLUEVA(@Optional("VA") String state) {
        Assertions.assertThat(asList("VA").contains(state)).as("Test does not support this state: " + state).isTrue();

        TestData policyTD = adjustNameInsured(getPolicyDefaultTD(), "NamedInsured_ChangeableNAF")
                .adjust(ERROR_TAB, getTestSpecificTD("ErrorTab_VA_MVR"))
                .adjust(INSURANCE_SCORE_OVERRIDE, getTestSpecificTD("InsuranceScoreOverride_649"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));
        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, true);

        String atFaultAccidentFlag = retrieveElementValue(cinDocument, "DriverDetails", "AtFltAccYN");

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_MISSING_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
            softly.assertThat(atFaultAccidentFlag).as(getPolicyErrorMessage("At fault flag is missing in the CIN document request", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNotNull();
            softly.assertThat(atFaultAccidentFlag).as(getPolicyErrorMessage("Failed to generate correct at fault flag for CIN document", policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isEqualTo("N");
        });
    }

    /**
     * Tests Insurance Score trigger
     * Tests extraordinary life circumstance functionality in scope of insurance score
     * If extraordinary life circumstance insurance score overrides the best insurance score on the policy then CIN document shouldn't be generated
     *
     * @param state NJ, DE, CO, CT, KS, KY, NV, MT
     * @scenario 1. Initiate a NB quote
     * 2. Select "Extraordinary life circumstance" to "Military deployment overseas"
     * 3. Use the default insurance score (650) for the NI
     * 4. Override the approval request in error tab
     * 5. Bind the policy
     * 6. Verify that CIN document hasn't been generated
     */
    @Parameters({STATE_PARAM})
    @StateList(states = {States.DE, States.CO, States.CT, States.KS, States.KY, States.MT, States.NJ, States.NV})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-1169")
    public void testInsuranceScoreELC(@Optional("MT") String state) {
        //Assertions.assertThat(asList("NJ", "DE", "CO", "CT", "KS", "KY", "NV", "MT").contains(state)).as("Test does not support this state: " + state).isTrue();
        TestData policyTD = getPolicyDefaultTD()
                .adjust(ADJUST_ELC, getTestSpecificTD("GeneralTab_ELC").getValue("Extraordinary Life Circumstance"))
                .adjust(ERROR_TAB_CALCULATE_PREMIUM, getTestSpecificTD("ErrorTab_ELC"))
                .adjust(DISABLE_MEMBERSHIP, getTestSpecificTD("AAAMembership"))
                .adjust(SUPPRESS_PRIOR_BI_TRIGGER, getTestSpecificTD("CurrentCarrierInformation_1000_1000").getValue("Agent Entered BI Limits"));

        String policyNumber = createPolicy(policyTD);

        Document cinDocument = DocGenHelper.waitForDocumentsAppearanceInDB(DocGenEnum.Documents.AHAUXX, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE, false);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(cinDocument).as(getPolicyErrorMessage(CIN_DOCUMENT_REDUNDANT_ERROR, policyNumber, AaaDocGenEntityQueries.EventNames.POLICY_ISSUE)).isNull();
        });
    }

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }
}
