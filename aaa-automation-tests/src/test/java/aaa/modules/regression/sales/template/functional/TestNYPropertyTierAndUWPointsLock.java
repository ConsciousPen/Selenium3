package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.BillingSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.ho6.functional.TestNYTierAndUWPointsLock;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.apache.commons.lang3.Range;
import toolkit.datax.TestData;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;

public class TestNYPropertyTierAndUWPointsLock extends PolicyBaseTest {

    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private BindTab bindTab = new BindTab();
    private ReportsTab reportsTab = new ReportsTab();
    private Range<String> rangeMarketTier = Range.between("A", "J");
    private String propertyInfoMessage = "* Market Tier may be a locked value from prior term";


    public void pas14030_TestNYViewRatingDetailsRenewal(PolicyType policyType) {

        mainApp().open();
        createCustomerIndividual();

        TestData tdHome = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Initiate Home Policy and add Auto policy as a companion
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHome, PremiumsAndCoveragesQuoteTab.class, true);
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

        // Initiate renewal. Make Policy Changes to change Total UW Points and Market Tier.
        policyType.get().renew().start().submit();
        policyChangesForTotalUWPointsAndMarketTier();

        // Validate Market Tier and UW points are the same saved value from NB policy.
        assertThat(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier *")).isEqualTo(marketTierValue);
		assertThat(PropertyQuoteTab.RatingDetailsView.propertyInfoMessage.getValue()).contains(propertyInfoMessage);
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Total points")).isEqualTo(totalUWPoints);
        PropertyQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }

    public void pas14030_TestNYViewRatingDetailsRenewalFlatEndorsement(PolicyType policyType) {

        mainApp().open();
        createCustomerIndividual();

        TestData tdHome = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Initiate Home Policy and add Auto policy as a companion
        policyType.get().createPolicy(tdHome);
        String policyNum = PolicySummaryPage.getPolicyNumber();
        LocalDateTime expDate = PolicySummaryPage.getExpirationDate();
        mainApp().close();

        // Change system date
        TimeSetterUtil.getInstance().nextPhase(expDate);
        mainApp().open();
        SearchPage.openPolicy(policyNum);

        // Override errors Issue Renewal
        policyType.get().renew().start().submit();

        //  Navigate to P&C
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

        // Calculate Premium, bind, & purchase policy
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.submitTab();
        purchaseRenewal(policyNum);

        // Navigate to Renewal
        PolicySummaryPage.buttonRenewals.click();

        // Initiate endorsement.
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("Endorsement"), "TestData"));

        //  Navigate to P&C
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.calculatePremium();
        PropertyQuoteTab.RatingDetailsView.open();

        // Market Tier is in range of A-J. Save Market Tier And Total UW Points values.
        assertThat(rangeMarketTier.contains(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier *"))).isTrue();
        String marketTierValue = PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier *");
        String totalUWPoints = PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Total points");
        PropertyQuoteTab.RatingDetailsView.close();

        // Make Policy Changes to change Total UW Points and Market Tier.
        policyChangesForTotalUWPointsAndMarketTier();

        // Validate Market Tier and UW points are the same saved value from NB policy.
        assertThat(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier *")).isEqualTo(marketTierValue);
		assertThat(PropertyQuoteTab.RatingDetailsView.propertyInfoMessage.getValue()).contains(propertyInfoMessage);
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Total points")).isEqualTo(totalUWPoints);
        PropertyQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }


    public void pas14030_TestNYViewRatingDetailsMidTermEndorsement(PolicyType policyType) {

        mainApp().open();
        createCustomerIndividual();

        TestData tdHome = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Initiate Home Policy and add Auto policy as a companion
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHome, PremiumsAndCoveragesQuoteTab.class, true);
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

        // Initiate endorsement. Make Policy Changes to change Total UW Points and Market Tier.
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("Endorsement"), "TestData_Plus1Month"));
        policyChangesForTotalUWPointsAndMarketTier();

        // Validate Market Tier and UW points are the same saved value from NB policy.
        assertThat(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier")).isEqualTo(marketTierValue);
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Total points")).isEqualTo(totalUWPoints);
        PropertyQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }

    public void pas14030_TestNYViewRatingDetailsEndorsement(PolicyType policyType) {

        mainApp().open();
        createCustomerIndividual();

        TestData tdHome = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // Initiate Home Policy and add Auto policy as a companion
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHome, PremiumsAndCoveragesQuoteTab.class, true);
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

        // Initiate endorsement. Make Policy Changes to change Total UW Points and Market Tier.
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("Endorsement"), "TestData"));
        policyChangesForTotalUWPointsAndMarketTier();

        // Validate Market Tier and UW points are recalculated.
        assertThat(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier")).isNotEqualTo(marketTierValue);
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Total points")).isNotEqualTo(totalUWPoints);
        PropertyQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }

    private void policyChangesForTotalUWPointsAndMarketTier(){
        // Override FR Score
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.fillTab(testDataManager.getDefault(TestNYTierAndUWPointsLock.class).getTestData("InsuranceScoreOverride920"));

        //  Navigate to P&C
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

        // Calculate Premium and save the amount
        premiumsAndCoveragesQuoteTab.calculatePremium();
        Dollar premium = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());

        // Change Cov E for bigger premium calculate premium
        premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeSSMetaData.PremiumsAndCoveragesQuoteTab.COVERAGE_E).setValueContains("1,000,000");
        premiumsAndCoveragesQuoteTab.calculatePremium();

        // Check if premium is higher and Open VRD
        Dollar changedPremium = new Dollar(PremiumsAndCoveragesQuoteTab.getPolicyTermPremium());
        assertThat(changedPremium.moreThan(premium)).isTrue();
        PropertyQuoteTab.RatingDetailsView.open();
    }

    private void purchaseRenewal(String policyNumber){
        // Open Billing account and Pay min due for the renewal
        SearchPage.openBilling(policyNumber);
        Dollar due = new Dollar(BillingSummaryPage.getTotalDue());
        new BillingAccount().acceptPayment().perform(testDataManager.billingAccount.getTestData("AcceptPayment", "TestData_Cash"), due);

        // Open Policy
        SearchPage.openPolicy(policyNumber);
    }
}
