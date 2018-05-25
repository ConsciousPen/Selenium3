package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
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
    public void pas8271_testClueReportOnCopyPolicyActionSS(@Optional("") String state) {

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

        // Copy from policy and initiate data gather
        policy.policyCopy().perform(tdCopy);
        policy.dataGather().start();

        // Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.RATING_DETAIL_REPORTS.get());
        policy.getDefaultView().getTab(RatingDetailReportsTab.class).fillTab(tdCopy);
        new PremiumAndCoveragesTab().calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
        policy.getDefaultView().getTab(DriverActivityReportsTab.class).fillTab(tdCopy);

        // Validate CLUE reports table
        assertThat(DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(1);
        assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1)
                .getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.ORDER_TYPE.getLabel()).getValue()).isEqualToIgnoringCase("HouseHold");
        assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1)
                .getCell(AutoSSMetaData.DriverActivityReportsTab.OrderCLUEReport.SELECT.getLabel()).controls.radioGroups.getFirst().getValue()).isEqualTo("Yes");
    }
}
