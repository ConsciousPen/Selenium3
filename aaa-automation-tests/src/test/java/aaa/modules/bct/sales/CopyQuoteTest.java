package aaa.modules.bct.sales;

import aaa.common.Tab;
import aaa.common.metadata.SearchMetaData;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BackwardCompatibilityBaseTest;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.verification.CustomAssert;

public class CopyQuoteTest extends BackwardCompatibilityBaseTest {

	@Test
	public void BCT_ONL_023_CopyQuote() {
		String quoteNumber = getPoliciesByQuery("BCT_ONL_023_CopyQuote", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_SS.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_SS);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);

		policy.copyQuote().perform(getStateTestData(tdPolicy, "CopyFromQuote", "TestData"));
		policy.dataGather().start();
		Tab.buttonSaveAndExit.click();

		String newQuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		CustomAssert.assertTrue("Quote was copied", !newQuoteNumber.equals(quoteNumber));
	}

	@Test
	public void BCT_ONL_025_CopyQuote() {
		String quoteNumber = getPoliciesByQuery("BCT_ONL_025_CopyQuote", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.AUTO_CA_SELECT.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);

		policy.copyQuote().perform(getStateTestData(tdPolicy, "CopyFromQuote", "TestData"));
		policy.dataGather().start();
		Tab.buttonSaveAndExit.click();

		String newQuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		CustomAssert.assertTrue("Quote was copied", !newQuoteNumber.equals(quoteNumber));
	}

	@Test
	public void BCT_ONL_040_CopyQuote() {
		String quoteNumber = getPoliciesByQuery("BCT_ONL_040_CopyQuote", "SelectPolicy").get(0);
		IPolicy policy = PolicyType.HOME_SS_HO3.get();
		TestData tdPolicy = testDataManager.policy.get(PolicyType.HOME_SS_HO3);

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);

		policy.copyQuote().perform(getStateTestData(tdPolicy, "CopyFromQuote", "TestData"));
		policy.dataGather().start();
		Tab.buttonSaveAndExit.click();

		String newQuoteNumber = PolicySummaryPage.labelPolicyNumber.getValue();
		CustomAssert.assertTrue("Quote was copied", !newQuoteNumber.equals(quoteNumber));
	}
}
