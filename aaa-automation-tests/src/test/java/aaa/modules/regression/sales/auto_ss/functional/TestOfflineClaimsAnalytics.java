package aaa.modules.regression.sales.auto_ss.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.claim.ClaimAnalyticsJSONTags;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.logs.PasLogGrabber;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;

@StateList(states = {Constants.States.AZ})
public class TestOfflineClaimsAnalytics extends TestOfflineClaimsTemplate {

    // NOTE: Claims Matching Logic: e2e tests should use HTTP instead of HTTPS in DB (Microservice propertyname ='aaaClaimsMicroService.microServiceUrl')
    // Example: http://claims-assignment-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1

    private static PasLogGrabber pasLogGrabber = new PasLogGrabber();

    private static List<String> listOfClaims;
    private static String pasFirstNamedInsured;
    private static String policyEffectiveDate;

    private static final String CLAIM_NUMBER_1 = "AnalyticsTestClaim1MG";
    private static final String CLAIMS_DATA_MODEL_FOR_ANALYTICS = "claims_data_model_for_analytics.yaml";

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
     * 9. Verify all tags and values in JSON claim analytics according to Claim Number and policyNumber
     *      //TODO: gunxgar - Created according to current functionality: need to refactor empty spaces (coverageId, coverageAmount, coverageName), missing values (claimCloseDate)
     *      "policyEffectiveDate": "2021-10-08 00:00:00.0"
     * 		"coverageId": " COV_001,COV_002",
     * 		"coverageAmount": " 100,250",
     * 		"coverageName": " BODILY_INJURY, MEDICAL_PAYMENTS",
     * 		"matchCode": "DL",
     * 		"dateOfLoss": "2018-05-26",
     * 		"claimDriverName": "casMATTHEW casFOX",
     * 		"policyNumber": "AZSS952918576",
     * 		"claimCause": "cause1",
     * 		"totalAmountPaid": 5000,
     * 		"pasDriverName": "Fernando-UQFKX Smith",
     * 		"permissiveUse": "Y",
     * 		"claimType": "Accident1",
     * 		"claimOpenDate": "2018-05-26",
     * 		"Accident Fault": "AF",
     * 		"lossSummary": "IV hit OV while turning.",
     * 		"claimCloseDate": "2018-08-12",
     * 		"claimNumber": "AnalyticsTestClaim1MG",
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
        pasFirstNamedInsured = CustomerSummaryPage.labelCustomerName.getValue();

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
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.MATCH_CODE))
                    .as("Match Code should be equal to: DL").isEqualTo("DL");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.DATE_OF_LOSS))
                    .as("Date Of Loss should be equal to: 2018-05-26").isEqualTo("2018-05-26");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_DRIVER_NAME))
                    .as("Claim Driver Name should be equal to: casMATTHEW casFOX").isEqualTo("casMATTHEW casFOX");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.POLICY_NUMBER))
                    .as("Policy Number should be equal to: "+policyNumber).isEqualTo(policyNumber);
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_CAUSE))
                    .as("Claim Cause should be equal to: cause1").isEqualTo("cause1");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.TOTAL_AMOUNT_PAID))
                    .as("Total Amount Paid should be equal to: 5000").isEqualTo("5000");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.PAS_DRIVER_NAME))
                    .as("PAS Driver Name should be equal to: "+pasFirstNamedInsured).isEqualTo(pasFirstNamedInsured);
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.PERMISSIVE_USE))
                    .as("Permissive Use should be equal to: Y").isEqualTo("Y");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_TYPE))
                    .as("Claim Type Name should be equal to: Accident1").isEqualTo("Accident1");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_OPEN_DATE))
                    .as("Claim Open Date should be equal to: 2018-05-26").isEqualTo("2018-05-26");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.ACCIDENT_FAULT))
                    .as("Accident Fault should be equal to: AF").isEqualTo("AF");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.LOSS_SUMMARY))
                    .as("Loss Summary should be equal to: IV hit OV while turning.").isEqualTo("IV hit OV while turning.");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_CLOSE_DATE))
					.as("Claim Close Date should be equal to: 2018-08-12").isEqualTo("2018-08-12");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.CLAIM_NUMBER))
                    .as("Claim Number should be equal to: "+CLAIM_NUMBER_1).isEqualTo(CLAIM_NUMBER_1);
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.STATUS))
                    .as("Status Key should be equal to: OPEN").isEqualTo("OPEN");
        });

    }
}
