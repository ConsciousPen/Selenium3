package aaa.modules.bct.sales.home_ss.ho3;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;

public class CopyQuote extends BackwardCompatibilityBaseTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	/**
	 * @author Deloite
	 * @name Copy Quote
	 * @scenario
	 * 1. User retrieves a Quote
	 * 2. User should select "Copy from Quote" from the drop down in policy consolidated view.
	 * 3. User need to initiate copy from quote and fill necessary details.
	 * Check:
	 * 1. Quote need to be retrieved succesfully
	 * 2. Copy from quote need to be selected
	 * 3. New quote need to be initated succesfully
	 * @param state
	 */
	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CA, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_040_CopyQuote(@Optional("") String state) {
		String quoteNumber = getPoliciesByQuery(getMethodName(), SELECT_POLICY_QUERY_TYPE).get(0);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);

		policy.copyQuote().perform(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromQuote", "TestData"));
		policy.dataGather().start();
		Tab.buttonSaveAndExit.click();

		String newQuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		assertThat(newQuoteNumber).as("Quote was copied").isNotEqualTo(quoteNumber);
	}
}
