package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.logs.PasAppLogGrabber;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomSoftAssertions;

import java.util.List;

@StateList(states = {Constants.States.AZ})
public class TestOfflineClaimsAnalytics extends TestOfflineClaimsTemplate {

    // NOTE: Claims Matching Logic: e2e tests should use HTTP instead of HTTPS in DB (Microservice propertyname ='aaaClaimsMicroService.microServiceUrl')
    // Example: http://claims-assignment-master.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1

    private static PasAppLogGrabber pasAppLogGrabber = new PasAppLogGrabber();

    private static String appLog;
    private static List<String> listOfClaims;
    private static String pasFirstNamedInsured;
    private static String policyEffectiveDate;

    private static final String policyEffectiveDateKey = "policyEffectiveDate";
    private static final String coverageIdKey = "coverageId";
    private static final String coverageAmountKey = "coverageAmount";
    private static final String coverageNameKey = "coverageName";
    private static final String matchCodeKey = "matchCode";
    private static final String dateOfLossKey = "dateOfLoss";
    private static final String claimDriverNameKey = "claimDriverName";
    private static final String policyNumberKey = "policyNumber";
    private static final String claimCauseKey = "claimCause";
    private static final String totalAmountPaidKey = "totalAmountPaid";
    private static final String pasDriverNameKey = "pasDriverName";
    private static final String permissiveUseKey = "permissiveUse";
    private static final String claimTypeKey = "claimType";
    private static final String claimOpenDateKey = "claimOpenDate";
    private static final String accidentFaultKey = "Accident Fault";
    private static final String lossSummaryKey = "lossSummary";
    private static final String claimCloseDateKey = "claimCloseDate";
    private static final String claimNumberKey = "claimNumber";
    private static final String statusKey = "status";

    private static final String CLAIM_NUMBER_1 = "AnalyticsTestClaim1"; // for MatchCode = COMP

    private static final String CLAIMS_DATA_MODEL_FOR_ANALYTICS = "claims_data_model_for_analytics.yaml";


    /**
     * @author Mantas Garsvinskas
     * @name Test All existing values in Offline Claims Analytics generated JSON
     * @scenario
     * 0. Preconditions: MatchMoreClaims lookup should be set to: TRUE
     * 1. Create a Policy
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob" to generate CAS Request
     * 4. Generate & Upload CAS Response file
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchReceiveJob"
     * 7. Retrieve the pas-admin log file
     * 8. Parse Claims Analytics rows from the log
     * 9. Verify all tags and values in JSON claim analytics according to Claim Number and policyNumber
     *      "policyEffectiveDate": "2021-10-08 00:00:00.0"
     * 		"coverageId": " COV_001,COV_002",
     * 		"coverageAmount": " 100,250",
     * 		"coverageName": " BODILY_INJURY, MEDICAL_PAYMENTS",
     * 		"matchCode": "DL",
     * 		"dateOfLoss": "2019-08-01",
     * 		"claimDriverName": "Name One",
     * 		"policyNumber": "AZSS952918542",
     * 		"claimCause": "Cause1",
     * 		"totalAmountPaid": 1100,
     * 		"pasDriverName": "Matthew Fox",
     * 		"permissiveUse": "Y",
     * 		"claimType": "Accident1",
     * 		"claimOpenDate": "2019-08-02",
     * 		"Accident Fault": "AF",
     * 		"lossSummary": "Policy: collided with 1 parked vehicle",
     * 		"claimCloseDate": " ",
     * 		"claimNumber": "4TZ01111OHS",
     * 		"status": "OPEN"
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-20361")
    public void pas20361_compClaimDeterminationBeforeMS(@Optional("AZ") String state) {

        DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);
        DBService.get().executeUpdate(String.format(SQL_UPDATE_PERMISSIVEUSE_DATEOFLOSS, "11-JAN-18"));

        //TODO: gunxgar change to default
        TestData testData = getPolicyDefaultTD();

        // Create Customer and Policy
        mainApp().open();
        createCustomerIndividual();
        pasFirstNamedInsured = CustomerSummaryPage.labelCustomerName.getValue();

        policy.createPolicy(testData);

        // Gather Policy details: Policy Number and expiration date
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        policyEffectiveDate =  PolicySummaryPage.getEffectiveDate().toLocalDate().toString();

        // Move to R-63, run batch job part 1 and renewalClaimOrderAsyncJob to generate CAS Request
        runRenewalClaimOrderJob();

        // Create the claim response
        createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(policyNumber, CLAIMS_DATA_MODEL_FOR_ANALYTICS);

        // Move to R-46 and run batch job part 2 and renewalClaimReceiveAsyncJob to generate Microservice Request/Response and Analytic logs
        runRenewalClaimReceiveJob();

        appLog = downloadPasAppLog();
        listOfClaims = pasAppLogGrabber.retrieveClaimsAnalyticsLogValues(appLog);

        CustomSoftAssertions.assertSoftly(softly -> {
            // Verify Claim Analytic Logs: all keys and values
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, policyEffectiveDateKey))
                    .as("Policy Eff. date should be equal to: "+policyEffectiveDate+" 00:00:00.0").isEqualTo(policyEffectiveDate+" 00:00:00.0");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, coverageIdKey))
                    .as("Coverage IDs should be equal to: COV_001,COV_002").isEqualTo("COV_001,COV_002");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, coverageAmountKey))
                    .as("Coverage Amounts should be equal to: 100,250").isEqualTo("100,250");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, coverageNameKey))
                    .as("Coverage Names should be equal to: BODILY_INJURY,MEDICAL_PAYMENTS").isEqualTo("BODILY_INJURY,MEDICAL_PAYMENTS");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, matchCodeKey))
                    .as("Match Code should be equal to: DL").isEqualTo("DL");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, dateOfLossKey))
                    .as("Date Of Loss should be equal to: 2018-05-26").isEqualTo("2018-05-26");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, claimDriverNameKey))
                    .as("Claim Driver Name should be equal to: casMATTHEW casFOX").isEqualTo("casMATTHEW casFOX");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, policyNumberKey))
                    .as("Policy Number should be equal to: "+policyNumber).isEqualTo(policyNumber);
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, claimCauseKey))
                    .as("Claim Cause should be equal to: cause1").isEqualTo("cause1");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, totalAmountPaidKey))
                    .as("Total Amount Paid should be equal to: 5000").isEqualTo("5000");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, pasDriverNameKey))
                    .as("PAS Driver Name should be equal to: "+pasFirstNamedInsured).isEqualTo(pasFirstNamedInsured);
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, permissiveUseKey))
                    .as("Permissive Use should be equal to: Y").isEqualTo("Y");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, claimTypeKey))
                    .as("Claim Type Name should be equal to: Accident1").isEqualTo("Accident1");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, claimOpenDateKey))
                    .as("Claim Open Date should be equal to: 2018-05-26").isEqualTo("2018-05-26");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, accidentFaultKey))
                    .as("Accident Fault should be equal to: AT_FAULT").isEqualTo("AT_FAULT");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, lossSummaryKey))
                    .as("Loss Summary should be equal to: IV hit OV while turning.").isEqualTo("IV hit OV while turning.");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, claimCloseDateKey))
                    .as("Claim Close Date should be equal to: 2018-08-12").isEqualTo("2018-08-12");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, claimNumberKey))
                    .as("Claim Number should be equal to: AnalyticsTestClaim1").isEqualTo("AnalyticsTestClaim1");
            softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, statusKey))
                    .as("Status Key should be equal to: OPEN").isEqualTo("OPEN");
        });
    }
}
