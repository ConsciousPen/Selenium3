package aaa.modules.regression.sales.auto_ss.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.service.helper.HelperCommon;
import aaa.modules.regression.service.helper.dtoClaim.ClaimsAssignmentResponse;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestOffLineClaims extends AutoSSBaseTest
{
    /**
     * * @author Chris Johns
     * @name Test Offline STUB/Mock Data Claims
     * @IMPORTANT: This test is written under the current stub structure and is subject to change
     * @scenario
     * Test Steps:
     * 1. Create a Policy with 3 drivers; 1 with no STUB data match, 2, and 3 with STUB data match
     * 2. Move time to R-63
     * 3. Run Renewal Part1 + "renewalClaimOrderAsyncJob"
     * 4. Run Claims Offline Batch Job
     * 5. Move Time to R-46
     * 6. Run Renewal Part2 + "claimsRenewBatchRecieveJob"
     * 7. Retrieve policy and enter renewal image
     * 8. Verify Claim Data is applied to the correct driver.
     * @details Clean Path. Expected Result is that claims data is applied to the correct driver
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void PAS14679_TestCase1(@Optional("AZ") String state) {
	    PurchaseTab purchaseTab = new PurchaseTab();
	    TestData testData = getPolicyTD();
	    TestData driverTab = getTestSpecificTD("TestData_DriverTab_OfflineClaim").resolveLinks();

	    //Create Customer and Policy with 3 drivers
	    mainApp().open();
	    createCustomerIndividual();
	    policy.initiate();
	    policy.getDefaultView().fillUpTo(testData, DriverTab.class, true);
	    policy.getDefaultView().fill(driverTab);

	    //Fill remaining Policy info and bind
	    policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, PurchaseTab.class, true);
	    purchaseTab.submitTab();

	    //Gather Policy details: Policy Number and expiration date
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
	    LocalDateTime policyExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusYears(1);
	    mainApp().close();

        //Move to R-63, run batch job part 1 and offline claims batch job
	    TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(63));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_CLAIMS_BATCH);

        //Move to R-46 and run batch job part 2
	    TimeSetterUtil.getInstance().nextPhase(policyExpirationDate.minusDays(46));
	    JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        //Retrieve policy
	    mainApp().open();
	    SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

	    //Enter renewal image and verify claim presence
	    PolicySummaryPage.buttonRenewals.click();
	    policy.dataGather().start();
	    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

	    //TODO: Add Claim Assertion!
    }

	/**
	 * * @author Chris Johns
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
	public void claimsMatching_test1(@Optional("AZ") String state) throws IOException {
		String DEFAULT_PATH = "src/test/resources/claimsmatch/";
		String claimsUrl = "https://claims-assignment.apps.prod.pdc.digital.csaa-insurance.aaa.com/pas-claims/v1";

		//Define which JSON request to use
		String claimsRequest = new String(Files.readAllBytes(Paths.get(DEFAULT_PATH + "NoMatch_ExistingMatch_DLMatch.json")));

		//Use runJsonRequestPostClaims to send the JSON request to the Claims Assignment Micro Service
		ClaimsAssignmentResponse microServiceResponse = HelperCommon.runJsonRequestPostClaims(claimsRequest);

		//Throw the microServiceResponse to log - assists with debugging
		log.info(microServiceResponse.toString());

		//Verify the First claim returned from CAS is in the unmatched section
		assertThat(microServiceResponse.getUnmatchedClaims().get(0).getClaimNumber()).isEqualTo("1TAZ1111OHS");

		//Verify that the Second claim returned from CAS is an existing match and the Third claim is a DL match
		assertThat(microServiceResponse.getMatchedClaims().get(0).getMatchCode()).isEqualTo("EXISTING_MATCH");
		assertThat(microServiceResponse.getMatchedClaims().get(1).getMatchCode()).isEqualTo("DL");
	}

}


