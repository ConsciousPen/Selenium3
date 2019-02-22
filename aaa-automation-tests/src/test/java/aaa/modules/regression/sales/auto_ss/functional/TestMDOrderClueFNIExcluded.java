package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.modules.regression.sales.auto_ss.TestPolicyCreationBig;
import aaa.modules.regression.sales.auto_ss.TestPolicyNano;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = Constants.States.MD)
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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-5147"})
	public void pas5147_testOrderClueFNIExcluded_StandardNB(@Optional("MD") String state) {

		TestData td = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName() + "[0]", AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel()), "Excluded");

		fillQuoteAndValidateCLUE(td);

	}

	/**
	 * @author Josh Carpenter
	 * @name Test ability to order CLUE in MD when the FNI is excluded for nano policies at NB
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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = {"PAS-5147"})
	public void pas5147_testOrderClueFNIExcluded_NanoNB(@Optional("MD") String state) {

		TestData td = getStateTestData(testDataManager.getDefault(TestPolicyCreationBig.class), "TestData")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName() + "[0]", AutoSSMetaData.DriverTab.DRIVER_TYPE.getLabel()), "Excluded")
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(),
						AutoSSMetaData.GeneralTab.PolicyInformation.POLICY_TYPE.getLabel()), "Named Non Owner")
				.adjust(VehicleTab.class.getSimpleName(), DataProviderFactory.emptyData());

		fillQuoteAndValidateCLUE(td);

	}

	/**
	 * @author Josh Carpenter
	 * @name Test ability to order CLUE in MD when the FNI is changed to excluded and a driver is added during same mid-term endorsements
	 * @scenario
	 * 1. Create new customer
	 * 2. Create MD Auto SS policy with 1 driver (FNI)
	 * 3. Initiate mid-term endorsement
	 * 4. Add a second driver as 'Included in Rating' and switch the first driver (FNI) to 'Excluded'
	 * 5. Navigate to Rating Details and order reports
	 * 6. Navigate to P & C tab and recalculate premium
	 * 7. Navigate to DAR page and validate
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-5147"})
	public void pas5147_testOrderClueExcludeFNIEndorsementStandard(@Optional("MD") String state) {

		mainApp().open();
		createCustomerIndividual();
		createPolicy();

		initiateEndorsementAndValidateCLUE(getTestSpecificTD("TestData"));
	}

	/**
	 * @author Josh Carpenter
	 * @name Test ability to order CLUE in MD when the FNI is changed to excluded and a driver is added during same mid-term endorsements
	 * @scenario
	 * 1. Create new customer
	 * 2. Create MD Auto SS Nano policy with 1 driver (FNI)
	 * 3. Initiate mid-term endorsement
	 * 4. Add a second driver as 'Included in Rating' and switch the first driver (FNI) to 'Excluded'
	 * 5. Navigate to Rating Details and order reports
	 * 6. Navigate to P & C tab and recalculate premium
	 * 7. Navigate to DAR page and validate
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = {"PAS-5147"})
	public void pas5147_testOrderClueExcludeFNIEndorsementNano(@Optional("MD") String state) {

		mainApp().open();
		createCustomerIndividual();
		createPolicy(getStateTestData(testDataManager.getDefault(TestPolicyNano.class), "TestData"));

		initiateEndorsementAndValidateCLUE(getTestSpecificTD("TestData")
				.mask(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.ADD_VEHICLE.getLabel()))
				.mask(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.USAGE.getLabel()))
				.mask(TestData.makeKeyPath(VehicleTab.class.getSimpleName(), AutoSSMetaData.VehicleTab.VIN.getLabel())));

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

	private void initiateEndorsementAndValidateCLUE(TestData td) {
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		policy.getDefaultView().fillFromTo(td, GeneralTab.class, DriverActivityReportsTab.class, true);

		assertThat(DriverActivityReportsTab.tableCLUEReports.getRows().size()).isEqualTo(2);
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1).getCell("Select").controls.radioGroups.getFirst()).isDisabled();
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(2).getCell("Select").controls.radioGroups.getFirst()).isEnabled();
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(1).getCell("Response").getValue()).isNotEmpty();
		assertThat(DriverActivityReportsTab.tableCLUEReports.getRow(2).getCell("Response").getValue()).isNotEmpty();
	}

}
