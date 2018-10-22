package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.dtoClaim.ClaimsAssignmentResponse;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class TestClaimsAssignment extends AutoSSBaseTest {
    @SuppressWarnings("SpellCheckingInspection")
    private static final String MICRO_SERVICE_REQUESTS = "src/test/resources/feature/claimsmatch/claim_micro_service_requests/";

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
    }
}


