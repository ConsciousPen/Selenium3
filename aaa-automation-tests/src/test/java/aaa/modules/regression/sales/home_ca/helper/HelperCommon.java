package aaa.modules.regression.sales.home_ca.helper;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.home_ca.defaulttabs.*;
import aaa.modules.policy.HomeCaHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.db.DBService;
import toolkit.utils.datetime.DateTimeUtils;
import toolkit.verification.CustomAssert;
import toolkit.webdriver.controls.composite.assets.MultiAssetList;

public class HelperCommon extends HomeCaHO3BaseTest{
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

    // This creates a customer, policy and return the policy number as a String.
    public String createCustomerPolicyReturnPN(TestData in_customerData, TestData in_policyData)
    {
        createCustomerIndividual(in_customerData);
        return createPolicy(in_policyData);
    }

    // Retrieves an existing policy and initiates a generic endorsement.
    public void doSameDayEndorsement(String policyNumberToOpen, IPolicy in_policy, TestData endorsementTestData)
    {
        //Go to Created Policy.
        SearchPage.openPolicy(policyNumberToOpen);
        // Begin Endorsement. Use Default data for Endorsement Reason Page. Using Custom Data From Adjustment.
        in_policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        in_policy.getDefaultView().fillUpTo(endorsementTestData, PurchaseTab.class, false);
    }
}