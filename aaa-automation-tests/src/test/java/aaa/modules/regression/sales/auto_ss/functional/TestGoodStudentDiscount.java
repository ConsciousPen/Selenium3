package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverActivityReportsTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.ErrorTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.RatingDetailReportsTab;
import aaa.modules.policy.AutoSSBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestGoodStudentDiscount extends AutoSSBaseTest {

	private ErrorTab errorTab = new ErrorTab();
	private PremiumAndCoveragesTab premiumAndCoveragesTab = new PremiumAndCoveragesTab();
	private DriverActivityReportsTab driverActivityReportsTab = new DriverActivityReportsTab();

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers viable for Good Student Discount NB
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Quote.
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "MVR Predictor Algo for Good Student Discount NB")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10108")
	public void pas10108_GoodStudentDiscountMVRPredictorNB(@Optional("NV") String state) {

		TestData driverTab = getTestSpecificTD("TestData_DriverTabGSD").resolveLinks();

		// Add 1 Driver who is eligible for GSD
		// Add 5 Drivers who are not eligible for GSD
		preconditionAddedDrivers(getPolicyTDforGSD(), driverTab);

		// Fill remaining policy to P&C tab
		policy.getDefaultView().fillFromTo(getPolicyTDforGSD(), RatingDetailReportsTab.class, PremiumAndCoveragesTab.class, true);

		assertGSD();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers viable for Good Student Discount Endorsement
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy. Initiate Endorsement
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "MVR Predictor Algo for Good Student Discount Endorsement")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10108")
	public void pas10108_GoodStudentDiscountMVRPredictorEndorsement(@Optional("") String state) {

		TestData driverTab = getTestSpecificTD("TestData_DriverTabGSD").resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		createPolicy(getPolicyTDforGSD());

		// Add 5 Drivers who are not eligible for GSD
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		policy.getDefaultView().fill(driverTab);

		// Navigate to P&C tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		assertGSD();
	}

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers viable for Good Student Discount Renewal
	 * @scenario 1. Create Customer1.
	 * 2. Create Auto SS Policy. Initiate Renewal.
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
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "MVR Predictor Algo for Good Student Discount Renewal")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10108")
	public void pas10108_GoodStudentDiscountMVRPredictorRenewal(@Optional("") String state) {

		TestData driverTab = getTestSpecificTD("TestData_DriverTabGSD").resolveLinks();

		mainApp().open();
		createCustomerIndividual();
		createPolicy(getPolicyTDforGSD());

		// Add 5 Drivers who are not eligible for GSD
		policy.endorse().perform(getPolicyTD("Endorsement", "TestData"));
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.DRIVER.get());
		policy.getDefaultView().fill(driverTab);

		// Navigate to P&C tab
		NavigationPage.toViewTab(NavigationEnum.AutoSSTab.PREMIUM_AND_COVERAGES.get());

		assertGSD();
		}

	private void assertGSD(){
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

	private TestData getPolicyTDforGSD(){
		// adjust policy TD so that first driver is eligible for GSD
		return getPolicyTD()
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]", AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel()), "Driver1")
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]", AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel()), "LastName1")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/2000")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel()), "Single")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.OCCUPATION.getLabel()), "Student")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.MOST_RECENT_GPA.getLabel()), "A Student")
				.adjust(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel()), "Yes");
	}
}