package aaa.modules.regression.sales.home_ca.ho4;

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
import toolkit.verification.CustomSoftAssertions;

public class TestSeniorDiscount extends HomeCaHO4BaseTest {

	private static final String SENIOR_DISCOUNT_NAME = "Senior";
	private HelperCommon helperCommon = new HelperCommon();


	 /**
	 * @author Oleg Stasyuk
	  * <b> Test HO3 Policy Senior Discounts </b>
	  * <p> Steps: 1.  Create a quote effective today
	  * <p> 2.  Set DOB to be 65 years in past from quote Effective date, check Senior Discount is applied
	  * <p> 3.  Check Boundary conditions +/-1 day
	  * <p> 4.  Set Quote effective date to be some days in the past
	  * <p> 5.  Set DOB to be 65 years in past from quote Effective date, check Senior Discount is applied
	  * <p> 6.  Check Boundary conditions +/-1 day
	  * <p> 7.  Set Quote effective date to be some days in the future
	  * <p> 8.  Set DOB to be 65 years in past from quote Effective date, check Senior Discount is applied
	  * <p> 9.  Check Boundary conditions +/-1 day
	  * <p> 10.  Set Dwelling usage <> Primary, check discount is not applied
	  * <p> 11.  Set Dwelling usage = Primary, check discount is applied
	  * <p> check age calculation in the middle
	 */
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
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

		CustomSoftAssertions.assertSoftly(softly -> {
			policy.dataGather().start();
			helperCommon.seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, 0, SENIOR_DISCOUNT_NAME, softly);

			helperCommon.seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, 7, SENIOR_DISCOUNT_NAME, softly);

			helperCommon.seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, -7, SENIOR_DISCOUNT_NAME, softly);

			//PAS-3712 start
			helperCommon.seniorDiscountDwellingUsageCheck("Secondary");
			softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).getValue().contains(SENIOR_DISCOUNT_NAME)).isFalse();

			helperCommon.seniorDiscountDwellingUsageCheck("Primary");
			softly.assertThat(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1)).valueContains(SENIOR_DISCOUNT_NAME);
			//PAS-3712 end
		});
	}
}
