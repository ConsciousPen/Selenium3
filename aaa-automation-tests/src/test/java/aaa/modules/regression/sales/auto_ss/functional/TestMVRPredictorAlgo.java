package aaa.modules.regression.sales.auto_ss.functional;


import static org.assertj.core.api.Assertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMVRPredictorAlgo extends AutoSSBaseTest {


	/**
	* * @author Dominykas Razgunas
	* @name MVR Predictor Algo for 2 of 4 Drivers
	* @scenario 1. Create Customer1.
	* 2. Create Auto SS CT Quote.
	* 3. Add 4 Drivers 2 of who are eligible for mvr status predicted valid
	* 4. Add 4 Vehicles
	* 5. Navigate to Driver Activity Reports Tab.
	* 6. Order Reports.
	* 7. Assert Statuses
	* @details
	*/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "MVR Predictor Algo for 2 of 4 Drivers")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9723")
	public void pas9723_MVRPredictorNewBusiness(@Optional("CT") String state) {

		mainApp().open();
		createCustomerIndividual();

		TestData testData = getPolicyTD();

		policy.initiate();

		// Add 4 Drivers 2 of them with age>83 and Driving exp>62
		policy.getDefaultView().fillUpTo(testData, DriverTab.class, true);
		policy.getDefaultView().fill(getTestSpecificTD("TestData_DriverTab").resolveLinks());

		// Add 4 Vehicles
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, VehicleTab.class, true);
		policy.getDefaultView().fill(getTestSpecificTD("TestData_VehicleTab").resolveLinks());

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, DriverActivityReportsTab.class, true);

		// Assert That two drivers have license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(3).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isEqualTo("Predicted Valid");

	}

}
