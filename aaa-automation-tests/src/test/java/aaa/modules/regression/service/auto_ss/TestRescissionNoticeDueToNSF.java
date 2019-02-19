package aaa.modules.regression.service.auto_ss;

import aaa.common.enums.Constants;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.DocGenEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.actiontabs.CancellationActionTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;

/**
 * @author mlaptsionak
 * @name Flat Cancel - Rescission Notice Due to Non-Sufficient Funds (NSF) ("D-T-AU-SS-CT-746")
 * @scernario 1. Login to the PAS and initiate an Auto Signature Series Quote
 * 2. Enter all the mandatory details, order the reports and rate the policy
 * 3. Make payment and Bind the polic
 * 4. DD1-20 Login to PAS as an user having Privilege 'Policy Rescind Cancellation' (E34)
 * 5. Select 'Cancellation' from Move To drop down.
 * 6. Select Cancellation Reason as New Business Rescission - NSF on Down Payment
 * 7. Select Cancellation Date = Policy Effective Date & Cancel the policy
 * 8. Log out from user
 * 9. Run aaaDocGenBatchJob
 * Expected results
 * 9.#V1 - Rescission Notice document (AH60XXA 03 16) is generated and stored in FastLane/Cancellation & Rescission & Reinstatement eFolder.
 * 6.#V2 - Cancellation Warning Notice (AA34CTA) document is generated and stored in eFolder.
 */

public class TestRescissionNoticeDueToNSF extends AutoSSBaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = {Constants.States.CA})
	@Test(groups = {Groups.REGRESSION, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS)
	public void testRescissionNoticeDueToNSF(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		String policyNumber = createPolicy();
		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getStartTime().plusMonths(1).minusDays(20));
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		policy.cancel().perform(getTestSpecificTD("TestData").adjust(TestData.makeKeyPath(new CancellationActionTab().getMetaKey(), AutoSSMetaData.CancellationActionTab.CANCELLATION_EFFECTIVE_DATE.getLabel()),
				TimeSetterUtil.getInstance().getStartTime().format(DateTimeUtils.MM_DD_YYYY)));
		JobUtils.executeJob(Jobs.aaaDocGenBatchJob);
		DocGenHelper.verifyDocumentsGenerated(true, true, policyNumber, DocGenEnum.Documents.AH60XXA, DocGenEnum.Documents.AA34CTA);
	}

}
