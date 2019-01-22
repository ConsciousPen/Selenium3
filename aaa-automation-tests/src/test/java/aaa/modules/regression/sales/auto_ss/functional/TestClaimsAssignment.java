package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.modules.regression.sales.template.functional.TestOfflineClaimsTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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
}

