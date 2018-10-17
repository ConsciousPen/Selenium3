package aaa.modules.bct.sales.auto_ss;

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
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_023_CopyQuote(@Optional("AZ") String state) {
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
