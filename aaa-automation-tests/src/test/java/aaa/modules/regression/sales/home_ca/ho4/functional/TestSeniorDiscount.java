package aaa.modules.regression.sales.home_ca.ho4.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO4BaseTest;
import aaa.modules.regression.sales.home_ca.helper.HelperCommon;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

public class TestSeniorDiscount extends HomeCaHO4BaseTest {

	private static final String SENIOR_DISCOUNT_NAME = "Senior";
	private HelperCommon helperCommon = new HelperCommon();


	 /**
	 * @author Oleg Stasyuk
	 * @name Test HO3 Policy Senior Discounts
	 * @scenario 1.  Create a quote effective today
	 * 2.  Set DOB to be 65 years in past from quote Effective date, check Senior Discount is applied
	 * 3.  Check Boundary conditions +/-1 day
	 * 4.  Set Quote effective date to be some days in the past
	 * 5.  Set DOB to be 65 years in past from quote Effective date, check Senior Discount is applied
	 * 6.  Check Boundary conditions +/-1 day
	 * 7.  Set Quote effective date to be some days in the future
	 * 8.  Set DOB to be 65 years in past from quote Effective date, check Senior Discount is applied
	 * 9.  Check Boundary conditions +/-1 day
	 * 10.  Set Dwelling usage <> Primary, check discount is not applied
	 * 11.  Set Dwelling usage = Primary, check discount is applied
	 * check age calculation in the middle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = {"PAS-3717", "PAS-3712"})
	public void pas3717_SeniorDiscountsAgeAndDwellingUsage(@Optional("CA") String state) {
		int seniorDiscountApplicabilityAgeYears = 65;

		TestData policyTestData = getPolicyTD("DataGather", "TestData");
		TestData policyTestDataAdjusted = policyTestData.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.DwellingAddress.class.getSimpleName(),
				HomeCaMetaData.ApplicantTab.DwellingAddress.COUNTY.getLabel()), "ShouldBeOptional?");

		mainApp().open();
		createCustomerIndividual();
		policy.createQuote(policyTestDataAdjusted);
		String policyNumber = PolicySummaryPage.getPolicyNumber();

		CustomAssert.enableSoftMode();
		policy.dataGather().start();
		helperCommon.seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, 0, SENIOR_DISCOUNT_NAME);

		helperCommon.seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, 7, SENIOR_DISCOUNT_NAME);

		helperCommon.seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, -7, SENIOR_DISCOUNT_NAME);

		//PAS-3712 start
		helperCommon.seniorDiscountDwellingUsageCheck("Secondary");
		assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).getValue().contains(SENIOR_DISCOUNT_NAME)).isFalse();

		helperCommon.seniorDiscountDwellingUsageCheck("Primary");
		assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1)).valueContains(SENIOR_DISCOUNT_NAME);
		//PAS-3712 end

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}
}
