package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestOutOfStateLicenseMVR extends AutoSSBaseTest {

	/**
	 * @author Josh Carpenter
	 * @name Test MVR reports for auto policies that have customer with out of state license
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS quote
	 * 3. Fill with valid out of state license containing special characters
	 * 4. Bind policy
	 * 5. Age policy to MVR order renewal date (R-63)
	 * 6. Run Renewal jobs part 1 & 2
	 * 7. Open policy in data gathering mode
	 * 8. Navigate to DAR page
	 * 9. Validate MVR status
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-10099")
	public void pas10099_testOutOfStateLicenseWA(@Optional("") String state) {

		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), "WA")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "LIU**Y*310L1");

		testOutOfStateLicenseMVR(td);

	}

	/**
	 * @author Josh Carpenter
	 * @name Test MVR reports for auto policies that have customer with out of state license
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS quote
	 * 3. Fill with valid out of state license containing special characters
	 * 4. Bind policy
	 * 5. Age policy to MVR order renewal date (R-63)
	 * 6. Run Renewal jobs part 1 & 2
	 * 7. Open policy in data gathering mode
	 * 8. Navigate to DAR page
	 * 9. Validate MVR status
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-10099")
	public void pas10099_testOutOfStateLicenseMT(@Optional("") String state) {

		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), "MT")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "A3B78- -2");

		testOutOfStateLicenseMVR(td);

	}

	/**
	 * @author Josh Carpenter
	 * @name Test MVR reports for auto policies that have customer with out of state license
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS quote
	 * 3. Fill with valid out of state license containing special characters
	 * 4. Bind policy
	 * 5. Age policy to MVR order renewal date (R-63)
	 * 6. Run Renewal jobs part 1 & 2
	 * 7. Open policy in data gathering mode
	 * 8. Navigate to DAR page
	 * 9. Validate MVR status
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-10099")
	public void pas10099_testOutOfStateLicenseWY(@Optional("") String state) {

		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), "WY")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "654879-645");

		testOutOfStateLicenseMVR(td);

	}

	private void testOutOfStateLicenseMVR(TestData td) {

		// Create customer and policy
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(td);

		// Create renewal image at R-63 with renewal jobs
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(PolicySummaryPage.getExpirationDate()));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		// Open renewal image and navigate to DAR page
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		// Validate MVR response is not empty (was ordered)
		assertThat(DriverActivityReportsTab.resultMsgMVRReports.getValue()).contains("Reports order is successful");

	}

}
