package aaa.modules.regression.sales.home_ca.ho4.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO4BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.TestInfo;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class TestSeniorDiscount extends HomeCaHO4BaseTest {

	private static final String AGE_VERIFICATION_SQL = "select ip.age from POLICYSUMMARY ps, INSUREDPRINCIPAL ip\n" +
			"where ps.POLICYDETAIL_ID = ip.POLICYDETAIL_ID \n" +
			"and ps.POLICYNUMBER = '%s'\n";
	private static final String SENIOR_DISCOUNT_NAME = "Senior";
	private GeneralTab generalTab = new GeneralTab();
	private ApplicantTab applicantTab = new ApplicantTab();
	private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();


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
	@TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3, testCaseId = "PAS-3717")
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
		seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, 0);

		seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, 7);

		seniorDiscountDependencyOnEffectiveDate(policyNumber, seniorDiscountApplicabilityAgeYears, -7);

		//PAS-3712 start
		seniorDiscountDwellingUsageCheck("Secondary");
		CustomAssert.assertFalse(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).getValue().contains(SENIOR_DISCOUNT_NAME));

		seniorDiscountDwellingUsageCheck("Primary");
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).verify.contains(SENIOR_DISCOUNT_NAME);
		//PAS-3712 end

		CustomAssert.disableSoftMode();
		CustomAssert.assertAll();
	}

	private void seniorDiscountDwellingUsageCheck(String dwellingUsageValue) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.getInteriorAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Interior.DWELLING_USAGE).setValue(dwellingUsageValue);
		premiumsAndCoveragesQuoteTab.calculatePremium();
	}

	private void seniorDiscountDependencyOnEffectiveDate(String policyNumber, int seniorDiscountApplicabilityAgeYears, int effectiveDateDaysDelta) {
		if (!generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).isPresent()) {
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.GENERAL.get());
		}
		generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(effectiveDateDaysDelta).format(DateTimeUtils.MM_DD_YYYY));

		seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears);
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).verify.contains(SENIOR_DISCOUNT_NAME);

		seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, -1 + effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears - 1);
		CustomAssert.assertFalse(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).getValue().contains(SENIOR_DISCOUNT_NAME));

		seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, 1 + effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears);
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).verify.contains(SENIOR_DISCOUNT_NAME);
	}

	private void seniorDiscountAppliedAndAgeCheck(String policyNumber, int seniorDiscountApplicabilityAgeYears, int dateOfBirthDaysDelta, int ageInDbYears) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
		String seniorDiscountApplicabilityAge = TimeSetterUtil.getInstance().getCurrentTime().minusYears(seniorDiscountApplicabilityAgeYears).minusDays(dateOfBirthDaysDelta).format(DateTimeUtils.MM_DD_YYYY);
		applicantTab.getAssetList().getAsset(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class).getAsset(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue(seniorDiscountApplicabilityAge);
		premiumsAndCoveragesQuoteTab.calculatePremium();
		int ageFromDb = Integer.parseInt(DBService.get().getValue(String.format(AGE_VERIFICATION_SQL, policyNumber)).get());
		CustomAssert.assertEquals(ageFromDb, ageInDbYears);
	}
}
