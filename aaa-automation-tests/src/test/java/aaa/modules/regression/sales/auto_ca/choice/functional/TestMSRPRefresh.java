package aaa.modules.regression.sales.auto_ca.choice.functional;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import aaa.modules.regression.queries.MsrpQueries;
import aaa.modules.regression.queries.postconditions.DatabaseCleanHelper;
import aaa.modules.regression.queries.postconditions.TestVinUploadPostConditions;
import aaa.modules.regression.sales.template.functional.TestMSRPRefreshTemplate;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;

public class TestMSRPRefresh extends TestMSRPRefreshTemplate implements MsrpQueries, TestVinUploadPostConditions {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_PartialMatch(@Optional("CA") String state) {
		partialMatch();
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_VehicleTypeRegular(@Optional("CA") String state) {
		TestData vehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData").getTestData(new VehicleTab().getMetaKey()).mask("VIN");
		TestData testData = getPolicyTD().adjust(new VehicleTab().getMetaKey(), vehicleTab).resolveLinks();

		vehicleTypeRegular(testData);

	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_VehicleTypeNotRegular(@Optional("CA") String state) {
		TestData testDataVehicleTabMotorHome = getVehicleMotorHomeTestData();

		TestData testData = getPolicyTD().adjust(new VehicleTab().getMetaKey(), testDataVehicleTabMotorHome).resolveLinks();

		vehicleTypeNotRegular(testData);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeRegular(@Optional("CA") String state) {
		// Some kind of random vin number
		TestData vehicleTab = testDataManager.getDefault(TestVINUpload.class).getTestData("TestData")
				.getTestData(new VehicleTab().getMetaKey()).adjust("VIN", "6FDEU15H7KL055795").resolveLinks();
		TestData testData = getPolicyTD().adjust(new VehicleTab().getMetaKey(), vehicleTab).resolveLinks();

		renewalVehicleTypeRegular(testData);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeNotRegular(@Optional("CA") String state) {
		TestData testData = getMSRPTestDataTwoVehicles(getPolicyTD());
		testData.adjust("AssignmentTab", getTwoAssignmentsTestData()).resolveLinks();
		testData.adjust("DocumentsAndBindTab", getTwoAdditionalInterests(testData)).resolveLinks();

		renewalVehicleTypeNotRegular(testData);
	}

	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE, testCaseId = "PAS-730")
	public void pas730_RenewalVINDoesMatchNBandNoMatchOn(@Optional("CA") String state) {
		String vinNumber = "7MSRP15H5V1011111";
		VehicleTab vehicleTab = new VehicleTab();
		TestData testData = getPolicyTD().adjust(testDataManager.getDefault(TestVINUpload.class).getTestData("TestData").resolveLinks())
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), AutoCaMetaData.VehicleTab.VIN.getLabel()), vinNumber)
				.adjust(TestData.makeKeyPath(vehicleTab.getMetaKey(), "Value($)"), "40000");

		renewalVINDoesMatchNBandNoMatchOn(testData);
	}

	/**
	 * Info in each xml file for this test could be used only once, so for running of tests properly DB should be cleaned after
	 * each test method. So newly added values should be deleted from :
	 * Vehiclerefdatavin,
	 * Vehiclerefdatamodel
	 * VEHICLEREFDATAVINCONTROL
	 * tables. Default values should be set for EXPIRATIONDATE field for default rows in VEHICLEREFDATAVINCONTROL table.
	 * <p>
	 * 'SYMBOL_2000_SS_TEST' are names of configurations which are used and listed in excel
	 * files for each product (choice config, select config and Signature Series config ONLY for UT state). So if they will be changed there
	 * this after method should be updated. But such updates are not supposed to be done.
	 * Please refer to the files with appropriate names in each test in /resources/uploadingfiles/vinUploadFiles.
	 */
	@AfterMethod(alwaysRun = true)
	protected void vinTablesCleaner() {
		// Reset 'default' msrp version
		DBService.get().executeUpdate(String.format(UPDATE_MSRP_COMP_COLL_CONTROL_VERSION_VEHICLEYEARMAX_BY_KEY_VEHICLEYEARMIN, 9999, 2011, 4));
		// Reset to the default state  MSRP_2000
		DBService.get().executeUpdate(String.format(UPDATE_VEHICLEREFDATAVINCONTROL_BY_EXPIRATION_DATE, getState()));
		// DELETE new VEHICLEREFDATAVINCONTROL version
		DBService.get().executeUpdate(String
				.format(DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION_STATECD, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE, getState()));
		DBService.get().executeUpdate(String
				.format(DELETE_FROM_VEHICLEREFDATAVINCONTROL_BY_VERSION_STATECD, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_CHOICE, getState()));
		// DELETE new MSRP version pas730_VehicleTypeRegular
		DBService.get()
				.executeUpdate(String
						.format(DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, NEWLY_ADDED_MSRP_VERSION_FOR_REGULAR_VEH_AUTO_CA_CHOICE, 44, vehicleTypeRegular));

		// DELETE new MSRP version pas730_VehicleTypeNotPPA
		DBService.get()
				.executeUpdate(String.format(DELETE_FROM_MSRPCompCollCONTROL_BY_VERSION_KEY, NEWLY_ADDED_MSRP_VERSION_FOR_MOTORHOME_VEH_AUTO_CA_CHOICE, 44, vehicleTypeMotorHome));

		DatabaseCleanHelper.cleanVinUploadTables("('SYMBOL_2000_CHOICE_T')", getState());
		vinMethods.enableVinRefresh(false);
	}

}
