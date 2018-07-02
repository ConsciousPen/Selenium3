package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyCreationBig;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestMDOrderClueFNIExcluded extends AutoSSBaseTest {

	/**
	 * @author Josh Carpenter
	 * @name Test ability to order CLUE in MD when the FNI is excluded for standard policies
	 * @scenario
	 * 1. Create new customer
	 * 2. Initiate MD SS Auto quote
	 * 3. Fill quote with 2 named insured/drivers and 2 vehicles
	 * 4. Mark driver 1 as 'excluded' and leave driver 2 as 'Available for Rating'
	 * 5. Navigate to Reports tab
	 * 6. Validate the CLUE report can be successfully ordered
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-5147"})
	public void pas5147_testOrderClueFNIExcludedStandard(@Optional("MD") String state) {

		TestData td = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName() + "[0]", AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel()), "Excluded");

		fillQuoteAndValidateCLUE(td);

	}

	/**
	 * @author Josh Carpenter
	 * @name Test ability to order CLUE in MD when the FNI is excluded for nano policies
	 * @scenario
	 * 1. Create new customer
	 * 2. Initiate Nano MD SS Auto quote
	 * 3. Fill quote with 2 named insured/drivers and 2 vehicles
	 * 4. Mark driver 1 as 'excluded' and leave driver 2 as 'Available for Rating'
	 * 5. Navigate to Reports tab
	 * 6. Validate the CLUE report can be successfully ordered
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-5147"})
	public void pas5147_testOrderClueFNIExcludedNano(@Optional("MD") String state) {

		TestData td = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName() + "[0]", AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel()), "Excluded")
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE.getLabel()), "Named Non Owner")
				.adjust(VehicleTab.class.getSimpleName(), DataProviderFactory.emptyData());

		fillQuoteAndValidateCLUE(td);

	}

	private void fillQuoteAndValidateCLUE(TestData td) {
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, DriverActivityReportsTab.class, true);

		assertThat(DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(1);
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1).getCell("Select").controls.radioGroups.getFirst()).isEnabled();
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1).getCell("Response").getValue()).isNotEmpty();
	}

}
