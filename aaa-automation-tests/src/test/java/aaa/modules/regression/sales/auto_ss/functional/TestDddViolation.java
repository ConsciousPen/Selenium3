package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.FIRST_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LAST_NAME;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;

public class TestDddViolation extends AutoSSBaseTest {
	private static final List<String> DRIVERS_WITHOUT_DISCOUNT = Arrays.asList("DriverInformationMinor2", "DriverInformationMajor2", "DriverInformationAlcohol2");
	private static final List<String> DRIVERS_WITH_DISCOUNT = new ArrayList<>(Arrays.asList("DriverInformationMajor1", "DriverInformationAlcohol1"));

	/**
	* * @author Igor Garkusha
	* @name Test NB - Defensive Driver Discount, Minor Violation
	* @scenario
	 * 1. Create Customer.
	* 2. Create Auto SS PA Quote.
	* 3. Add 3 drivers and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1y).
	 * 4. Add 3 drivers and select 'Defensive Driver Course Completed?' Yes (completion date CSD-6M).
	* 5. Fill All other required data up to Driver Activity Reports Tab.
	* 6. Order Reports.
	* 7. Navigate to P&C Tab.
	 * 8. Validate that 3 drivers who had DDDCC CSD-1y are with Defensive driver discount
	* @details
	*/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-2450, PAS-3819")
	public void pas3663_DddForDriverWithMinorViolationCheckNb(@Optional("PA") String state) {

	    mainApp().open();
		createCustomerIndividual(getCustomerTD());

		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()), getDriversTd())
                .adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName()), getTestSpecificTD(DriverActivityReportsTab.class.getSimpleName()));

		policy.initiate();
		policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		CustomAssert.enableSoftMode();
		verifyDrivers();
		CustomAssert.assertAll();

	}

	/**
	* @author Igor Garkusha
	* @name Test Endorsement - Defensive Driver Discount, Minor Violation
	* @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS PA Policy and Endorse it.
	 * 3. Add 3 drivers and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1y).
	 * 4. Add 3 drivers and select 'Defensive Driver Course Completed?' Yes (completion date CSD-6M).
	 * 5. Fill All other required data up to Driver Activity Reports Tab.
	 * 6. Order Reports.
	 * 7. Navigate to P&C Tab.
	 * 8. Validate that 3 drivers who had DDDCC CSD-1y are with Defensive driver discount
	* @details
	*/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-2450, PAS-3819")
	public void pas3663_DddForDriverWithMinorViolationCheckEndorsement(@Optional("PA") String state) {

		mainApp().open();
        createCustomerIndividual(getCustomerTD());
		createPolicy();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		CustomAssert.enableSoftMode();
		renewAndEndorsementSteps();
		CustomAssert.assertAll();
	}

	/** @author Igor Garkusha
	* @name Test Renewal- Defensive Driver Discount, Minor Violation
	* @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS PA Policy and Renew it.
	 * 3. Add 3 drivers and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1y).
	 * 4. Add 3 drivers and select 'Defensive Driver Course Completed?' Yes (completion date CSD-6M).
	 * 5. Fill All other required data up to Driver Activity Reports Tab.
	 * 6. Order Reports.
	 * 7. Navigate to P&C Tab.
	 * 8. Validate that 3 drivers who had DDDCC CSD-1y are with Defensive driver discount
	* @details
	*/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-2450, PAS-3819")
	public void pas3663_DddForDriverWithMinorViolationCheckRenewal(@Optional("PA") String state) {

		mainApp().open();
        createCustomerIndividual(getCustomerTD());
		createPolicy(getBackDatedPolicyTD());
		policy.renew().start();

		CustomAssert.enableSoftMode();
		renewAndEndorsementSteps();
		CustomAssert.assertAll();
	}

	/** @author Dominykas Razgunas
	 * @name Test Conversion - Defensive Driver Discount, Minor Violation
	 * @scenario
	 * 1. Create Customer.
	 * 2. Create Auto SS PA Conversion Policy.
	 * 3. Add 3 drivers and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1y).
	 * 4. Add 3 drivers and select 'Defensive Driver Course Completed?' Yes (completion date CSD-6M). Remove one driver as there can only be 6 for the conversion policy
	 * 5. Fill All other required data up to Driver Activity Reports Tab.
	 * 6. Order Reports.
	 * 7. Navigate to P&C Tab.
	 * 8. Validate that 3 drivers who had DDDCC CSD-1y are with Defensive driver discount
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS, testCaseId = "PAS-2450, PAS-3819")
	public void pas3663_DddForDriverWithMinorViolationCheckConversion(@Optional("PA") String state) {

		mainApp().open();
        createCustomerIndividual(getCustomerTD());

		TestData testData = getConversionPolicyDefaultTD()
                .adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()), getDriversTd())
                .adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName()), getTestSpecificTD(DriverActivityReportsTab.class.getSimpleName()));

		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fillUpTo(testData, DriverTab.class, true);
		new DriverTab().submitTab();
		policy.getDefaultView().fillFromTo(getConversionPolicyDefaultTD(), RatingDetailReportsTab.class, DocumentsAndBindTab.class);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		CustomAssert.enableSoftMode();
		verifyDrivers();
		CustomAssert.assertAll();
	}

	private void renewAndEndorsementSteps() {
		TestData testData = getPolicyTD();
		testData.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()),
				getDriversTd());

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		new DriverTab().fillTab(testData).submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		new PremiumAndCoveragesTab().btnCalculatePremium().click();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		new DriverActivityReportsTab().getAssetList().getAsset(VALIDATE_DRIVING_HISTORY.getLabel(), Button.class).click();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		verifyDrivers();
	}

	private List<TestData> prepareData(List<TestData> drivers, String tdName) {
		drivers.add(DataProviderFactory.emptyData().adjust(drivers.get(1)).adjust(getTestSpecificTD(tdName)).resolveLinks());
		return drivers;
	}

	private void verifyDrivers() {
		PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).verify.contains("Defensive Driving Course Discount");
		DRIVERS_WITH_DISCOUNT.forEach(driver -> checkDriverDiscount(getTestSpecificTD(driver)));
		DRIVERS_WITHOUT_DISCOUNT.forEach(v ->
				CustomAssert.assertFalse(getDriverFullName(getTestSpecificTD(v)) + " should not have discount",
						PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue().contains(getDriverFullName(getTestSpecificTD(v))))
		);
	}

	private void checkDriverDiscount(TestData driverTD) {
		String driverWithDiscountName = getDriverFullName(driverTD);
		//BUG PAS-12755: Defensive driver discount is not displayed properly in driver discount section.
		PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).verify.
				contains(String.format("Defensive Driving Course Discount(%s)", driverWithDiscountName));
	}

	private String getDriverFullName(TestData driverTD) {
		return driverTD.getValue(FIRST_NAME.getLabel()) + " " + driverTD.getValue(LAST_NAME.getLabel());
	}

	private List<TestData> getDriversTd() {
		List<TestData> drivers = new ArrayList<>();
		getTestSpecificTD("TestData").getTestDataList(DriverTab.class.getSimpleName())
                .forEach(v -> drivers.add(DataProviderFactory.emptyData().adjust(v).resolveLinks()));

		DRIVERS_WITH_DISCOUNT.forEach(v -> prepareData(drivers, v));
		DRIVERS_WITHOUT_DISCOUNT.forEach(v -> prepareData(drivers, v));
		DRIVERS_WITH_DISCOUNT.add(0, "DriverInformationMinor1");
		return drivers;
	}

	private TestData getCustomerTD() {
        return getStateTestData(testDataManager.customer.get(CustomerType.INDIVIDUAL), "DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "Paul")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "Magic")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.DATE_OF_BIRTH.getLabel()), "10/17/1950");
    }

}
