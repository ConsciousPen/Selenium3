package aaa.modules.regression.sales.auto_ss.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.FIRST_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LAST_NAME;

public class TestDddViolation extends AutoSSBaseTest {
	private static final List<String> DRIVERS_WITHOUT_DISCOUNT = Arrays.asList("DriverInformationMinor2", "DriverInformationMajor2", "DriverInformationAlcohol2");
	private static final List<String> DRIVERS_WITH_DISCOUNT = new ArrayList<>(Arrays.asList("DriverInformationMajor1", "DriverInformationAlcohol1"));

	/**
	 * @author Igor Garkusha
	 * @name Test Paperless Preferences properties and Inquiry mode
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS PA Quote.
	 * 3. Add driverA and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1).
	 * 4. Add driverB and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1).
	 * 5. Fill All other required data up to Premium&Coverages Tab.
	 * 6. Verify that Defensive driver Discount is applied.
	 * 7. Navigate to Driver Activity Reports Tab.
	 * 8. Order Reports.
	 * 9. Navigate to P&C Tab.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "also includes PAS-3822(Major and Alcohol Violation)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3663")
	public void pas3663_DddForDriverWithMinorViolationCheckNb(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();

		TestData testData = getPolicyTD();
		testData.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()), getDriversTd()).
				adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName()),
						getTestSpecificTD(DriverActivityReportsTab.class.getSimpleName()));

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
	 * @scenario 1. Create Auto SS PA policy1.
	 * 2. Endorse policy1.
	 * 3. Add driverA and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1).
	 * 4. Add driverB and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1).
	 * 5. Fill All other required data up to Premium&Coverages Tab.
	 * 6. Verify that Defensive driver Discount is applied.
	 * 7. Navigate to Driver Activity Reports Tab.
	 * 8. Order Reports.
	 * 9. Navigate to P&C Tab.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "also includes PAS-3822(Major and Alcohol Violation)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3663")
	public void pas3663_DddForDriverWithMinorViolationCheckEndorsement(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy();
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		CustomAssert.enableSoftMode();
		renewAndEndorsementSteps();
		CustomAssert.assertAll();
	}

	/**
	 * @author Igor Garkusha
	 * @name Test Renewal- Defensive Driver Discount, Minor Violation
	 * @scenario 1. Create Auto SS PA policy1.
	 * 2. Renew policy1.
	 * 3. Add driverA and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1).
	 * 4. Add driverB and select 'Defensive Driver Course Completed?' Yes (completion date CSD-1).
	 * 5. Fill All other required data up to Premium&Coverages Tab.
	 * 6. Verify that Defensive driver Discount is applied.
	 * 7. Navigate to Driver Activity Reports Tab.
	 * 8. Order Reports.
	 * 9. Navigate to P&C Tab.
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "also includes PAS-3822(Major and Alcohol Violation)")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-3663")
	public void pas3663_DddForDriverWithMinorViolationCheckRenewal(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		createPolicy(getBackDatedPolicyTD());
		policy.renew().start();

		CustomAssert.enableSoftMode();
		renewAndEndorsementSteps();
		CustomAssert.assertAll();
	}

	private void renewAndEndorsementSteps() {
		TestData testData = getPolicyTD();
		testData.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()),
				getDriversTd());

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		new DriverTab().fillTab(testData).submitTab();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		PremiumAndCoveragesTab.buttonCalculatePremium.click();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		new DriverActivityReportsTab().getAssetList().getAsset(VALIDATE_DRIVING_HISTORY.getLabel(), Button.class).click();
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());
		verifyDrivers();
	}

	private List<TestData> prepareData(List<TestData> drivers, String tdName) {
		drivers.add(DataProviderFactory.emptyData().
				adjust(drivers.get(1)).adjust(getTestSpecificTD(tdName)).resolveLinks());
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
		//BUG QC 26288: PAS2_REGR_070-301CL Defensive driver discount is not displayed in driver discount section of policy consolidated page.
		PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).verify.
				contains(String.format("Defensive Driving Course Discount(%s)", driverWithDiscountName));
	}

	private String getDriverFullName(TestData driverTD) {
		return driverTD.getValue(FIRST_NAME.getLabel()) + " " + driverTD.getValue(LAST_NAME.getLabel());
	}

	private List<TestData> getDriversTd() {
		List<TestData> drivers = new ArrayList<>();
		getTestSpecificTD("TestData").
				getTestDataList(DriverTab.class.getSimpleName()).forEach(v ->
				drivers.add(DataProviderFactory.emptyData().adjust(v).resolveLinks()));

		DRIVERS_WITH_DISCOUNT.forEach(v -> prepareData(drivers, v));
		DRIVERS_WITHOUT_DISCOUNT.forEach(v -> prepareData(drivers, v));
		DRIVERS_WITH_DISCOUNT.add(0, "DriverInformationMinor1");
		return drivers;
	}

}
