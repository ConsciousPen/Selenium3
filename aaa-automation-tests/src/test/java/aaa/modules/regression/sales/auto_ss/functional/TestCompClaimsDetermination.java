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
public class TestCompClaimsDetermination extends TestOfflineClaimsTemplate {

	// NOTE: Claims Matching Logic: e2e tests should use HTTP instead of HTTPS in DB (Microservice propertyname ='aaaClaimsMicroService.microServiceUrl')
	// Example: http://claims-assignment.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1

	private static PasLogGrabber pasLogGrabber = new PasLogGrabber();

	private static List<String> listOfClaims;
	private static String pasFirstNamedInsured;
	private static String pas2ndDriver = "pasMATTHEW pasFOX";

	private static final String CLAIM_NUMBER_1 = "AnalyticsClaim1"; // for MatchCode = COMP
	private static final String CLAIM_NUMBER_2 = "AnalyticsClaim2"; // for MatchCode = COMP
	private static final String CLAIM_NUMBER_3 = "AnalyticsClaim3"; // for MatchCode = COMP
	private static final String CLAIM_NUMBER_4 = "AnalyticsClaim4"; // for MatchCode = DL
	private static final String CLAIM_NUMBER_5 = "AnalyticsClaim5"; // for MatchCode = DL

	private static final String COMP_CLAIMS_DATA_MODEL = "comp_claims_data_model.yaml";


	/**
	 * @author Mantas Garsvinskas
	 * @name Test Comprehensive Claims Determination before sending to MS
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
	 * 9. Verify matchCode values in JSON claim analytics according to Claim Number and policyNumber
	 * 9.1: Claim1 = AnalyticsClaim1 = COMP: COMP/RENTAL/TOWING = 0;
	 * 9.2: Claim2 = AnalyticsClaim2 = COMP: COMP/RENTAL/TOWING > 0;
	 * 9.3: Claim3 = AnalyticsClaim3 = COMP: COMP/RENTAL/TOWING >= 0, OTHER COVERAGE = 0;
	 * 9.4: Claim4 = AnalyticsClaim4 = DL: RENTAL/TOWING >= 0;
	 * 9.5: Claim5 = AnalyticsClaim5 = DL: COMP/RENTAL/TOWING/ >= 0, OTHER COVERAGE > 1;
	 * 10. Verify pasDriverName values in JSON claim analytics according to Claim Number
	 * 10.1 Claim1 = AnalyticsClaim1 = COMP: First Named Insured
	 * 10.2 Claim5 = AnalyticsClaim5 = DL: 2nd Driver from PAS
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-20361")
	public void pas20361_compClaimDeterminationBeforeMS(@Optional("AZ") String state) {

		TestData testData = getTestSpecificTD("TestData_DriverTab_CompClaimsDetermination_AZ").resolveLinks();
		TestData td = getPolicyTD().adjust(testData);

		// Create Customer and Policy
		mainApp().open();
		createCustomerIndividual();
		pasFirstNamedInsured = CustomerSummaryPage.labelCustomerName.getValue();

		policy.createPolicy(td);

		// Gather Policy details: Policy Number and expiration date
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		mainApp().close();

		// Move to R-63, run batch job part 1 and renewalClaimOrderAsyncJob to generate CAS Request
		runRenewalClaimOrderJob();

		// Create the claim response
		createCasClaimResponseAndUploadWithUpdatedPolicyNumberOnly(policyNumber, COMP_CLAIMS_DATA_MODEL);

		// Move to R-46 and run batch job part 2 and renewalClaimReceiveAsyncJob to generate Microservice Request/Response and Analytic logs
		runRenewalClaimReceiveJob();

		listOfClaims = pasLogGrabber.retrieveClaimsAnalyticsLogValues(combinePasAppAndAdminLog());

		CustomSoftAssertions.assertSoftly(softly -> {
		// Verify Claim Analytic Logs: MATCH CODE according to Claim Number and policyNumber
			softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.MATCH_CODE))
					.as("Match Code should be equal to COMP").isEqualTo("COMP");
			softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_2, policyNumber, ClaimAnalyticsJSONTags.TagNames.MATCH_CODE))
					.as("Match Code should be equal to COMP").isEqualTo("COMP");
			softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_3, policyNumber, ClaimAnalyticsJSONTags.TagNames.MATCH_CODE))
					.as("Match Code should be equal to COMP").isEqualTo("COMP");
			softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_4, policyNumber, ClaimAnalyticsJSONTags.TagNames.MATCH_CODE))
					.as("Match Code should be equal to DL").isEqualTo("DL");
			softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_5, policyNumber, ClaimAnalyticsJSONTags.TagNames.MATCH_CODE))
					.as("Match Code should be equal to DL").isEqualTo("DL");

		// Verify Claim Analytic Logs: PAS Driver Name according to Claim Number and policyNumber
			softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_1, policyNumber, ClaimAnalyticsJSONTags.TagNames.PAS_DRIVER_NAME))
					.as("PAS Driver should be First Named Insured").isEqualTo(pasFirstNamedInsured);
			softly.assertThat(retrieveClaimValueFromAnalytics(listOfClaims, CLAIM_NUMBER_5, policyNumber, ClaimAnalyticsJSONTags.TagNames.PAS_DRIVER_NAME))
					.as("PAS Driver should be 2nd Driver of Policy").isEqualTo(pas2ndDriver);
		});
	}
}



