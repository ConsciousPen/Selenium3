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
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.FormsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.VehicleTab;
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
	* 2. Create Auto SS CT Quote.
	* 3. Add 4 Drivers 2 of who are eligible for mvr status predicted valid . eligibility = age>83 and Driving exp>62
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

		TestData testData = getPolicyTD();
		TestData driverTab = getTestSpecificTD("TestData_DriverTab").resolveLinks();

		// Add 4 Drivers 2 of them with age>83 and Driving exp>62
		preconditionAddedDrivers(testData, driverTab);

		// Add 4 Vehicles
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, VehicleTab.class, true);
		policy.getDefaultView().fill(getTestSpecificTD("TestData_VehicleTab").resolveLinks());

		policy.getDefaultView().fillFromTo(testData, FormsTab.class, DriverActivityReportsTab.class, true);

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

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933");
		TestData driverTab = getTestSpecificTD("TestData_DriverTabViolations").resolveLinks();

		// Add 1 Driver who is eligible for mvr status predicted valid
		// Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
		preconditionAddedDrivers(testData, driverTab);

		// Fill remaining policy to drivers activity reports tab
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);
		premiumAndCoveragesTab.submitTab();
		errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_200005, ErrorEnum.Errors.ERROR_AAA_200009);
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

		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933");
		TestData driverTab = getTestSpecificTD("TestData_DriverTabAccidents").resolveLinks();

		// Add 1 Driver who is eligible for mvr status predicted valid
		// Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
		preconditionAddedDrivers(testData, driverTab);

		// Fill remaining policy to drivers activity reports tab
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, DriverActivityReportsTab.class, true);

		assertMVRResponse();
		}


	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers viable for Good Student Discount
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS CT Quote.
	 * 3. Add 1 Driver who is eligible for GSD
	 * 4. Add 5 Drivers who are not eligible for GSD with following:
	1. Driver is a rated driver AND
	2. Driver age is between 16 and 25 years AND
	3. Driver is single, separated or divorced AND
	4. Driver is a Student or a college graduate AND
	5. Driver have maintained a grade of at least B in letter grading system or at least 3.00 in a 4 point numerical grading system or a College Graduate.
	 * 5. Calculate Premium
	 * 6. Assert Discounts
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "MVR Predictor Algo for Good Student Discount")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10108")
	public void pas10108_GoodStudentDiscountMVRPredictor(@Optional("CT") String state) {

		// adjust policy TD so that first driver is eligible for GSD
		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]", AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel()), "Driver1")
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]", AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel()), "LastName1")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/2000")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel()), "Single")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.OCCUPATION.getLabel()), "Student")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.MOST_RECENT_GPA.getLabel()), "A Student");

		TestData driverTab = getTestSpecificTD("TestData_DriverTabGSD").resolveLinks();

		// Add 1 Driver who is eligible for GSD
		// Add 5 Drivers who are not eligible for GSD
		preconditionAddedDrivers(testData, driverTab);

		// Fill remaining policy to P&C tab
		policy.getDefaultView().fillFromTo(testData, RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);

		// Assert 1 Driver who is eligible for GSD
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).contains("Good Student Discount(Driver1 LastName1)");

		// Assert 5 Drivers who are not eligible for GSD
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(Driver2 LastName2)");
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(Driver3 LastName3)");
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(Driver4 LastName4)");
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(Driver5 LastName5)");
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(Driver6 LastName6)");
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