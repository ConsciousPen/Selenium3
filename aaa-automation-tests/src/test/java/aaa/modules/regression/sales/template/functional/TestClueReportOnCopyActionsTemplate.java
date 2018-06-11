package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestClueReportOnCopyActionsTemplate extends PolicyBaseTest {

	protected void pas8271_testClueReportOnCopyPolicyAction(TestData tdEndorsement) {

		// Create customer and policy
		mainApp().open();
		createCustomerIndividual();
		getPolicyType().get().createPolicy(getPolicyDefaultTD());

		// Perform endorsement to add 2nd driver
		getPolicyType().get().createEndorsement(tdEndorsement);

		// Copy from policy and initiate data gather
		TestData tdCopy = getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromPolicy", "TestData");
		getPolicyType().get().policyCopy().perform(tdCopy);

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		fillAndValidateCLUETable(tdCopy);

	}

	protected void pas8271_testClueReportOnCopyQuoteActionCA(TestData td) {

		// Create customer
		mainApp().open();
		createCustomerIndividual();

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		createQuoteFillAndInitiateCopyAction(td, new DriverActivityReportsTab());

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		fillAndValidateCLUETable(td);

	}

	protected void fillAndValidateCLUETable(TestData td) {
		getPolicyType().get().dataGather().start();
		if (getPolicyType().isCaProduct()) {
			NavigationPage.toViewTab(NavigationEnum.AutoCaTab.MEMBERSHIP.get());
			getPolicyType().get().getDefaultView().getTab(MembershipTab.class).fillTab(td);
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
			getPolicyType().get().getDefaultView().getTab(DriverActivityReportsTab.class).fillTab(td);

			// Validate CLUE reports table
			assertThat(DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(1);
			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1)
					.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("HouseHold");
			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1)
					.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.SELECT.getLabel()).controls.radioGroups.getFirst().getValue()).isEqualTo("Yes");
		} else {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
			getPolicyType().get().getDefaultView().getTab(RatingDetailReportsTab.class).fillTab(td);
			new aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
			getPolicyType().get().getDefaultView().getTab(aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab.class).fillTab(td);

			// Validate CLUE reports table
			assertThat(aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(1);
			assertThat(aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(1)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("HouseHold");
			assertThat(aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(1)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.SELECT.getLabel()).controls.radioGroups.getFirst().getValue()).isEqualTo("Yes");
		}

	}

	protected void createQuoteFillAndInitiateCopyAction(TestData td, Tab driverActivityReportsTab) {
		getPolicyType().get().initiate();
		getPolicyType().get().getDefaultView().fillUpTo(td, driverActivityReportsTab.getClass(), true);
		driverActivityReportsTab.saveAndExit();
		getPolicyType().get().copyQuote().perform(getStateTestData(testDataManager.policy.get(getPolicyType()), "CopyFromQuote", "TestData"));
	}

	protected TestData getCAEndorsementTD(TestData tdDriverTab, TestData tdSpecific) {
		tdDriverTab.adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click")
				.adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Sally")
				.adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Smith")
				.mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel());

		return tdSpecific.adjust(DriverTab.class.getSimpleName(), tdDriverTab)
				.adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()), "I Agree")
				.adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY.getLabel()), "click");
	}

	protected TestData getCACopyQuoteTD(TestData tdDefault, List<TestData> tdDriverTab) {
		tdDriverTab.get(1)
				.adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Sally")
				.adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Smith")
				.mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel());

		return tdDefault.adjust(DriverTab.class.getSimpleName(), tdDriverTab);
	}

}
