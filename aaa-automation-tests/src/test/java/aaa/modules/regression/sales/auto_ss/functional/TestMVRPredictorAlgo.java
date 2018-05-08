package aaa.modules.regression.sales.auto_ss.functional;


import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMVRPredictorAlgo extends AutoSSBaseTest {


	/**
	* @author Dominykas Razgunas
	* @name MVR Predictor Algo for 2 of 4 Drivers
	* @scenario 1. Create Customer1.
	* 2. Create Auto SS CT Quote.
	* 3. Add 4 Drivers 2 of who are eligible for mvr status predicted valid
	* 4. Add 4 Vehicles
	 * 5. Calculate Premium
	* 6. Navigate to Driver Activity Reports Tab.
	* 7. Order Reports.
	* 8. Assert Statuses
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


	/**
	 * @author Dominykas Razgunas
	 * @name Bypass MVR Predictor Algo for drivers with violations
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS CT Quote.
	 * 3. Add 1 Driver who is eligible for mvr status predicted valid
	 * 4. Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
	 * 5. Calculate Premium
	 * 6. Navigate to Driver Activity Reports Tab.
	 * 7. Order Reports.
	 * 8. Assert Statuses
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Bypass MVR Predictor Algo for drivers with violations")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9723")
	public void pas9723_BypassMVRPredictorManuallyAddedViolations(@Optional("CT") String state) {

		mainApp().open();
		createCustomerIndividual();

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933");

		policy.initiate();

		// Add 4 Drivers 2 of them with age>83 and Driving exp>62
		policy.getDefaultView().fillUpTo(testData, DriverTab.class, true);
		policy.getDefaultView().fill(getTestSpecificTD("TestData_DriverTabViolations").resolveLinks());

		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);

		new PremiumAndCoveragesTab().submitTab();
		new ErrorTab().verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_200005, ErrorEnum.Errors.ERROR_AAA_200009);
		new ErrorTab().overrideAllErrors();
		new ErrorTab().override();
		new PremiumAndCoveragesTab().submitTab();


		// Assert That driver with no violations has license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isEqualTo("Predicted Valid");
		// Assert That drivers with violations do not have license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(3).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(4).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(5).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(6).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Bypass MVR Predictor Algo for drivers with accidents
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS CT Quote.
	 * 3. Add 1 Driver who is eligible for mvr status predicted valid
	 * 4. Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  at-fault accident, comprehensive claim, glass only loss, non-fault accident, principally at-fault accident
	 * 5. Add 4 Vehicles
	 * 6. Calculate Premium
	 * 7. Navigate to Driver Activity Reports Tab.
	 * 8. Order Reports.
	 * 9. Assert Statuses
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Bypass MVR Predictor Algo for drivers with accidents")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9723")
	public void pas9723_BypassMVRPredictorManuallyAddedAccidents(@Optional("CT") String state) {

		mainApp().open();
		createCustomerIndividual();

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933");

		policy.initiate();

		// Add 4 Drivers 2 of them with age>83 and Driving exp>62
		policy.getDefaultView().fillUpTo(testData, DriverTab.class, true);
		policy.getDefaultView().fill(getTestSpecificTD("TestData_DriverTabAccidents").resolveLinks());

		//		// Add 4 Vehicles
		//		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, VehicleTab.class, true);
		//		policy.getDefaultView().fill(getTestSpecificTD("TestData_VehicleTab").resolveLinks());
		//
		//
		//
		//		policy.getDefaultView().fillFromTo(testData, FormsTab.class, DriverActivityReportsTab.class, true);


		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, DriverActivityReportsTab.class, true);

		// Assert That driver with no violations has license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isEqualTo("Predicted Valid");
		// Assert That drivers with violations do not have license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(3).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(4).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(5).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(6).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
	}

}
