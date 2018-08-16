package aaa.modules.regression.sales.template.functional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import aaa.helpers.docgen.AaaDocGenEntityQueries;
import aaa.helpers.docgen.DocGenHelper;
import aaa.helpers.xml.model.Document;
import aaa.main.modules.policy.home_ss.defaulttabs.EndorsementTab;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.DefaultMarkupParser;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonTemplateMethods extends PolicyBaseTest {
	private MembershipTab membershipTab = new MembershipTab();

 	protected String openAppAndCreatePolicy(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		return createPolicy(testData);
	}

	protected String openAppAndCreatePolicy() {
		return openAppAndCreatePolicy(getPolicyTD());
	}

	protected String openAppAndCreateConversionPolicy(TestData testData) {
		mainApp().open();
		createCustomerIndividual();
		return createConversionPolicy(testData);
	}

	protected String openAppAndCreateConversionPolicy() {
		return openAppAndCreatePolicy(getConversionPolicyDefaultTD());
	}

	protected void createQuoteAndFillUpTo(TestData testData, Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, tab, true);
	}

	protected void createQuoteAndFillUpTo(Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(getPolicyTD(), tab, true);
	}

	protected void createConversionQuoteAndFillUpTo(TestData testData, Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fillUpTo(testData, tab, false);
	}

	protected void createConversionQuoteAndFillUpTo(Class<? extends Tab> tab) {
		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fillUpTo(getConversionPolicyDefaultTD(), tab, false);
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

	protected void moveTimeAndRunRenewJobs(LocalDateTime nextPhaseDate) {
		TimeSetterUtil.getInstance().nextPhase(nextPhaseDate);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
		JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
	}


	public TestData getTwoAssignmentsTestData() {
		TestData  firstAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");
		TestData  secondAssignment = getPolicyDefaultTD().getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver");

		List<TestData> listDataAssignmentTab = new ArrayList<>();
		listDataAssignmentTab.add(firstAssignment);
		listDataAssignmentTab.add(secondAssignment);

		return new SimpleDataProvider().adjust("DriverVehicleRelationshipTable", listDataAssignmentTab);
	}

	public void searchForPolicy(String policyNumber) {
		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNumber);
	}

	protected void openAppNonPrivilegedUser(String privilege) {
		mainApp().open(initiateLoginTD()
				.adjust("User","qa_roles")
				.adjust("Groups", privilege)
				.adjust("UW_AuthLevel", "01")
				.adjust("Billing_AuthLevel", "01")
		);
	}
}
