package aaa.modules.regression.sales.template.functional;

import static org.assertj.core.api.Assertions.assertThat;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.MembershipTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;

public class TestClueReportOnCopyActionsTemplate extends PolicyBaseTest {

	protected void pas8271_testClueReportOnCopyPolicyAction(PolicyType policyType, TestData tdEndorsement) {

		// Create customer and policy
		mainApp().open();
		createCustomerIndividual();
		policyType.get().createPolicy(getPolicyDefaultTD(policyType));

		// Perform endorsement to add 2nd driver
		policyType.get().createEndorsement(tdEndorsement);

		// Copy from policy and initiate data gather
		TestData tdCopy = getStateTestData(testDataManager.policy.get(policyType), "CopyFromPolicy", "TestData");
		policyType.get().policyCopy().perform(tdCopy);

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		fillAndValidate(policyType, tdCopy);

	}

	protected void pas8271_testClueReportOnCopyQuoteActionCA(PolicyType policyType, TestData td) {

		// Create customer
		mainApp().open();
		createCustomerIndividual();

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		createQuoteFillAndInitiateCopyAction(policyType, td, new DriverActivityReportsTab());

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		td.mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_POLICY.getLabel()));
		fillAndValidate(policyType, td);

	}

	protected void fillAndValidate(PolicyType policyType, TestData td) {
		policyType.get().dataGather().start();
		if (policyType.isCaProduct()) {
			NavigationPage.toViewTab(NavigationEnum.AutoCaTab.MEMBERSHIP.get());
			policyType.get().getDefaultView().getTab(MembershipTab.class).fillTab(td);
			new PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoCaTab.DRIVER_ACTIVITY_REPORTS.get());
			policyType.get().getDefaultView().getTab(DriverActivityReportsTab.class).fillTab(td);

			// Validate CLUE reports table
			assertThat(DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(1);
			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1)
					.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("HouseHold");
			assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1)
					.getCell(AutoCaMetaData.DriverActivityReportsTab.OrderClueRow.SELECT.getLabel()).controls.radioGroups.getFirst().getValue()).isEqualTo("Yes");
		} else {
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
			policyType.get().getDefaultView().getTab(RatingDetailReportsTab.class).fillTab(td);
			new aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab().calculatePremium();
			NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
			policyType.get().getDefaultView().getTab(aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab.class).fillTab(td);

			// Validate CLUE reports table
			assertThat(aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(1);
			assertThat(aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(1)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("HouseHold");
			assertThat(aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab.tableCLUEReports.getRow(1)
					.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.SELECT.getLabel()).controls.radioGroups.getFirst().getValue()).isEqualTo("Yes");
		}

	}

	protected void createQuoteFillAndInitiateCopyAction(PolicyType policyType, TestData td, Tab driverActivityReportsTab) {
		policyType.get().initiate();
		policyType.get().getDefaultView().fillUpTo(td, driverActivityReportsTab.getClass(), true);
		driverActivityReportsTab.saveAndExit();
		policyType.get().copyQuote();
	}

//	protected TestData adjustTDCopyPolicyCA(TestData td) {
//
//	}

}
