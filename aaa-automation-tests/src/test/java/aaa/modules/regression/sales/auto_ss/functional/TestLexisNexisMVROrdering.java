package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
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
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestLexisNexisMVROrdering extends AutoSSBaseTest {

	/**
	 * @author Josh Carpenter
	 * @name Test MVR reports for auto policies that have customer with out of state license
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS quote
	 * 3. Fill with valid out of state license (WA)
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
	public void pas10099_testLexisNexisMVROrderingWA(@Optional("") String state) {

		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), "WA")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "LIU**Y*310L1");

		testLexisNexisMVROrdering(td);

	}

	/**
	 * @author Josh Carpenter
	 * @name Test MVR reports for auto policies that have customer with out of state license
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS quote
	 * 3. Fill with valid out of state license (CA)
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
	public void pas10099_testLexisNexisMVROrderingCA(@Optional("") String state) {

		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), "CA")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "C6512304");

		testLexisNexisMVROrdering(td);

	}

	/**
	 * @author Josh Carpenter
	 * @name Test MVR reports for auto policies that have customer with out of state license
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto SS quote
	 * 3. Fill with valid out of state license (FL)
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
	public void pas10099_testLexisNexisMVROrderingFL(@Optional("") String state) {

		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_STATE.getLabel()), "FL")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), "C320025874125");

		testLexisNexisMVROrdering(td);

	}

	private void testLexisNexisMVROrdering(TestData td) {

		// Create customer and policy
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy(td);
		LocalDateTime expDate = PolicySummaryPage.getExpirationDate();

		// Create renewal image at R-63 with renewal jobs
		mainApp().close();
		TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewReportsDate(expDate));
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);

		// Open renewal image and navigate to DAR page
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		PolicySummaryPage.buttonRenewals.click();
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());

		// Validate MVR message area is not present (should not be there in this specific scenario)
		assertThat(DriverActivityReportsTab.resultMsgMVRReports).isPresent(false);

	}

}
