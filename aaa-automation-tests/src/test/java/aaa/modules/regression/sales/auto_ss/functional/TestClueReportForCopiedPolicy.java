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
     * @name Test that CLUE reports do not carry over on 'Copy Policy' action
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

        // Get state-specific license number for 2nd driver and adjust test data
        String licenseNumber = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
                .getTestDataList(DriverTab.class.getSimpleName()).get(1).getValue(AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel());
        TestData td = getTestSpecificTD("TestData").adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.LICENSE_NUMBER.getLabel()), licenseNumber);
        TestData tdCopy = getPolicyTD("CopyFromPolicy", "TestData");

        // Perform endorsement to add 2nd driver
        policy.createEndorsement(td);

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
