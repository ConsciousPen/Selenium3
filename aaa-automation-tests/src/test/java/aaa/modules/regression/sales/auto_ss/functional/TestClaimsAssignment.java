package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate.SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE;
import static toolkit.verification.CustomAssertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestClaimsAssignment extends AutoSSBaseTest {
	@SuppressWarnings("SpellCheckingInspection")
	private static final String MICRO_SERVICE_REQUESTS = "src/test/resources/feature/claimsmatch/claim_micro_service_requests/";
	private static final String claimsUrl = "https://claims-assignment-pas-18300-permissive-use.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1";
	//TODO:gunxgar - change it back, before creating a pull request

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
	 * @scenario
	 * Test Steps:
	 * 1. Send JSON Request with 3 claims to the Claims Matching Micro Service
	 * 2. Verify the following claims match results:
	 *      --Claim 1, 1TAZ1111OHS: No Match
	 *      --Claim 2, 7TZ02222OHS: Existing Match
	 *      --Claim 3, 3TAZ3333OHS: DL Match
	 *      --Claim 4, 4TAZ4444OHS: COMP Match - goes to fist named insured
	 *      --Claim 5, 1TZ90531OHS: LASTNAME_FIRSTNAME_DOB Match
	 *      --Cliam 6, 1TZ90411OHS: LASTNAME_FIRSTNAME_YOB Match
	 *      --Claim 7-11,  17894- 2, 3, 5, 7, & 9: UNMATCHED
	 *      --Claim 12,    17894- 1: LASTNAME_FIRSTNAME
	 *      --Claim 13,    17894- 4: LASTNAME_FIRSTINITAL_DOB
	 *      --Claim 14-15, 17894- 6 & 8: Unmatched (PAS-21435 Removed LASTNAME_YOB Match)
	 *      --Claim 16-18, 18431- 1, 2, 3: PERMISSIVE_USE Match
	 *      --Claim 19, 18431- 4: UNMATCHED Match with PU = Yes, because dateOfLoss of Claim = -1 Day of dateOfLoss Parameter
	 *      --Claim 20, 18431- 5: UNMATCHED Match
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-14679,PAS-18391,PAS-14058,PAS-8310,PAS-17894,PAS-18300")
	@StateList(states = {Constants.States.AZ})
	public void pas14679_testMSClaimsAssignment(@Optional("AZ") String state) throws IOException {

		// Toggle ON PermissiveUse Logic. NOTE: dateOfLoss parameter value is set manually in JSON request
		DBService.get().executeUpdate(SQL_UPDATE_PERMISSIVEUSE_DISPLAYVALUE);

		//Define which JSON request to use
		String claimsRequest = new String(Files.readAllBytes(Paths.get(MICRO_SERVICE_REQUESTS + "PAS14679_testMSClaimsAssignment.json")));

		//Use 'runJsonRequestPostClaims' to send the JSON request to the Claims Assignment Micro Service
		ClaimsAssignmentResponse microServiceResponse = runJsonRequestPostClaims(claimsRequest);

		//Throw the microServiceResponse to log - assists with debugging
		log.info(microServiceResponse.toString());

		//Create a list of all the expected UNMATCHED claim numbers
		String[] expectedClaimNumbers = {"1TAZ1111OHS", "17894-2222OHS", "17894-3333OHS", "17894-55555OHS", "17894-66666OHS", "17894-77777OHS", "17894-88888OHS", "17894-99999OHS", "18431-44444OHS", "18431-55555OHS"};
		ArrayList<String> expectedUnmatchedClaims = new ArrayList<>();
		expectedUnmatchedClaims.addAll(Arrays.asList(expectedClaimNumbers));

		//Create a list of all the actual UNMATCHED claim numbers
		ArrayList<String> actualUnmatchedClaims = new ArrayList<>();
		int x = 0;
		while (x < microServiceResponse.getUnmatchedClaims().size())
		{
			String claimNumber = microServiceResponse.getUnmatchedClaims().get(0+x).getClaimNumber();
			log.info(claimNumber);
			actualUnmatchedClaims.add(claimNumber);
			x++;
		}

		//Verify the actual UNMATCHED claims equal the expected UNMATCHED claims
		//PAS-21435 - Removed LASTNAME_YOB match logic. These claims will now be unmatched
		assertThat(actualUnmatchedClaims).isEqualTo(expectedUnmatchedClaims);

		//Create a list of all the expected MATCH CODES (Last 3: PERMISSIVE_USE to cover all possible cases of PU)
		String[] expectedCodes = {"EXISTING_MATCH", "COMP", "DL", "LASTNAME_FIRSTNAME_DOB", "LASTNAME_FIRSTNAME_YOB", "LASTNAME_FIRSTNAME", "LASTNAME_FIRSTINITAL_DOB", "PERMISSIVE_USE", "PERMISSIVE_USE", "PERMISSIVE_USE"};
		ArrayList<String> expectedMatchCodes = new ArrayList<>();
		expectedMatchCodes.addAll(Arrays.asList(expectedCodes));

		//Create a list of all the actual MATCH CODES
		ArrayList<String> actualMatchCodes = new ArrayList<>();
		int y = 0;
		while (y < microServiceResponse.getMatchedClaims().size())
		{
			String matchcode = microServiceResponse.getMatchedClaims().get(0+y).getMatchCode();
			log.info(matchcode);
			actualMatchCodes.add(matchcode);
			y++;
		}

		//Verify the actual MATCH CODES equal the expected MATCH CODES
		//PAS-14679 - Match Logic: DL Number
		//PAS-14058 - Match Logic: COMP
		//PAS-8310  - Match Logic: LASTNAME_FIRSTNAME_DOB, LASTNAME_FIRSTNAME_YOB
		//PAS-17894 - Match Logic: LASTNAME_FIRSTNAME, LASTNAME_FIRSTINITAL_DOB, & LASTNAME_YOB
		//PAS-18300 - Match Logic: PERMISSIVE_USE
		assertThat(actualMatchCodes).isEqualTo(expectedMatchCodes);
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

