package aaa.modules.regression.sales.template.functional;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class CommonTemplateMethods extends PolicyBaseTest {
	private MembershipTab membershipTab = new MembershipTab();

	protected String createPolicyPreconds(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		return createPolicy(testData);
	}

	protected void createQuoteAndFillUpTo(TestData testData, Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, tab, true);
	}

	protected void findAndRateQuote(TestData testData, String quoteNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNumber);
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.ASSIGNMENT.get());
		policy.getDefaultView().fillFromTo(testData, AssignmentTab.class, PremiumAndCoveragesTab.class, true);
	}

	public TestData getTestWithSinceMembership(TestData testData) {
		// Start of  MembershipTab
		TestData addMemberSinceDialog = new SimpleDataProvider()
				.adjust(AutoCaMetaData.MembershipTab.AddMemberSinceDialog.MEMBER_SINCE.getLabel(), new DefaultMarkupParser().parse("$<today:MM/dd/yyyy>"))
				.adjust(AutoCaMetaData.MembershipTab.AddMemberSinceDialog.BTN_OK.getLabel(), "click")
				.adjust(AutoCaMetaData.MembershipTab.AddMemberSinceDialog.BTN_CANCEL.getLabel(), "click");
		TestData aaaMembershipReportRow = new SimpleDataProvider()
				.adjust("Action", "Add Member Since")
				.adjust("AddMemberSinceDialog", addMemberSinceDialog);
		TestData testMembershipTab = testData.getTestData(membershipTab.getMetaKey())
				.adjust(AutoCaMetaData.MembershipTab.AAA_MEMBERSHIP_REPORT.getLabel(), aaaMembershipReportRow);
		// End of membershipTab
		// Add new membership tab to td
		return testData.adjust(membershipTab.getMetaKey(), testMembershipTab);
	}
}
