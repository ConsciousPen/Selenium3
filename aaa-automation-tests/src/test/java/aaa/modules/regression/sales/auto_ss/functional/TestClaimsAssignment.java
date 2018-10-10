package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.RestRequestMethodTypes;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.JsonClient;
import aaa.helpers.rest.RestRequestInfo;
import aaa.helpers.rest.dtoClaim.ClaimsAssignmentResponse;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestClaimsAssignment extends AutoSSBaseTest {
	private static final String claimsUrl = "https://claims-assignment.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1";

	@SuppressWarnings("SpellCheckingInspection")
	private static final String MICRO_SERVICE_REQUESTS = "src/test/resources/claimsmatch/claim_micro_service_requests";

	/**
	* * @author Chris Johns
	 *
	 * PAS-14679: MATCH MORE: Create Claim to Driver Match Logic (use DL # when not comp/not already assigned to driver)
	 * PAS-18391: Add Existing Logic to Micro service (previously matched claims)
	 *
	* @name Test Claims Matching Micro Service - Test 1 -3 Claims: No match, Exiting match, DL Match
	* @scenario
	* Test Steps:
	* 1. Send JSON Request with 3 claims to the Claims Matching Micro Service
	* 2. Verify the following claims match results:
	*      --Claim 1, 1TAZ1111OHS: No Match
	*      --Claim 2, 7TZ02222OHS: Existing Match
	*      --Claim 3, 3TAZ3333OHS: DL Match
	*/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	@StateList(states = {Constants.States.AZ})
	public void claimsMatching_test1(@Optional("AZ") String state) throws IOException {
		//Define which JSON request to use
		//TODO - Consider using a JSON Request Builder for future tests
		String claimsRequest = new String(Files.readAllBytes(Paths.get(MICRO_SERVICE_REQUESTS + "NoMatch_ExistingMatch_DLMatch.json")));

		//Use 'runJsonRequestPostClaims' to send the JSON request to the Claims Assignment Micro Service
		ClaimsAssignmentResponse microServiceResponse = runJsonRequestPostClaims(claimsRequest);

		//Throw the microServiceResponse to log - assists with debugging
		log.info(microServiceResponse.toString());

		//Verify the First claim is in the unmatched section
		assertThat(microServiceResponse.getUnmatchedClaims().get(0).getClaimNumber()).isEqualTo("1TAZ1111OHS");

		//Verify that the Second claim returned is an existing match and the Third claim is a DL match
		assertThat(microServiceResponse.getMatchedClaims().get(0).getMatchCode()).isEqualTo("EXISTING_MATCH");
		assertThat(microServiceResponse.getMatchedClaims().get(1).getMatchCode()).isEqualTo("DL");
	}
	//Method to send JSON Request to Claims Matching Micro Service

	public static ClaimsAssignmentResponse runJsonRequestPostClaims(String claimsRequest) {
		RestRequestInfo<ClaimsAssignmentResponse> restRequestInfo = new RestRequestInfo<>();
		restRequestInfo.url = claimsUrl;
		restRequestInfo.bodyRequest = claimsRequest;
		restRequestInfo.responseType = ClaimsAssignmentResponse.class;
		return JsonClient.sendJsonRequest(restRequestInfo, RestRequestMethodTypes.POST);
	}
}


