package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.IN, Constants.States.KY, Constants.States.MD, Constants.States.NJ, Constants.States.NV,
        Constants.States.PA, Constants.States.VA, Constants.States.OK, Constants.States.CT})
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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for 2 of 4 Drivers")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9723")
	public void pas9723_MVRPredictorNewBusiness(@Optional("") String state) {

		TestData td = getPolicyTD().adjust(getTestSpecificTD("TestData_DriverTab").resolveLinks());

		// Add 4 Drivers 2 of them with age>83 and Driving exp>62
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, DriverActivityReportsTab.class, true);

		// assert Ordered MVR License Statuses
		assertAddedDrivers(false);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for 2 of 4 Drivers Endorsement
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy.
	 * 3. Endorse Policy.
	 * 4. Add 4 Drivers 2 of who are eligible for mvr status predicted valid . eligibility = age>83 and Driving exp>62
	 * 5. Calculate Premium
	 * 6. Navigate to Driver Activity Reports Tab.
	 * 7. Order Reports.
	 * 8. Assert Statuses
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for 2 of 4 Drivers Endrosement")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_MVRPredictorEndorsement(@Optional("") String state) {

		TestData td = getTestSpecificTD("TestData_DriverTab_Endorsement").resolveLinks();

		// Open application Create Customer Create Policy with Driver exceeding MVR predictor threshold. Endorse Policy
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getPolicyDefaultTD());
		
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		policy.getDefaultView().fillFromTo(td, GeneralTab.class, DriverActivityReportsTab.class, true);

		// assert Ordered MVR License Statuses
		assertAddedDrivers(false);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for 2 of 4 Drivers Renewal
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy.
	 * 3. Renew Policy.
	 * 4. Add 4 Drivers 2 of who are eligible for mvr status predicted valid . eligibility = age>83 and Driving exp>62
	 * 5. Calculate Premium
	 * 6. Navigate to Driver Activity Reports Tab.
	 * 7. Order Reports.
	 * 8. Assert Statuses
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT}, description = "MVR Predictor Algo for 2 of 4 Drivers Renewal")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_MVRPredictorRenewal(@Optional("") String state) {

		//TestData testData = getAdjustedDriverTestData();
		//TestData driverTab = getTestSpecificTD("TestData_DriverTab").resolveLinks();
		TestData td = getTestSpecificTD("TestData_DriverTab_Endorsement").resolveLinks();

		// Open application Create Customer Create Policy with Driver exceeding MVR predictor threshold. Renew Policy
		createPolicyAndRenewal(getPolicyDefaultTD());

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		//preconditionsAddDriversRenewalEndorsement(driverTab);
		policy.getDefaultView().fillFromTo(td, GeneralTab.class, DriverActivityReportsTab.class, true);
		
		// assert Ordered MVR License Statuses
		assertAddedDrivers(true);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for 2 of 4 Drivers Conversion
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Manual Entry Quote.
	 * 3. Add 4 Drivers 2 of who are eligible for mvr status predicted valid . eligibility = age>83 and Driving exp>62
	 * 4. Calculate Premium
	 * 5. Navigate to Driver Activity Reports Tab.
	 * 6. Order Reports.
	 * 7. Assert Statuses
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for 2 of 4 Drivers Conversion")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_MVRPredictorConversion(@Optional("") String state) {

		// For exceeding OK threshold (above threshold) you need a driver age x < 27y , driving exp  5< y <15 , male, single
		TestData testData = getConversionPolicyDefaultTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1990")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.GENDER.getLabel()), "Male")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel()), "Single")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel()), "18");

		if (!getState().equals(Constants.States.OK)) {
			testData.adjust(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel()),
					new RatingDetailReportsTab().getInsuranceScoreOverrideData("900"));
		}

		TestData driverTab = getTestSpecificTD("TestData_DriverTab").resolveLinks();

		// Open application Create Customer Initiate Conversion Policy with Driver exceeding MVR predictor threshold.
		initiateManualEntry(testData);

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		preconditionsAddDriversRenewalEndorsement(driverTab);

		// assert Ordered MVR License Statuses
		assertAddedDrivers(false);
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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Bypass MVR Predictor Algo for drivers with violations")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9723")
	public void pas9723_BypassMVRPredictorManuallyAddedViolations(@Optional("") String state) {
	
		TestData td = getPolicyTD().adjust(getTestSpecificTD("TestData_DriverTabViolations").resolveLinks());
		
		// Add 1 Driver who is eligible for mvr status predicted valid
		// Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, PremiumAndCoveragesTab.class, true);
		premiumAndCoveragesTab.submitTab();
		
		errorTab.overrideAllErrors();
		errorTab.override();
		premiumAndCoveragesTab.submitTab();
		driverActivityReportsTab.fillTab(td);

		
		assertMVRResponseViolations(false);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Bypass MVR Predictor Algo for drivers with violations Endorsement
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy.
	 * 3. Endorse Policy.
	 * 4. Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
	 * 5. Calculate Premium
	 * 6. Navigate to Driver Activity Reports Tab.
	 * 7. Order Reports.
	 * 8. Assert Statuses
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Bypass MVR Predictor Algo for drivers with violations Endorsement")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_BypassMVRPredictorManuallyAddedViolationsEndorsement(@Optional("") String state) {


		TestData td = getTestSpecificTD("TestData_DriverTabViolations_Endorsement").resolveLinks();

		//Open application Create Customer Create Policy with Driver exceeding MVR predictor threshold. Endorse Policy
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getPolicyDefaultTD()
				//.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.GENDER.getLabel()), "Female"));
		
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		policy.getDefaultView().fillFromTo(td, GeneralTab.class, DriverActivityReportsTab.class, true);

		assertMVRResponseViolations(false);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Bypass MVR Predictor Algo for drivers with violations Renewal
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy.
	 * 3. Renew Policy.
	 * 4. Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
	 * 5. Calculate Premium
	 * 6. Navigate to Driver Activity Reports Tab.
	 * 7. Order Reports.
	 * 8. Assert Statuses
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT}, description = "Bypass MVR Predictor Algo for drivers with violations Renewal")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_BypassMVRPredictorManuallyAddedViolationsRenewal(@Optional("") String state) {


		TestData td = getTestSpecificTD("TestData_DriverTabViolations_Endorsement").resolveLinks();

		// Open application Create Customer Create Policy with Driver exceeding MVR predictor threshold. Renew Policy
		createPolicyAndRenewal(getPolicyDefaultTD());

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		//preconditionsAddDriversRenewalEndorsement(driverTab);
		policy.getDefaultView().fillFromTo(td, GeneralTab.class, DriverActivityReportsTab.class, true);
		
		assertMVRResponseViolations(true);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name Bypass MVR Predictor Algo for drivers with violations Manual Entry Conversion
	 * @scenario 1. Create Customer1.
	 * 2. Initiate Auto SS Manual Entry Policy.
	 * 3. Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation
	 * 4. Calculate Premium
	 * 5. Navigate to Driver Activity Reports Tab.
	 * 6. Order Reports.
	 * 7. Assert Statuses
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Bypass MVR Predictor Algo for drivers with violations Conversion")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_BypassMVRPredictorManuallyAddedViolationsConversion(@Optional("") String state) {

		TestData testData = getConversionPolicyDefaultTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.GENDER.getLabel()), "Female")
				.adjust(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel()),
						new RatingDetailReportsTab().getInsuranceScoreOverrideData("900"));
		TestData driverTab = getTestSpecificTD("TestData_DriverTabViolations").resolveLinks();

		// Open application Create Customer Initiate Conversion Policy with Driver exceeding MVR predictor threshold.
		initiateManualEntry(testData);

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		preconditionsAddDriversRenewalEndorsement(driverTab);

		assertMVRResponseViolations(false);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers with accidents below threshold
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Quote.
	 * 3. Add 1 Driver who is eligible for mvr status predicted valid
	 * 4. Add 5 Drivers who are eligible for mvr status predicted valid with following violations  at-fault accident, comprehensive claim, glass only loss, non-fault accident, principally at-fault accident with clear Clue report
	 * 6. Calculate Premium
	 * 7. Navigate to Driver Activity Reports Tab.
	 * 8. Order Reports.
	 * 9. Assert Statuses
	 * @details
	 */
    @StateList(states = {Constants.States.IN, Constants.States.KY, Constants.States.MD, Constants.States.NJ, Constants.States.PA, Constants.States.VA, Constants.States.CT})
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for drivers with accidents")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-9723")
	public void pas9723_BypassMVRPredictorManuallyAddedAccidents(@Optional("") String state) {

		TestData td = getPolicyTD().adjust(getTestSpecificTD("TestData_DriverTabAccidents").resolveLinks());

		// Add 1 Driver who is eligible for mvr status predicted valid
		// Add 5 Drivers who are not eligible for mvr status predicted valid with following violations  alcohol-related violation, major violation, minor violation, non-moving violation, speeding violation	
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(td, DriverActivityReportsTab.class, true);

		assertMVRResponseAccidents(false);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers with accidents below threshold Endorsement
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy.
	 * 3. Endorse Policy.
	 * 4. Add 5 Drivers who are eligible for mvr status predicted valid with following violations  at-fault accident, comprehensive claim, glass only loss, non-fault accident, principally at-fault accident with clear Clue report
	 * 6. Calculate Premium
	 * 7. Navigate to Driver Activity Reports Tab.
	 * 8. Order Reports.
	 * 9. Assert Statuses
	 * @details
	 */
	@StateList(states = {Constants.States.IN, Constants.States.KY, Constants.States.MD, Constants.States.NJ, Constants.States.PA, Constants.States.VA, Constants.States.CT})
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for drivers with accidents Endorsement")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_BypassMVRPredictorManuallyAddedAccidentsEndorsement(@Optional("") String state) {

		TestData testData = getFirstDriverTestData();
		TestData driverTab = getTestSpecificTD("TestData_DriverTabAccidents").resolveLinks();

		// Open application Create Customer Create Policy with Driver exceeding MVR predictor threshold. Renew Policy
		mainApp().open();
		createCustomerIndividual();
		createPolicy(testData);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Month"));

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		preconditionsAddDriversRenewalEndorsement(driverTab);

		assertMVRResponseAccidents(false);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers with accidents below threshold Renewal
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy.
	 * 3. Renew Policy.
	 * 4. Add 5 Drivers who are eligible for mvr status predicted valid with following violations  at-fault accident, comprehensive claim, glass only loss, non-fault accident, principally at-fault accident with clear Clue report
	 * 6. Calculate Premium
	 * 7. Navigate to Driver Activity Reports Tab.
	 * 8. Order Reports.
	 * 9. Assert Statuses
	 * @details
	 */
	@StateList(states = {Constants.States.IN, Constants.States.KY, Constants.States.MD, Constants.States.NJ, Constants.States.PA, Constants.States.VA, Constants.States.CT})
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH, Groups.TIMEPOINT}, description = "MVR Predictor Algo for drivers with accidents Renewal")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_BypassMVRPredictorManuallyAddedAccidentsRenewal(@Optional("") String state) {

		TestData testData = getFirstDriverTestData();
		TestData driverTab = getTestSpecificTD("TestData_DriverTabAccidents").resolveLinks();

		// Open application Create Customer Create Policy with Driver exceeding MVR predictor threshold. Renew Policy
		createPolicyAndRenewal(testData);

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		preconditionsAddDriversRenewalEndorsement(driverTab);

		assertMVRResponseAccidents(true);
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers with accidents below threshold Manual Entry Conversion
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy.
	 * 3. Endorse Policy.
	 * 4. Add 5 Drivers who are eligible for mvr status predicted valid with following violations  at-fault accident, comprehensive claim, glass only loss, non-fault accident, principally at-fault accident with clear Clue report
	 * 6. Calculate Premium
	 * 7. Navigate to Driver Activity Reports Tab.
	 * 8. Order Reports.
	 * 9. Assert Statuses
	 * @details
	 */
	@StateList(states = {Constants.States.IN, Constants.States.KY, Constants.States.MD, Constants.States.NJ, Constants.States.PA, Constants.States.VA, Constants.States.CT})
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for drivers with accidents Conversion")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-14264")
	public void pas14264_BypassMVRPredictorManuallyAddedAccidentsConversion(@Optional("") String state) {

		TestData testData = getConversionPolicyDefaultTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.GENDER.getLabel()), "Female")
				.adjust(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel()),
						new RatingDetailReportsTab().getInsuranceScoreOverrideData("900"));
		TestData driverTab = getTestSpecificTD("TestData_DriverTabAccidents").resolveLinks();

		// Open application Create Customer Initiate Conversion Policy with Driver exceeding MVR predictor threshold.
		initiateManualEntry(testData);

		// Fill Drivers Tab Calculate premium and Validate Drivers history
		preconditionsAddDriversRenewalEndorsement(driverTab);

		assertMVRResponseAccidents(false);
	}
/*
	private TestData getAdjustedDriverTestData() {
		// For exceeding OK threshold (above threshold) you need a driver age x < 27y , driving exp  5< y <15 , male, single, Change score for SS Tier
		TestData td = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1990")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.GENDER.getLabel()), "Male")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel()), "Single")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.AGE_FIRST_LICENSED.getLabel()), "18");
		if (getState().equals(Constants.States.OK)) {
			return td.adjust(TestData.makeKeyPath(RatingDetailReportsTab.class.getSimpleName(), AutoSSMetaData.RatingDetailReportsTab.INSURANCE_SCORE_OVERRIDE.getLabel()),
					new RatingDetailReportsTab().getInsuranceScoreOverrideData("150"));
		}
		return td;
	}*/

	private TestData getFirstDriverTestData() {
		return getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/1933")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.GENDER.getLabel()), "Female");
	}

	private void createPolicyAndRenewal(TestData td) {
		mainApp().open();
		createCustomerIndividual();
		String policyNum = createPolicy(td);

		// Change time for any state other than NJ (due to Documents & Bind tab issue)
		if (!getState().equals(Constants.States.NJ)) {
			TimeSetterUtil.getInstance().nextPhase(getTimePoints().getRenewPreviewGenerationDate(PolicySummaryPage.getExpirationDate()));
		}

		mainApp().open();
		SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNum);
		policy.renew().perform();
	}

	private void initiateManualEntry(TestData testData) {
		// Open application Create Customer Initiate Conversion Policy with Driver exceeding MVR predictor threshold.
		mainApp().open();
		createCustomerIndividual();
		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fillUpTo(testData, DriverActivityReportsTab.class, true);
	}
/*
	private void preconditionAddedDrivers(TestData policyTestData){
		mainApp().open();
		createCustomerIndividual();
		policy.initiate();
		policy.getDefaultView().fillUpTo(policyTestData, DriverTab.class, true);
		//policy.getDefaultView().fill(driverTabTD);
	}
*/
	private void preconditionsAddDriversRenewalEndorsement(TestData driverTabTD) {
		//Navigate to Driver Tab and add Drivers
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		policy.getDefaultView().fill(driverTabTD);
		//Navigate to P&C Tab and calculate premium. Submit Tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		premiumAndCoveragesTab.calculatePremium();
		premiumAndCoveragesTab.submitTab();

		// Validate Drivers History
		if (driverActivityReportsTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).isPresent()) {
			driverActivityReportsTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT).setValue("I Agree");
		}
		driverActivityReportsTab.getAssetList().getAsset(AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY).click();
	}

	/**
	 * Renewal MVR Report table Row 1 is empty due to offline MVR batch needing to be ran.  Only validating that row if it is NOT a renewal
	 */
	private void assertAddedDrivers(boolean isRenewal) {
		// Assert That two drivers have license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(3).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		// Assert That other driver does not have license status = predicted valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(4).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		if (!isRenewal) {
			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		}
	}

	/**
	 * Renewal MVR Report table Row 1 is empty due to offline MVR batch needing to be ran.  Only validating that row if it is NOT a renewal
	 */
	private void assertMVRResponseViolations(boolean isRenewal){
		// Assert That drivers with violations do not have license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(3).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(4).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(5).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(6).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS).getValue()).isNotEqualTo("Predicted Valid");
		if (!isRenewal) {
			// Assert That driver with no violations has license status = Predicted Valid
			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		}
		}

	/**
	 * Renewal MVR Report table Row 1 is empty due to offline MVR batch needing to be ran.  Only validating that row if it is NOT a renewal
	 */
	private void assertMVRResponseAccidents(boolean isRenewal){
		// Assert That drivers with manually added accidents (Clue report is successful and clear) has license status = Predicted Valid
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(2).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(3).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(4).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(5).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		assertThat(DriverActivityReportsTab.tableMVRReports.getRow(6).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		if (!isRenewal) {
			assertThat(DriverActivityReportsTab.tableMVRReports.getRow(1).getCell(PolicyConstants.MVRReportTable.LICENSE_STATUS)).hasValue("Predicted Valid");
		}
	}
}
