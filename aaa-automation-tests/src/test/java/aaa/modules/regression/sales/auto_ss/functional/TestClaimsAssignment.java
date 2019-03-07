package aaa.modules.regression.sales.auto_ss.functional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.claim.ClaimAnalyticsJSONTags;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

/**
 * @author Chris Johns
 * PAS-14679: MATCH MORE: Create Claim to Driver Match Logic (use DL # when not comp/not already assigned to driver)
 * PAS-18391: Add Existing Logic to Micro service (previously matched claims)
 * PAS-14058: MATCH MORE: Create Claim to Driver Match Logic (comp claims and not already assigned to driver)
 * PAS-8310: MATCH MORE: Create Claim to Driver Match Logic (not comp/not already assigned to driver/not DL) (part 1)
 * PAS-17894: MATCH MORE: Create Claim to Driver Match Logic (not comp/not already assigned to driver/not DL) (part 2)
 * PAS-21435: Remove LASTNAME_YOB match logic
 * PAS-18341; Added PermissiveUse tag to Claims Service Contract (this test contains Y, N, blank, no tag, and Junk test veriations)
 * PAS-18300; Add Permissive use match criteria; will match to the FNI
 * @name Test Claims Matching Micro Service - Test 1 -3 Claims: No match, Exiting match, DL Match
 * @scenario Test Steps:
 * 1. Send JSON Request with 3 claims to the Claims Matching Micro Service
 * 2. Verify the following claims match results:
 * --Claim 1, 1TAZ1111OHS: No Match
 * --Claim 2, 7TZ02222OHS: Existing Match
 * --Claim 3, 3TAZ3333OHS: DL Match
 * --Claim 4, 4TAZ4444OHS: COMP Match - goes to fist named insured
 * --Claim 5, 1TZ90531OHS: LASTNAME_FIRSTNAME_DOB Match
 * --Cliam 6, 1TZ90411OHS: LASTNAME_FIRSTNAME_YOB Match
 * --Claim 7-11,  17894- 2, 3, 5, 7, & 9: UNMATCHED
 * --Claim 12,    17894- 1: LASTNAME_FIRSTNAME
 * --Claim 13,    17894- 4: LASTNAME_FIRSTINITAL_DOB
 * --Claim 14-15, 17894- 6 & 8: Unmatched (PAS-21435 Removed LASTNAME_YOB Match)
 * --Claim 16-18, 18431- 1, 2, 3: PERMISSIVE_USE Match
 * --Claim 19, 18431- 4: UNMATCHED Match with PU = Yes, because dateOfLoss of Claim = -1 Day of dateOfLoss Parameter
 * --Claim 20, 18431- 5: UNMATCHED Match
 **/
public class TestClaimsAssignment extends TestOfflineClaimsTemplate {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String MICRO_SERVICE_REQUESTS = "src/test/resources/feature/claimsmatch/claim_micro_service_requests/";

    private static List<String> listOfClaims;
    private static String policyEffectiveDate;

    private static final String CLAIM_NUMBER_1 = "AnalyticsTestClaim1MG";
    private static final String CLAIMS_DATA_MODEL_FOR_ANALYTICS = "claims_data_model_for_analytics.yaml";

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-14679,PAS-18391,PAS-14058,PAS-8310,PAS-17894,PAS-18300")
    @StateList(states = {Constants.States.AZ})
    public void pas14679_testMSClaimsAssignment_AZ_SS(@Optional("AZ") String state) throws IOException {
        // Toggle ON PermissiveUse Logic. NOTE: dateOfLoss parameter value is set manually in JSON request
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        //Define which JSON request to use
        String claimsRequest = new String(Files.readAllBytes(Paths.get(MICRO_SERVICE_REQUESTS + "testMSClaimsAssignment_AZ_SS.json")));
        //Use 'runJsonRequestPostClaims' to send the JSON request to the Claims Assignment Micro Service
        testClaimsAssigmentAssertion(runJsonRequestPostClaims(claimsRequest));
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT, testCaseId = "PAS-14679,PAS-18391,PAS-14058,PAS-8310,PAS-17894")
    @StateList(states = {Constants.States.CA})
    public void pas14679_testMSClaimsAssignment_CA_Select(@Optional("CA") String state) throws IOException {
        // Toggle ON PermissiveUse Logic. NOTE: dateOfLoss parameter value is set manually in JSON request
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        //Define which JSON request to use
        String claimsRequest = new String(Files.readAllBytes(Paths.get(MICRO_SERVICE_REQUESTS + "testMSClaimsAssignment_CA_Select.json")));
        testClaimsAssigmentAssertion(runJsonRequestPostClaims(claimsRequest)); //Use 'runJsonRequestPostClaims' to send the JSON request to the Claims Assignment Micro Service
    }

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE, testCaseId = "PAS-14679,PAS-18391,PAS-14058,PAS-8310,PAS-17894")
    @StateList(states = {Constants.States.CA})
    public void pas14679_testMSClaimsAssignment_CA_Choice(@Optional("CA") String state) throws IOException {
        // Toggle ON PermissiveUse Logic. NOTE: dateOfLoss parameter value is set manually in JSON request
        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        //Define which JSON request to use
        String claimsRequest = new String(Files.readAllBytes(Paths.get(MICRO_SERVICE_REQUESTS + "testMSClaimsAssignment_CA_Choice.json")));
        testClaimsAssigmentAssertion(runJsonRequestPostClaims(claimsRequest)); //Use 'runJsonRequestPostClaims' to send the JSON request to the Claims Assignment Micro Service
    }

    /**
     * @author Mantas Garsvinskas
     * PAS-18159 - ANALYTICS: Claim Assignment Log (part 2)
     * PAS-14074 - ANALYTICS: Claim Assignment Log (part 1)
     * @name Test All existing values in Offline Claims Analytics generated JSON
     * @scenario
     * 0. Preconditions: MatchMoreClaims lookup should be set to: TRUE
     * 1. Create a Policy
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob" to generate CAS Request
     * 4. Generate & Upload CAS Response file
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 7. Retrieve the pas-admin AND pas-app logs and combine those in one log content
     * 8. Parse Claims Analytics rows from the log
     * 9. Verify the most important tags and values in JSON claim analytics according to Claim Number and policyNumber
     *      "policyEffectiveDate": "2021-10-08 00:00:00.0"
     * 		"coverageId": " COV_001,COV_002",
     * 		"coverageAmount": " 100,250",
     * 		"coverageName": " BODILY_INJURY, MEDICAL_PAYMENTS",
     * 		"dateOfLoss": "2018-05-26",
     * 		"claimDriverName": "casMATTHEW casFOX",
     * 		"totalAmountPaid": 5000,
     * 		"permissiveUse": "Y",
     * 		"claimOpenDate": "2018-05-26",
     * 		"Accident Fault": "AF",
     * 		"claimCloseDate": "2018-08-12",
     * 		"status": "OPEN"
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-18159", "PAS-14074"})
    public void pas18159_offlineClaimsAnalytics(@Optional("AZ") String state) {

        TestData testData = getPolicyDefaultTD();

        // Create Customer and Policy
        mainApp().open();
        createCustomerIndividual();

        policy.createPolicy(testData);

        // Gather Policy details: Policy Number and expiration date (required R eff. date)
        policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        policyEffectiveDate =  PolicySummaryPage.getEffectiveDate().plusYears(1).toLocalDate().toString();

        // Move to R-63, run batch job part 1 and renewalClaimOrderAsyncJob to generate CAS Request
        runRenewalClaimOrderJob();
        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(policyNumber, CLAIMS_DATA_MODEL_FOR_ANALYTICS);
        // Move to R-46 and run batch job part 2 and renewalClaimReceiveAsyncJob to generate Microservice Request/Response and Analytic logs
        runRenewalClaimReceiveJob();

        // Retrieve Analytic values from log
        listOfClaims = pasLogGrabber.retrieveClaimsAnalyticsLogValues(combinePasAppAndAdminLog());

        // Verify that Claim Analytics' values are correct according to Claim and Policy Numbers
        CustomSoftAssertions.assertSoftly(softly -> {
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.POLICY_EFFECTIVE_DATE))
                    .as("Policy Eff. date should be equal to: "+policyEffectiveDate+" 00:00:00.0").isEqualTo(policyEffectiveDate+" 00:00:00.0");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.COVERAGE_ID))
                    .as("Coverage IDs should be equal to: COV_001,COV_002").isEqualTo(" COV_001,COV_002");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.COVERAGE_AMOUNT))
                    .as("Coverage Amounts should be equal to: 100,250").isEqualTo(" 100,250");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.COVERAGE_NAME))
                    .as("Coverage Names should be equal to: BODILY_INJURY,MEDICAL_PAYMENTS").isEqualTo(" BODILY_INJURY,MEDICAL_PAYMENTS");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.DATE_OF_LOSS))
                    .as("Date Of Loss should be equal to: 2018-05-26").isEqualTo("2018-05-26");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_DRIVER_NAME))
                    .as("Claim Driver Name should be equal to: casMATTHEW casFOX").isEqualTo("casMATTHEW casFOX");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.TOTAL_AMOUNT_PAID))
                    .as("Total Amount Paid should be equal to: 5000").isEqualTo("5000");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.PERMISSIVE_USE))
                    .as("Permissive Use should be equal to: Y").isEqualTo("Y");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_OPEN_DATE))
                    .as("Claim Open Date should be equal to: 2018-05-26").isEqualTo("2018-05-26");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.ACCIDENT_FAULT))
                    .as("Accident Fault should be equal to: AF").isEqualTo("AF");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_CLOSE_DATE))
                    .as("Claim Close Date should be equal to: 2018-08-12").isEqualTo("2018-08-12");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.STATUS))
                    .as("Status Key should be equal to: OPEN").isEqualTo("OPEN");
        });

    }
}

