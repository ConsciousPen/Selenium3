package aaa.modules.bct.service;

import static aaa.common.enums.Constants.States.*;
import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.Tab;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.BackwardCompatibilityBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;

@Deprecated
public class CopyQuoteTest extends BackwardCompatibilityBaseTest {

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_023_CopyQuote(@Optional("") String state) {
		mainApp().open();

		String quoteNumber = getPoliciesByQuery("BCT_ONL_023_CopyQuote", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_SS);

		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);

		policy.copyQuote().perform(getStateTestData(tdPolicy, "CopyFromQuote", "TestData"));
		policy.dataGather().start();
		Tab.buttonSaveAndExit.click();

		String newQuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		assertThat(newQuoteNumber).as("Quote was copied").isNotEqualTo(quoteNumber);
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {CA})
	public void BCT_ONL_025_CopyQuote(@Optional("") String state) {
		mainApp().open();

		String quoteNumber = getPoliciesByQuery("BCT_ONL_025_CopyQuote", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);

		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);

		policy.copyQuote().perform(getStateTestData(tdPolicy, "CopyFromQuote", "TestData"));
		policy.dataGather().start();
		Tab.buttonSaveAndExit.click();

		String newQuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		assertThat(newQuoteNumber).as("Quote was copied").isNotEqualTo(quoteNumber);
	}

	@Parameters({"state"})
	@Test
	@StateList(states = {AZ, CA, CO, CT, DC, DE, ID, IN, KS, KY, MD, MT, NJ, NV, NY, OH, OK, OR, PA, SD, UT, VA, WV, WY})
	public void BCT_ONL_040_CopyQuote(@Optional("") String state) {
		mainApp().open();

		String quoteNumber = getPoliciesByQuery("BCT_ONL_040_CopyQuote", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.HOME_SS_HO3);

		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);

		policy.copyQuote().perform(getStateTestData(tdPolicy, "CopyFromQuote", "TestData"));
		policy.dataGather().start();
		Tab.buttonSaveAndExit.click();

		String newQuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		assertThat(newQuoteNumber).as("Quote was copied").isNotEqualTo(quoteNumber);
	}
}
