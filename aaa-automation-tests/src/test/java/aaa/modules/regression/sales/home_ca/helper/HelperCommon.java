package aaa.modules.regression.sales.home_ca.helper;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.GeneralTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PropertyInfoTab;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class HelperCommon {
	private static final String AGE_VERIFICATION_SQL = "select ip.age from POLICYSUMMARY ps, INSUREDPRINCIPAL ip\n" +
			"where ps.POLICYDETAIL_ID = ip.POLICYDETAIL_ID \n" +
			"and ps.POLICYNUMBER = '%s'\n";
	private GeneralTab generalTab = new GeneralTab();
	private ApplicantTab applicantTab = new ApplicantTab();
	private PropertyInfoTab propertyInfoTab = new PropertyInfoTab();
	private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();


	public void seniorDiscountDwellingUsageCheck(String dwellingUsageValue) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PROPERTY_INFO.get());
		propertyInfoTab.getInteriorAssetList().getAsset(HomeCaMetaData.PropertyInfoTab.Interior.DWELLING_USAGE).setValue(dwellingUsageValue);
		premiumsAndCoveragesQuoteTab.calculatePremium();
	}

	public void seniorDiscountDependencyOnEffectiveDate(String policyNumber, int seniorDiscountApplicabilityAgeYears, int effectiveDateDaysDelta, String seniorDiscountName) {
		if (!generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).isPresent()) {
			NavigationPage.toViewTab(NavigationEnum.HomeCaTab.GENERAL.get());
		}
		generalTab.getPolicyInfoAssetList().getAsset(HomeCaMetaData.GeneralTab.PolicyInfo.EFFECTIVE_DATE).setValue(TimeSetterUtil.getInstance().getCurrentTime().minusDays(effectiveDateDaysDelta).format(DateTimeUtils.MM_DD_YYYY));

		seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears);
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).verify.contains(seniorDiscountName);
		seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "Yes");

		seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, -1 + effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears - 1);
		CustomAssert.assertFalse(PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).getValue().contains(seniorDiscountName));
		seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "No");

		seniorDiscountAppliedAndAgeCheck(policyNumber, seniorDiscountApplicabilityAgeYears, 1 + effectiveDateDaysDelta, seniorDiscountApplicabilityAgeYears);
		PremiumsAndCoveragesQuoteTab.tableDiscounts.getRow(1).getCell(1).verify.contains(seniorDiscountName);
		seniorDiscountViewRatingDetailsCheck(seniorDiscountName, "Yes");
	}


	public void seniorDiscountViewRatingDetailsCheck(String seniorDiscountName, String seniorDiscountValue) {
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
		//BUG QC 44971 Regression: Senior discount is not displayed in rating details dialog
		switch(seniorDiscountName) {
			case "Senior":
				CustomAssert.assertEquals(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Senior Discount"), seniorDiscountValue);
				break;
			case "Mature Policy Holder":
				CustomAssert.assertEquals(PremiumsAndCoveragesQuoteTab.RatingDetailsView.discounts.getValueByKey("Mature policy holder"), seniorDiscountValue);
				break;
		}
		PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
	}


	public void seniorDiscountAppliedAndAgeCheck(String policyNumber, int seniorDiscountApplicabilityAgeYears, int dateOfBirthDaysDelta, int ageInDbYears) {
		NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
		String seniorDiscountApplicabilityAge = TimeSetterUtil.getInstance().getCurrentTime().minusYears(seniorDiscountApplicabilityAgeYears).minusDays(dateOfBirthDaysDelta).format(DateTimeUtils.MM_DD_YYYY);
		applicantTab.getAssetList().getAsset(HomeCaMetaData.ApplicantTab.NAMED_INSURED.getLabel(), MultiAssetList.class).getAsset(HomeCaMetaData.ApplicantTab.NamedInsured.DATE_OF_BIRTH).setValue(seniorDiscountApplicabilityAge);
		premiumsAndCoveragesQuoteTab.calculatePremium();
		int ageFromDb = Integer.parseInt(DBService.get().getValue(String.format(AGE_VERIFICATION_SQL, policyNumber)).get());
		CustomAssert.assertEquals(ageFromDb, ageInDbYears);
	}
}
