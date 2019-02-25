package aaa.modules.regression.sales.auto_ss.functional;

import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.metadata.policy.AutoSSMetaData.GeneralTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.regression.sales.auto_ss.TestPolicyCreationBig;
import aaa.modules.regression.sales.template.functional.TestClueReportOnCopyActionsTemplate;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(statesExcept = Constants.States.CA)
public class TestClueReportOnCopyActions extends TestClueReportOnCopyActionsTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

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
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-8271")
    public void pas8271_testClueReportOnCopyPolicyActionSS(@Optional("") String state) {

        // Get state-specific 2nd driver and adjust test data      
		TestData tdNamedInsured = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
				.getTestData(GeneralTab.class.getSimpleName())
				.getTestDataList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel())
				.get(1)
				.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.ADD_INSURED.getLabel(), "Click")
				.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(), "Sally")
				.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(), "Smith");
		
        TestData tdDriverTab =  getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
        		.getTestDataList(DriverTab.class.getSimpleName())
        		.get(1)
        		.adjust(AutoSSMetaData.DriverTab.ADD_DRIVER.getLabel(), "Click");
		

        TestData tdEndorsement = getTestSpecificTD("TestData")
        		.adjust(DriverTab.class.getSimpleName(), tdDriverTab)
        		.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()), tdNamedInsured);
		
		pas8271_testClueReportOnCopyPolicyAction(tdEndorsement);

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
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-8271")
    public void pas8271_testClueReportOnCopyQuoteActionSS(@Optional("") String state) {

		mainApp().open();
		createCustomerIndividual();
		
		List<TestData> tdNamedInsured = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
				.getTestData(GeneralTab.class.getSimpleName())
				.getTestDataList(AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel());

		tdNamedInsured.get(1)
				.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel(), "Sally")
				.adjust(AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel(), "Smith");
		
		TestData td = getPolicyDefaultTD().adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel()), tdNamedInsured);
		// Initiate Quote with 2 drivers, fill up to DAR page, and initiate Copy From Quote action
		createQuoteFillAndInitiateCopyAction(td, new DriverActivityReportsTab());

		// Fill requirements on Rating Detail Reports, calculate premium, and order reports on DAR
		td.adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName(), AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT.getLabel()), "I Agree")
				.mask(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.CUSTOMER_AGREEMENT.getLabel()))
				.mask(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.SALES_AGENT_AGREEMENT.getLabel()));

		fillAndValidateCLUETable(td);

	}

}
