package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import aaa.common.enums.Constants;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.dtoClaim.ClaimsAssignmentResponse;
import toolkit.utils.TestInfo;

public class TestClaimsAssignment extends AutoSSBaseTest {
	@SuppressWarnings("SpellCheckingInspection")
	private static final String MICRO_SERVICE_REQUESTS = "src/test/resources/claimsmatch/claim_micro_service_requests/";

	/**
	* * @author Chris Johns
	 *
	 * PAS-14679: MATCH MORE: Create Claim to Driver Match Logic (use DL # when not comp/not already assigned to driver)
	 * PAS-18391: Add Existing Logic to Micro service (previously matched claims)
	 * PAS-14058: MATCH MORE: Create Claim to Driver Match Logic (comp claims and not already assigned to driver)
	 *
	* @name Test Claims Matching Micro Service - Test 1 -3 Claims: No match, Exiting match, DL Match
	* @scenario
	* Test Steps:
	* 1. Send JSON Request with 3 claims to the Claims Matching Micro Service
	* 2. Verify the following claims match results:
	*      --Claim 1, 1TAZ1111OHS: No Match
	*      --Claim 2, 7TZ02222OHS: Existing Match
	*      --Claim 3, 3TAZ3333OHS: DL Match
	 *     --Claim 4, 4TAZ4444OHS: COMP Match - goes to fist named insured
	*/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	@StateList(states = {Constants.States.AZ})
	public void claimsMatching_test1(@Optional("AZ") String state) throws IOException {
		//Define which JSON request to use
		//TODO - Consider using a JSON Request Builder for future tests
		String claimsRequest = new String(Files.readAllBytes(Paths.get(MICRO_SERVICE_REQUESTS + "claimsMatching_test1.json")));

		//Use 'runJsonRequestPostClaims' to send the JSON request to the Claims Assignment Micro Service
		ClaimsAssignmentResponse microServiceResponse = HelperCommon.runJsonRequestPostClaims(claimsRequest);

		//Throw the microServiceResponse to log - assists with debugging
		log.info(microServiceResponse.toString());

		//Verify the First claim is in the unmatched section
		assertThat(microServiceResponse.getUnmatchedClaims().get(0).getClaimNumber()).isEqualTo("1TAZ1111OHS");

		//Verify that the Second claim returned is an existing match and the Third claim is a DL match
		assertThat(microServiceResponse.getMatchedClaims().get(0).getMatchCode()).isEqualTo("EXISTING_MATCH");
		assertThat(microServiceResponse.getMatchedClaims().get(1).getMatchCode()).isEqualTo("COMP");
		assertThat(microServiceResponse.getMatchedClaims().get(2).getMatchCode()).isEqualTo("DL");
	}

}


