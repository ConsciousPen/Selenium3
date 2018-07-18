package aaa.modules.regression.sales.auto_ca.select.functional;

import static aaa.helpers.db.queries.MsrpQueries.CA_SELECT_REGULAR_VEH_MSRP_VERSION;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestMSRPRefreshTemplate;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMSRPRefreshNotRegularVehicle extends TestMSRPRefreshTemplate{

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Comp/Coll symbols refresh VIN Doesn't match to DB Vehicle type (NOT PPA/Regular)
	 * 1. Create Auto quote: VIN doesn't match, type NOT PPA/Regular
	 * 2. Calculate premium and validate comp/coll symbols
	 * 3. Add new Active MSRP versions to DB, Adjust values in MSRP tables
	 * 4. Retrieve created quote
	 * 5. Navigate to P&C page and validate that comp/coll symbols WEREN'T Changed because Vehicle type = Motor Home (Not Regular)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_VehicleTypeNotRegular(@Optional("CA") String state) {
		TestData testDataVehicleTabMotorHome = getVehicleMotorHomeTestData(String.valueOf(VEHICLYEARMIN_ADDED));

		TestData testData = getPolicyTD().adjust(vehicleTab.getMetaKey(), testDataVehicleTabMotorHome).resolveLinks();
		testData.getTestData("AssignmentTab").getTestDataList("DriverVehicleRelationshipTable").get(0).ksam("Primary Driver").resolveLinks();

		vehicleTypeNotRegular(testData, false);
	}

	/**
	 * @author Viktor Petrenko
	 * @scenario Renewal: Comp/Coll symbols refresh VIN Doesn't match to DB Vehicle type (PPA/Regular)
	 * 1. Auto Policy created: VIN doesn't match, type NOT PPA/Regular
	 * 2. Add new Active MSRP versions to DB, Adjust values in MSRP tables
	 * 3. Generate and rate renewal image
	 * 4. Open generated renewal image
	 * 5. Navigate to P&C page and validate that comp/coll symbols WEREN'T Changed because Vehicle type = Motor Home (Not Regular)
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-730")
	public void pas730_RenewalVehicleTypeNotRegular(@Optional("") String state) {
		TestData testData = getMSRPTestDataTwoVehicles(getPolicyTD());
		testData.adjust("AssignmentTab", getTwoAssignmentsTestData()).resolveLinks();

		renewalVehicleTypeNotRegular(testData, false);
	}

	@AfterClass(alwaysRun = true)
	protected void resetVinControlTable() {
		pas730_SelectCleanDataBase(CA_SELECT_REGULAR_VEH_MSRP_VERSION,vehicleTypeMotorHome);
	}
}
