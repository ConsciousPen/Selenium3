package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.BillingConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.dp3.functional.TestPARevisedHomeTierAutoNA;
import aaa.modules.regression.sales.home_ss.ho6.functional.TestNYTierAndUWPointsLock;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.apache.commons.lang3.Range;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestNYPropertyTierAndUWPointsLock extends PolicyBaseTest {

    private ApplicantTab applicantTab = new ApplicantTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private ReportsTab reportsTab = new ReportsTab();
    private BindTab bindTab = new BindTab();
    private Range<String> rangeMarketTier = Range.between("A", "J");

    private ComboBox policyTier = applicantTab.getAssetList()
            .getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
            .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL)
            .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER);

    private SingleSelectSearchDialog policySearchDialog = applicantTab.getAssetList()
            .getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
            .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH);


    public void pas14030_TestNYViewRatingDetailsRenewal(PolicyType policyType) {

        mainApp().open();
        createCustomerIndividual();

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
        TestData tdHome = getTdWithAutoPolicy(tdAuto, policyType);

        // Initiate Home Policy and add Auto policy as a companion
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        if (policyType.equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Calculate Premium and open View Rating details
        applicantTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        PropertyQuoteTab.RatingDetailsView.open();

        // Market Tier is in range of A-J. Save Market Tier And Total UW Points values.
        assertThat(rangeMarketTier.contains(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier"))).isTrue();
        String marketTierValue = PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier");
        String totalUWPoints = PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Total points");

        // Issue Policy
        PropertyQuoteTab.RatingDetailsView.close();
        premiumsAndCoveragesQuoteTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        // Initiate renewal. Override Insurance Score.
        policyType.get().renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.fillTab(testDataManager.getDefault(TestNYTierAndUWPointsLock.class).getTestData("InsuranceScoreOverride920"));

        //  Navigate to P&C
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

        // Calculate Premium and save the amount
        premiumsAndCoveragesQuoteTab.calculatePremium();
        Dollar premium = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());

        // Change payment plan for bigger premium calculate premium
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN).setValue(BillingConstants.PaymentPlan.QUARTERLY_RENEWAL);
        premiumsAndCoveragesQuoteTab.calculatePremium();

        // Check if premium is higher and Open VRD
        Dollar changedPremium = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
        assertThat(changedPremium.moreThan(premium)).isTrue();
        PropertyQuoteTab.RatingDetailsView.open();

        // Validate Market Tier and UW points are the same saved value from NB policy.
        assertThat(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier")).isEqualTo(marketTierValue);
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Total points")).isEqualTo(totalUWPoints);
        PropertyQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }


    private TestData getTdWithAutoPolicy(TestData tdAuto, PolicyType policyType) {
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        TestData tdOtherActive = testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_OtherActiveAAAPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy number"), PolicySummaryPage.getPolicyNumber());
        return getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActive);
    }


}
