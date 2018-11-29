package aaa.modules.regression.sales.auto_ss.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.auto_ss.defaulttabs.*;
import aaa.modules.policy.AutoSSBaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.IN, Constants.States.KY, Constants.States.MD, Constants.States.NJ, Constants.States.NV,
		Constants.States.PA, Constants.States.VA, Constants.States.OK, Constants.States.CT})
public class TestGoodStudentDiscount extends AutoSSBaseTest {

	/**
	 * @author Dominykas Razgunas
	 * @name MVR Predictor Algo for drivers viable for Good Student Discount NB
	 * @scenario
	 * 1. Create Customer1.
	 * 2. Create Auto SS Quote.
	 * 3. Add 1 Driver who is eligible for GSD
	 * 4. Add 5 Drivers who are not eligible for GSD with following:
	 * 5. Driver is a rated driver AND
	 * 6. Driver age is between 16 and 25 years AND
	 * 7. Driver is single, separated or divorced AND
	 * 8. Driver is a Student or a college graduate AND
	 * 9. Driver have maintained a grade of at least B in letter grading system or at least 3.00 in a 4 point numerical grading system or a College Graduate.
	 * 10. Calculate Premium
	 * 11. Assert Discounts
	 * @details
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for Good Student Discount NB")
	@TestInfo(component = ComponentConstant.Sales.AUTO_SS, testCaseId = "PAS-10108")
	public void pas10108_GoodStudentDiscountMVRPredictorNB(@Optional("") String state) {

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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for Good Student Discount Endorsement")
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
	@Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "MVR Predictor Algo for Good Student Discount Renewal")
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
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).contains("Good Student Discount(DriverOne LastNameOne)");

		// Assert 5 Drivers who are not eligible for GSD
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(DriverTwo LastNameTwo)");
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(DriverThree LastNameThree)");
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(DriverFour LastNameFour)");
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(DriverFive LastNameFive)");
		assertThat(PremiumAndCoveragesTab.tableDiscounts.getRow(1).getCell(1).getValue()).doesNotContain("Good Student Discount(DriverSix LastNameSix)");
	}

	private void preconditionAddedDrivers(TestData policyTestData, TestData driverTabTD){
		createQuoteAndFillUpTo(policyTestData, DriverTab.class);
		policy.getDefaultView().fill(driverTabTD);
	}

	private TestData getPolicyTDforGSD(){
		// adjust policy TD so that first driver is eligible for GSD
		return getPolicyTD()
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]", AutoSSMetaData.GeneralTab.NamedInsuredInformation.FIRST_NAME.getLabel()), "DriverOne")
				.adjust(TestData.makeKeyPath(GeneralTab.class.getSimpleName(), AutoSSMetaData.GeneralTab.NAMED_INSURED_INFORMATION.getLabel() + "[0]", AutoSSMetaData.GeneralTab.NamedInsuredInformation.LAST_NAME.getLabel()), "LastNameOne")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.DATE_OF_BIRTH.getLabel()), "01/01/2000")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.MARITAL_STATUS.getLabel()), "Single")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.OCCUPATION.getLabel()), "Student")
				.adjust(TestData.makeKeyPath(DriverTab.class.getSimpleName(), AutoSSMetaData.DriverTab.MOST_RECENT_GPA.getLabel()), "A Student")
				.adjust(TestData.makeKeyPath(DocumentsAndBindTab.class.getSimpleName(), AutoSSMetaData.DocumentsAndBindTab.REQUIRED_TO_ISSUE.getLabel(), AutoSSMetaData.DocumentsAndBindTab.RequiredToIssue.PROOF_OF_GOOD_STUDENT_DISCOUNT.getLabel()), "Yes");
	}
}