package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyCreationBig;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestClueReportForCopiedPolicy extends AutoSSBaseTest {

    /**
     * @author Josh Carpenter
     * @name Test that CLUE reports do not carry over on 'Copy Policy' action for SS Auto policies
     * @scenario
     * 1. Create Customer
     * 2. Create Auto SS Policy
     * 3. Create endorsement to add a driver
     * 4. Copy from policy
     * 5. Order reports on Rating Details Report tab
     * 6. Calculate premium
     * 7. Order reports on DAR page
     * 8. Verify CLUE table contains a single row with 'HouseHold' for 'Order Type'
     * 9. Verify CLUE table 'Select' radio button defaults to 'Yes'
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-8271")
    public void pas8271_testClueReportOnCopyPolicyAction(@Optional("") String state) {

        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        // Get state-specific 2nd driver and adjust test data
        TestData tdDriverTab =  getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
                .adjust(AutoSSMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click")
                .adjust(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), "Sally")
                .adjust(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), "Smith")
                .mask(AutoSSMetaData.DriverTab.NAMED_INSURED.getLabel());
        TestData tdEndorsement = getTestSpecificTD("TestData").adjust(DriverTab.class.getSimpleName(), tdDriverTab);
        TestData tdCopy = getPolicyTD("CopyFromPolicy", "TestData");

        // Perform endorsement to add 2nd driver
        policy.createEndorsement(tdEndorsement);

        // Initiate 'Copy From Policy' action
        policy.policyCopy().perform(tdCopy);

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		fillAndValidate(tdCopy);

    }

    /**
     * @author Josh Carpenter
     * @name Test that CLUE reports do not carry over on 'Copy Quote' action for SS Auto policies
     * @scenario
     * 1. Create Customer
     * 2. Initiate Auto SS Quote
     * 3. Fill quote with 2 drivers up to DAR page
     * 4. Save and Exit
     * 5. Initiate 'Copy From Quote' Action
     * 6. Order reports on Rating Details Report tab
     * 7. Calculate premium
     * 8. Order reports on DAR page
     * 9. Verify CLUE table contains a single row with 'HouseHold' for 'Order Type'
     * 10. Verify CLUE table 'Select' radio button defaults to 'Yes'
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-8271")
    public void pas8271_testClueReportOnCopyQuoteAction(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();

		List<TestData> tdDriversTab = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName());
		tdDriversTab.get(1)
				.adjust(AutoSSMetaData.DriverTab.FIRST_NAME.getLabel(), "Sally")
				.adjust(AutoSSMetaData.DriverTab.LAST_NAME.getLabel(), "Smith")
				.mask(AutoSSMetaData.DriverTab.NAMED_INSURED.getLabel());
		TestData td = getPolicyDefaultTD().adjust(DriverTab.class.getSimpleName(), tdDriversTab);

		// Initiate Quote  with 2 drivers and fill up to DAR page
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, DriverActivityReportsTab.class, true);
		new DriverActivityReportsTab().saveAndExit();

		// Initiate 'Copy From Quote' action
		policy.copyQuote();

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		td.mask(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.CUSTOMER_AGREEMENT.getLabel()))
				.mask(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.SALES_AGENT_AGREEMENT.getLabel()))
				.mask(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoSSMetaData.DriverActivityReportsTab.HAS_THE_CUSTOMER_EXPRESSED_INTEREST_IN_PURCHASING_THE_QUOTE.getLabel()));
		fillAndValidate(td);

	}

	private void fillAndValidate(TestData td) {
		policy.dataGather().start();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
		policy.getDefaultView().getTab(RatingDetailReportsTab.class).fillTab(td);
		new PremiumAndCoveragesTab().calculatePremium();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		policy.getDefaultView().getTab(DriverActivityReportsTab.class).fillTab(td);

		assertThat(DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(1);
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1)
				.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("HouseHold");
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1)
				.getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.SELECT.getLabel()).controls.radioGroups.getFirst().getValue()).isEqualTo("Yes");

	}

}
