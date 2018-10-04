package aaa.modules.regression.sales.auto_ss.functional;

import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.SALES_AGENT_AGREEMENT;
import static toolkit.verification.CustomAssertions.assertThat;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverActivityReportsTab.VALIDATE_DRIVING_HISTORY;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.FIRST_NAME;
import static aaa.main.metadata.policy.AutoSSMetaData.DriverTab.LAST_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.customer.CustomerType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomSoftAssertions;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.RadioGroup;

@StateList(states = Constants.States.PA)
public class TestDddViolation extends AutoSSBaseTest {

	private static final List<String> DRIVERS_WITHOUT_DISCOUNT = Collections.synchronizedList(new ArrayList<>(Arrays.asList("DriverInformationMajor2", "DriverInformationAlcohol2")));
	private static final List<String> DRIVERS_WITH_DISCOUNT = Collections.synchronizedList(new ArrayList<>(Arrays.asList("DriverInformationMajor1", "DriverInformationAlcohol1")));
	private List<TestData> driversTD;

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
	public void pas2450_testDriversWithViolationsNB(@Optional("PA") String state) {

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusDays(540));

	    mainApp().open();
		createCustomerIndividual(getCustomerTD());

		driversTD = getDriversTd();
		TestData testData = getPolicyTD()
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()), driversTD)
                .adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName()), getTestSpecificTD(DriverActivityReportsTab.class.getSimpleName()));

		policy.initiate();

		policy.getDefaultView().fillUpTo(testData, DocumentsAndBindTab.class);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		verifyDrivers();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM}, priority = 1)
	@TestInfo(component = ComponentConstant.Service.AUTO_SS, testCaseId = "PAS-2450, PAS-3819")
	public void pas2450_testDriversWithViolationsEndorsement(@Optional("PA") String state) {

		mainApp().open();
        createCustomerIndividual(getCustomerTD());

        TestData testData = getPolicyTD()
                .adjust(DriverTab.class.getSimpleName(), getTestSpecificTD("DriverInformationMinor1"))
                .adjust(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
                        AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_DEFENSIVE_DRIVER_COURSE_COMPLETION.getLabel()), "Yes");

		createPolicy(testData);
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));

		renewAndEndorsementSteps();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM}, priority = 1)
	@TestInfo(component = ComponentConstant.Renewal.AUTO_SS, testCaseId = "PAS-2450, PAS-3819")
	public void pas2450_testDriversWithViolationsRenewal(@Optional("PA") String state) {

		mainApp().open();
        createCustomerIndividual(getCustomerTD());

        TestData testData = getBackDatedPolicyTD()
                .adjust(DriverTab.class.getSimpleName(), getTestSpecificTD("DriverInformationMinor1"))
                .adjust(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(),
                        AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_DEFENSIVE_DRIVER_COURSE_COMPLETION.getLabel()), "Yes");

		createPolicy(testData);
		policy.renew().start();

		renewAndEndorsementSteps();
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM}, priority = 1)
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS, testCaseId = "PAS-2450, PAS-3819")
	public void pas2450_testDriversWithViolationsConversion(@Optional("PA") String state) {

		mainApp().open();
        createCustomerIndividual(getCustomerTD());

		TestData testData = getConversionPolicyDefaultTD()
                .adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()), driversTD)
                .adjust(TestData.makeKeyPath(DriverActivityReportsTab.class.getSimpleName()), getTestSpecificTD(DriverActivityReportsTab.class.getSimpleName()));

		customer.initiateRenewalEntry().perform(getManualConversionInitiationTd());
		policy.getDefaultView().fillUpTo(testData, DriverTab.class, true);
		new DriverTab().submitTab();
		policy.getDefaultView().fillFromTo(getConversionPolicyDefaultTD(), RatingDetailReportsTab.class, DocumentsAndBindTab.class);
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		verifyDrivers();
	}

	private void renewAndEndorsementSteps() {
		TestData testData = getPolicyTD().adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName()), driversTD);
		RadioGroup saleAgentAgreement = new DriverActivityReportsTab().getAssetList().getAsset(SALES_AGENT_AGREEMENT);

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		new DriverTab().fillTab(testData).submitTab();

		new PremiumAndCoveragesTab().calculatePremium();

		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER_ACTIVITY_REPORTS.get());
		if (saleAgentAgreement.isPresent()) {
			saleAgentAgreement.setValue("I Agree");
		}
		new DriverActivityReportsTab().getAssetList().getAsset(VALIDATE_DRIVING_HISTORY.getLabel(), Button.class).click();
		new PremiumAndCoveragesTab().calculatePremium();
		verifyDrivers();
	}

	private List<TestData> prepareData(List<TestData> drivers, String tdName) {
		drivers.add(DataProviderFactory.emptyData().adjust(drivers.get(1)).adjust(getTestSpecificTD(tdName)).resolveLinks());
		return drivers;
	}

	private void verifyDrivers() {
		CustomSoftAssertions.assertSoftly(softly -> {
			softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1)).valueContains("Defensive Driving Course Discount");
			DRIVERS_WITH_DISCOUNT.forEach(driver -> checkDriverDiscount(getTestSpecificTD(driver)));
			DRIVERS_WITHOUT_DISCOUNT.forEach(v -> softly.assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue())
					.as(getDriverFullName(getTestSpecificTD(v)) + " should not have discount").doesNotContain(getDriverFullName(getTestSpecificTD(v)))
			);
		});
	}

	private void checkDriverDiscount(TestData driverTD) {
		String driverWithDiscountName = getDriverFullName(driverTD);
		//BUG PAS-12755: Defensive driver discount is not displayed properly in driver discount section.
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1)).valueContains(String.format("Defensive Driving Course Discount(%s)", driverWithDiscountName));
	}

	private String getDriverFullName(TestData driverTD) {
		String fullName = driverTD.getValue(FIRST_NAME.getLabel()) + " " + driverTD.getValue(LAST_NAME.getLabel());
		if ("null null".equals(fullName)) {
		    return "Paul Magic";
        }
        return fullName;
	}

	private List<TestData> getDriversTd() {
		List<TestData> drivers = new ArrayList<>();
		getTestSpecificTD("TestData").getTestDataList(DriverTab.class.getSimpleName()).forEach(v -> drivers.add(DataProviderFactory.emptyData().adjust(v).resolveLinks()));

		DRIVERS_WITH_DISCOUNT.forEach(v -> prepareData(drivers, v));
		DRIVERS_WITHOUT_DISCOUNT.forEach(v -> prepareData(drivers, v));
		DRIVERS_WITH_DISCOUNT.add(0, "DriverInformationMinor1");
        DRIVERS_WITH_DISCOUNT.add(0, "DriverInformationMinor2");
		return drivers;
	}

	private TestData getCustomerTD() {
        return getStateTestData(testDataManager.customer.get(CustomerType.INDIVIDUAL), "DataGather", "TestData")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.FIRST_NAME.getLabel()), "Paul")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.LAST_NAME.getLabel()), "Magic")
                .adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.DATE_OF_BIRTH.getLabel()), "10/17/1950");
    }

}
