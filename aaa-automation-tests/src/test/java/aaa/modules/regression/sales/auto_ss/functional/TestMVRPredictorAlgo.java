package aaa.modules.regression.sales.auto_ss.functional;


import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestMVRPredictorAlgo extends AutoSSBaseTest {

	private ErrorTab errorTab = new ErrorTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();

	/**
	* @author Dominykas Razgunas
	* @name MVR Predictor Algo for 2 of 4 Drivers
	* @scenario 1. Create Customer1.
	* 2. Create Auto SS Quote.
	* 3. Add 4 Drivers 2 of who are eligible for mvr status predicted valid . eligibility = age>83 and Driving exp>62
	 * 4. Calculate Premium
	* 5. Navigate to Driver Activity Reports Tab.
	* 6. Order Reports.
	* 7. Assert Statuses
	* @details
	*/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "MVR Predictor Algo for 2 of 4 Drivers")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9723")
	public void pas9723_MVRPredictorNewBusiness(@Optional("") String state) {

		TestData testData = getPolicyTD();
		TestData driverTab = getTestSpecificTD("TestData_DriverTab").resolveLinks();

		// Add 4 Drivers 2 of them with age>83 and Driving exp>62
		preconditionAddedDrivers(testData, driverTab);

		// fill remaining Policy
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, DriverActivityReportsTab.class, true);

		// Assert That two drivers have license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(3).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isEqualTo("Predicted Valid");
		// Assert That other driver does not have license status = predicted valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(4).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Bypass MVR Predictor Algo for drivers with violations
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Quote.
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
	public void pas9723_BypassMVRPredictorManuallyAddedViolations(@Optional("") String state) {

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933");
		TestData driverTab = getTestSpecificTD("TestData_DriverTabViolations").resolveLinks();

		// Add 1 Driver who is eligible for mvr status predicted valid
		// Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
		preconditionAddedDrivers(testData, driverTab);

		// Fill remaining policy to drivers activity reports tab
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);
		premiumAndCoveragesTab.submitTab();
		errorTab.overrideAllErrors();
		errorTab.override();
		premiumAndCoveragesTab.submitTab();
		driverActivityReportsTab.fillTab(testData);

		assertMVRResponse();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Bypass MVR Predictor Algo for drivers with accidents
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Quote.
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

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933");
		TestData driverTab = getTestSpecificTD("TestData_DriverTabAccidents").resolveLinks();

		// Add 1 Driver who is eligible for mvr status predicted valid
		// Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
		preconditionAddedDrivers(testData, driverTab);

		// Fill remaining policy to drivers activity reports tab
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, DriverActivityReportsTab.class, true);

		assertMVRResponse();
		}

	private void preconditionAddedDrivers(TestData policyTestData, TestData driverTabTD){
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(policyTestData, DriverTab.class, true);
		policy.getDefaultView().fill(driverTabTD);
	}

	private void assertMVRResponse(){
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