package aaa.modules.regression.sales.template.functional;

import java.util.ArrayList;
import java.util.List;
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

	public TestData getTwoAssignmentsTestData() {
		TestData  firstAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");
		TestData  secondAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");

		List<TestData> listDataAssignmentTab = new ArrayList<>();
		listDataAssignmentTab.add(firstAssignment);
		listDataAssignmentTab.add(secondAssignment);

		return new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", listDataAssignmentTab);
	}
}