package aaa.modules.regression.sales.auto_ca.select.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoCaMetaData.GeneralTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.modules.regression.sales.auto_ca.select.TestPolicyCreationBig;
import aaa.modules.regression.sales.template.functional.TestClueReportOnCopyActionsTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.CA)
public class TestClueReportOnCopyActions extends TestClueReportOnCopyActionsTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

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
   // @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-8271")
    public void pas8271_testClueReportOnCopyPolicyActionCA_Select(@Optional("CA") String state) {

		TestData tdNamedInsured = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
				.getTestData(GeneralTab.class.getSimpleName())
				.getTestDataList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel())
				.get(1)
				.adjust(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getLabel(), "Click")
				.adjust(AutoCaMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(), "Sally")
				.adjust(AutoCaMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(), "Smith");
		
		
        TestData tdDriverTab =  getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
        		.getTestDataList(DriverTab.class.getSimpleName())
        		.get(1)
        		.adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");
        
		//TestData tdDriverTab = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData").getTestDataList(DriverTab.class.getSimpleName()).get(1);
		TestData tdSpecific = testDataManager.getDefault(aaa.modules.regression.sales.auto_ss.functional.TestClueReportOnCopyActions.class).getTestData("TestData");

		tdSpecific
		  .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()), tdNamedInsured)
		  .adjust(DriverTab.class.getSimpleName(), tdDriverTab)
		  .adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()), "I Agree")
		  .adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoCaMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY.getLabel()), "click");

		pas8271_testClueReportOnCopyPolicyAction(tdSpecific);

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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-8271")
	public void pas8271_testClueReportOnCopyQuoteActionCA_Select(@Optional("CA") String state) {

		TestData tdNamedInsured = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
				.getTestData(GeneralTab.class.getSimpleName())
				.getTestDataList(AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel())
				.get(1)
				.adjust(AutoCaMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getLabel(), "Click")
				.adjust(AutoCaMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(), "Sally")
				.adjust(AutoCaMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(), "Smith");
		
        TestData tdDriverTab =  getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
        		.getTestDataList(DriverTab.class.getSimpleName())
        		.get(1)
        		.adjust(AutoCaMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");
		
		//TestData td = getCACopyQuoteTD(getPolicyDefaultTD(), tdDriverTab);
        TestData td = getPolicyDefaultTD()
      		  .adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoCaMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()), tdNamedInsured)
    		  .adjust(DriverTab.class.getSimpleName(), tdDriverTab);

		pas8271_testClueReportOnCopyQuoteActionCA(td);

	}

}
