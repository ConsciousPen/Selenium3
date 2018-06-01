package aaa.modules.regression.sales.auto_ca.select.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.modules.regression.sales.auto_ca.select.TestPolicyCreationBig;
import aaa.modules.regression.sales.template.functional.TestClueReportOnCopyActionsTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestClueReportOnCopyActions extends TestClueReportOnCopyActionsTemplate {

    /**
     * @author Josh Carpenter
     * @name Test that CLUE reports do not carry over on 'Copy Policy' action for CA Select Auto policies
     * @scenario
     * 1. Create Customer
     * 2. Create Auto CA Select Policy
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
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-8271")
    public void pas8271_testClueReportOnCopyPolicyActionCA_Select(@Optional("CA") String state) {

        // Get state-specific 2nd driver and adjust test data
        TestData tdDriverTab = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1)
                .adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click")
                .adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Sally")
                .adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Smith")
                .mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel());

        TestData tdEndorsement = testDataManager.getDefault(aaa.modules.regression.sales.auto_ss.functional.TestClueReportOnCopyActions.class).getTestData("TestData")
                .adjust(DriverTab.class.getSimpleName(), tdDriverTab)
                .adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()), "I Agree")
                .adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY.getLabel()), "click");

		pas8271_testClueReportOnCopyPolicyAction(PolicyType.AUTO_CA_SELECT, tdEndorsement);

    }

	/**
	 * @author Josh Carpenter
	 * @name Test that CLUE reports do not carry over on 'Copy Quote' action for CA Auto policies
	 * @scenario
	 * 1. Create Customer
	 * 2. Initiate Auto CA Select Quote
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
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-8271")
	public void pas8271_testClueReportOnCopyQuoteActionCA_Select(@Optional("CA") String state) {

		List<TestData> tdDriversTab = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName());
		tdDriversTab.get(1)
				.adjust(AutoCaMetaData.DriverTab.FIRST_NAME.getLabel(), "Sally")
				.adjust(AutoCaMetaData.DriverTab.LAST_NAME.getLabel(), "Smith")
				.mask(AutoCaMetaData.DriverTab.NAMED_INSURED.getLabel());
		TestData td = getPolicyDefaultTD().adjust(DriverTab.class.getSimpleName(), tdDriversTab);

		pas8271_testClueReportOnCopyQuoteActionCA(PolicyType.AUTO_CA_SELECT, td);

	}

}
