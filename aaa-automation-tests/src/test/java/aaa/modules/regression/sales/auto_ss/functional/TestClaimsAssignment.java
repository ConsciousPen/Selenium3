package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.RestRequestMethodTypes;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.rest.JsonClient;
import aaa.helpers.rest.RestRequestInfo;
import aaa.helpers.rest.dtoClaim.ClaimsAssignmentResponse;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.dtoClaim.ClaimsAssignmentResponse;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class TestClaimsAssignment extends AutoSSBaseTest {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String MICRO_SERVICE_REQUESTS = "src/test/resources/feature/claimsmatch/claim_micro_service_requests/";
	@SuppressWarnings("SpellCheckingInspection")
	private static final String MICRO_SERVICE_REQUESTS = "src/test/resources/feature/claimsmatch/claim_micro_service_requests/";
	private static final String claimsUrl = "https://claims-assignment.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1";

    /**
     * * @author Chris Johns
     * * @author Kiruthika Rajendran
     * <p>
     * PAS-14679: MATCH MORE: Create Claim to Driver Match Logic (use DL # when not comp/not already assigned to driver)
     * PAS-18391: Add Existing Logic to Micro service (previously matched claims)
     * PAS-14058: MATCH MORE: Create Claim to Driver Match Logic (comp claims and not already assigned to driver)
     * PAS-8310: MATCH MORE: Create Claim to Driver Match Logic (not comp/not already assigned to driver/not DL) (part 1)
     *
     * @name Test Claims Matching Micro Service - Test 1 -5 Claims: No match, Exiting match, DL Match, LASTNAME_FIRSTNAME_DOB Match, LASTNAME_FIRSTNAME_YOB Match
     * @scenario Test Steps:
     * 1. Send JSON Request with 5 claims to the Claims Matching Micro Service
     * 2. Verify the following claims match results:
     * --Claim 1, 1TAZ1111OHS: No Match
     * --Claim 2, 7TZ02222OHS: Existing Match
     * --Claim 3, 3TAZ3333OHS: DL Match
     * --Claim 4, 1TZ90531OHS: LASTNAME_FIRSTNAME_DOB Match
     * --Cliam 5, 1TZ90411OHS: LASTNAME_FIRSTNAME_YOB Match
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
    @StateList(states = {Constants.States.AZ})
    public void claimsMatchingMSTest(@Optional("AZ") String state) throws IOException {
        //Define which JSON request to use
        //TODO - Consider using a JSON Request Builder for future tests
        String claimsRequest = new String(Files.readAllBytes(Paths.get(MICRO_SERVICE_REQUESTS + "claimsMatching_test1.json")));
	/**
	 * * @author Chris Johns
	 *
	 * PAS-14679: MATCH MORE: Create Claim to Driver Match Logic (use DL # when not comp/not already assigned to driver)
	 * PAS-18391: Add Existing Logic to Micro service (previously matched claims)
	 * PAS-14058: MATCH MORE: Create Claim to Driver Match Logic (comp claims and not already assigned to driver)
	 * PAS-17894: MATCH MORE: Create Claim to Driver Match Logic (not comp/not already assigned to driver/not DL) (part 2)
	 *
	 * @name Test Claims Matching Micro Service - Test 1 -3 Claims: No match, Exiting match, DL Match
	 * @scenario
	 * Test Steps:
	 * 1. Send JSON Request with 3 claims to the Claims Matching Micro Service
	 * 2. Verify the following claims match results:
	 *      --Claim 1, 1TAZ1111OHS: No Match
	 *      --Claim 2, 7TZ02222OHS: Existing Match
	 *      --Claim 3, 3TAZ3333OHS: DL Match
	 *      --Claim 4, 4TAZ4444OHS: COMP Match - goes to fist named insured
	 *      --Claim 17894- 2, 3, 5, 7, & 9: UNMATCHED
	 *      --Claim 17894- 1: LASTNAME_FIRSTNAME
	 *      --Claim 17894- 4: LASTNAME_FIRSTINITAL_DOB
	 *      --Claim 17894- 6 & 8: LASTNAME_YOB
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-12465"})
	@StateList(states = {Constants.States.AZ})
	public void claimsMatching_test1(@Optional("AZ") String state) throws IOException {

		//Define which JSON request to use
		//TODO - Consider using a JSON Request Builder for future tests
		String claimsRequest = new String(Files.readAllBytes(Paths.get(MICRO_SERVICE_REQUESTS + "claimsMatching_test1.json")));

		//Use 'runJsonRequestPostClaims' to send the JSON request to the Claims Assignment Micro Service
		ClaimsAssignmentResponse microServiceResponse = runJsonRequestPostClaims(claimsRequest);
        //Use 'runJsonRequestPostClaims' to send the JSON request to the Claims Assignment Micro Service
        ClaimsAssignmentResponse microServiceResponse = HelperCommon.runJsonRequestPostClaims(claimsRequest);

        //Throw the microServiceResponse to log - assists with debugging
        log.info(microServiceResponse.toString());

        //Verify the First claim is in the unmatched section
        assertThat(microServiceResponse.getUnmatchedClaims().get(0).getClaimNumber()).isEqualTo("1TAZ1111OHS");
        assertThat(microServiceResponse.getUnmatchedClaims().get(0).getMatchCode()).isEqualTo("UNMATCHED");

        //Verify that the Second claim returned is an existing match and the Third claim is a DL match
        assertThat(microServiceResponse.getMatchedClaims().get(0).getMatchCode()).isEqualTo("EXISTING_MATCH");
        assertThat(microServiceResponse.getMatchedClaims().get(1).getMatchCode()).isEqualTo("COMP");
        assertThat(microServiceResponse.getMatchedClaims().get(2).getMatchCode()).isEqualTo("DL");
        assertThat(microServiceResponse.getMatchedClaims().get(3).getMatchCode()).isEqualTo("LASTNAME_FIRSTNAME_DOB");
        assertThat(microServiceResponse.getMatchedClaims().get(4).getMatchCode()).isEqualTo("LASTNAME_FIRSTNAME_YOB");

		//PAS-17894 - LASTNAME_FIRSTNAME, LASTNAME_FIRSTINITAL_DOB, & LASTNAME_YOB
		assertThat(microServiceResponse.getMatchedClaims().get(5).getMatchCode()).isEqualTo("LASTNAME_FIRSTNAME");
		assertThat(microServiceResponse.getMatchedClaims().get(6).getMatchCode()).isEqualTo("LASTNAME_FIRSTINITAL_DOB");
		assertThat(microServiceResponse.getMatchedClaims().get(7).getMatchCode()).isEqualTo("LASTNAME_YOB"); //DOB is exact match
		assertThat(microServiceResponse.getMatchedClaims().get(8).getMatchCode()).isEqualTo("LASTNAME_YOB"); //Only YOB matches

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


