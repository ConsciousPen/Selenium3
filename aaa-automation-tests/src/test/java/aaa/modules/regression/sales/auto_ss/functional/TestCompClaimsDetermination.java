package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.google.common.collect.ImmutableMap;
import aaa.common.enums.Constants;
import aaa.helpers.claim.PasAdminLogGrabber;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.AZ})
public class TestCompClaimsDetermination extends TestOfflineClaimsTemplate {

	private static VehicleTab vehicleTab = new VehicleTab();
	private static PasAdminLogGrabber pasAdminLogGrabber = new PasAdminLogGrabber();

	private static String adminLog;
	private static List<String> listOfClaims;

	private static final String matchCode = "matchCode";
	private static final String pasDriverName = "pasDriverName";
	private static final String CLAIM_NUMBER_1 = "AnalyticsClaim1"; // for MatchCode = COMP
	private static final String CLAIM_NUMBER_2 = "AnalyticsClaim2"; // for MatchCode = COMP
	private static final String CLAIM_NUMBER_3 = "AnalyticsClaim3"; // for MatchCode = COMP
	private static final String CLAIM_NUMBER_4 = "AnalyticsClaim4"; // for MatchCode = DL
	private static final String CLAIM_NUMBER_5 = "AnalyticsClaim5"; // for MatchCode = DL
	private static final String LICENSE_NUMBER = "A19191912";

	private static final Map<String, String> CLAIM_TO_DRIVER_LICENSE =
			ImmutableMap.of(CLAIM_NUMBER_1, LICENSE_NUMBER, CLAIM_NUMBER_2, LICENSE_NUMBER,
					CLAIM_NUMBER_3, LICENSE_NUMBER, CLAIM_NUMBER_4, LICENSE_NUMBER, CLAIM_NUMBER_5, LICENSE_NUMBER); //TODO gunxgar - get knowledge about this one;

	private static final String COMP_CLAIMS_DATA_MODEL = "comp_claims_data_model.yaml";


	// TODO gunxgar add notes to TOGGLE ON MatchMoreClaims logic
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
	 * 8. Verify matchCode values in JSON claim analytics according to Claim Number
	 * 8.1: Claim1 = COMP: COMP/RENTAL/TOWING = 0;
	 * 8.1: Claim2 = COMP: COMP/RENTAL/TOWING > 0;
	 * 8.1: Claim3 = COMP: COMP/RENTAL/TOWING >= 0, OTHER COVERAGE = 0;
	 * 8.1: Claim4 = DL: RENTAL/TOWING >= 0;
	 * 8.1: Claim5 = DL: COMP/RENTAL/TOWING >= 0, OTHER COVERAGE > 1;
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-20361")
	public void pas20361_compClaimDeterminationBeforeMS(@Optional("AZ") String state) {


		//TODO gunxgar negatvie cases exceptions and so on catch
		//TODO gunxgar maybe at the end, go to app, and validate that COMP claims assigned to FNI, other assigned to Driver2 for instance. OUT OF SCOPE OR IN SCOPE? other tests covering maybe this one?

		TestData testData = getTestSpecificTD("TestData_DriverTab_CompClaimsDetermination_AZ").resolveLinks();
		TestData td = getPolicyTD().adjust(testData);

		// Create Customer and Policy
		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(td);

		// Gather Policy details: Policy Number and expiration date
		String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		LocalDateTime policyExpirationDate = PolicySummaryPage.getExpirationDate();
		mainApp().close();

		// Move to R-63, run batch job part 1 and renewalClaimOrderAsyncJob to generate CAS Request
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(63));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalClaimOrderAsyncJob);


		// Create the claim response
		createCasClaimResponseAndUpload(policyNumber, COMP_CLAIMS_DATA_MODEL, CLAIM_TO_DRIVER_LICENSE);

		// Move to R-46 and run batch job part 2 and renewalClaimReceiveAsyncJob to generate Microservice Request/Response and Analytic logs
		TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(46));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
		JobUtils.executeJob(Jobs.renewalClaimReceiveAsyncJob);

		adminLog = downloadPasAdminLog();
		listOfClaims = pasAdminLogGrabber.retrieveClaimsAnalyticsLogValues(adminLog);

		// Verify Claim Analytic Logs: MATCH CODE according to Claim Number
		assertThat(verifyMatchCodeAccordingToClaimNumber(CLAIM_NUMBER_1, matchCode)).as("Match Code is not COMP").isEqualTo("COMP");
		assertThat(verifyMatchCodeAccordingToClaimNumber(CLAIM_NUMBER_2, matchCode)).as("Match Code is not COMP").isEqualTo("COMP");
		assertThat(verifyMatchCodeAccordingToClaimNumber(CLAIM_NUMBER_3, matchCode)).as("Match Code is not COMP").isEqualTo("COMP");
		assertThat(verifyMatchCodeAccordingToClaimNumber(CLAIM_NUMBER_4, matchCode)).as("Match Code is not DL").isEqualTo("DL");
		assertThat(verifyMatchCodeAccordingToClaimNumber(CLAIM_NUMBER_5, matchCode)).as("Match Code is not DL").isEqualTo("DL");


		// TODO gunxgar, add assertions and check if claims have pasDriverName = correct one. COMP claims for FNI, DL for Driver with DL stated

	}

	/**
	Method returns matchCode value for selected Claim (claimNumber)
	 */
	private String verifyMatchCodeAccordingToClaimNumber(String claimNumber, String requiredValue) {
		return retrieveClaimValueFromAnalytics(listOfClaims, claimNumber, matchCode);
	}
}



