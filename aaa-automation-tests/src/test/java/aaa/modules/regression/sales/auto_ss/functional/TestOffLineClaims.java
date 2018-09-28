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
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;


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

}


