package aaa.modules.regression.sales.auto_ss.functional;

import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.http.HttpStub;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyCreationBig;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.AZ)
public class TestOffLineClaims extends AutoSSBaseTest
{
    /**
     * * @author Chris Johns
     * @name Test Membership Renewal Consideration
     * @scenario
     * Preconditions: 1.Open App, 2.Create Customer, 3.Create Policy, 4.Close App
     * Test Steps:
     * 1.
     * 2.
     * 3.
     * 4.
     * 5.
     * @details Clean Path. Expected Result is
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14679")
    public void PAS14679_TestCase1(@Optional("AZ") String state) {

//	    TestData testData = getPolicyTD()
//			    .adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(), "Bruce")
//			    .adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(), "Banner")

	    TestData td =  getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
			    .adjust(AutoSSMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click")
			    .adjust(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), "Bruce")
			    .adjust(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), "Banner")
			    .adjust(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel(), "A12345222")
			    .mask(AutoSSMetaData.DriverTab.NAMED_INSURED.getLabel());
	    TestData testData = getTestSpecificTD("td").adjust(DriverTab.class.getSimpleName(), td);


	    //Policy Setup
        mainApp().open();
        createCustomerIndividual();
        createPolicy(testData);

        //Gather Policy details
        String policyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
	    LocalDateTime policyExpirationDate = TimeSetterUtil.getInstance().getCurrentTime().plusDays(360);
	    mainApp().close();

        //Move to R-63, run batch job part 1, and offline claims batch job
	    moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(63));
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        HttpStub.executeSingleBatch(HttpStub.HttpStubBatch.OFFLINE_AAA_CLAIMS_BATCH);

        //Move to R-46 and run batch job part 2
	    moveTimeAndRunRenewJobs(policyExpirationDate.minusDays(46));
	    JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

        //Retrieve policy
	    mainApp().open();
	    SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);

	    //Enter renewal image and verify claim presence
	    PolicySummaryPage.buttonRenewals.click();
	    policy.dataGather().start();
	    NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());

    }
}
