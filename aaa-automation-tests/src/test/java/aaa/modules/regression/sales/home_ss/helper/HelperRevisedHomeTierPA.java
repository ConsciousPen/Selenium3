package aaa.modules.regression.sales.home_ss.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang3.Range;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.dp3.functional.TestPARevisedHomeTierAutoNA;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;

public class HelperRevisedHomeTierPA extends PolicyBaseTest {

    private ApplicantTab applicantTab = new ApplicantTab();
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();
    private PurchaseTab purchaseTab = new PurchaseTab();
    private Range<String> range = Range.between("A", "J");

    private ComboBox policyTier = applicantTab.getAssetList()
            .getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
            .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL)
            .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER);

    private SingleSelectSearchDialog policySearchDialog = applicantTab.getAssetList()
            .getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
            .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_SEARCH);


    public void pas6849_TestDisplayAutoTierOnApplicantTab(PolicyType policyType) {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");

        // TODO This can be removed after 5/28/18 (effective date requirement for new rating algo)
        verifyAlgoDate();

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get test data with PA Auto policy
        TestData tdHome = getTdWithAutoPolicy(tdAuto, policyType);

        // Create PA HO policy with companion Auto policy created above
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        // Verify the Auto 'Policy Tier' field is present and select N/A
        assertThat(policyTier).isPresent();
        policyTier.setValue("N/A");

        if (policyType.equals(PolicyType.HOME_SS_DP3)) {
            // Add HO policy manually for DP3 requirement
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Submit and continue to the Premiums & Coverages Tab
        applicantTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        // Open the rating details dialogue box and verify Auto Tier
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier")).isEqualTo("N/A");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
    }


    public void pas6849_TestAutoNAValueWithNonPACompanionAuto(PolicyType policyType) {
        TestData tdAutoOH = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData_OH")
                .adjust(PrefillTab.class.getSimpleName(), testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_PrefillTab_OH"));

        // TODO This can be removed after 5/28/18 (effective date requirement for new rating algo)
        verifyAlgoDate();

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get test data with PA Auto policy
        TestData tdHome = getTdWithAutoPolicy(tdAutoOH, policyType);

        // Initiate HO policy
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        // Verify the 'Policy Tier' is prefilled to 'N/A' and is disabled
        assertThat(policyTier.getValue()).isEqualTo("N/A");
        assertThat(policyTier.isEnabled()).isFalse();

        if (policyType.equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Submit and continue to the Premiums & Coverages Tab
        applicantTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        // Open the rating details dialogue box and verify Auto Tier
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier")).isEqualTo("N/A");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

        // Verify policy can be bound
        premiumsAndCoveragesQuoteTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }


    public void pas6676_TestPAViewRatingDetailsAutoTier(PolicyType policyType) {

        IntRange range = new IntRange(1, 16);

        // TODO This needs to be removed after 5/28/18 (new algo implementation)
        verifyAlgoDate();

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

        applicantTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        // Assert that the Auto Tier Rating present and is between 1-16
        PropertyQuoteTab.RatingDetailsView.open();
        assertThat(range.containsInteger(Integer.parseInt(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier")))).isTrue();
        PropertyQuoteTab.RatingDetailsView.close();

        // Issue Policy and initiate renewal
        premiumsAndCoveragesQuoteTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        policyType.get().renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.calculatePremium();

        // Assert that the Auto Tier Rating present and is between 1-16
        PropertyQuoteTab.RatingDetailsView.open();
        assertThat(range.containsInteger(Integer.parseInt(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier")))).isTrue();
        PropertyQuoteTab.RatingDetailsView.close();

        mainApp().close();

    }

    public void pas7025_TestPAPropertyTierChange(PolicyType policyType) {

        TestData tdHome = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        // TODO This needs to be removed after 5/28/18 (new algo implementation)
        verifyAlgoDate();

        // Open App create Policy Navigate to P&C page calculate premium
        mainApp().open();
        createCustomerIndividual();
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHome, PremiumsAndCoveragesQuoteTab.class, true);
        PropertyQuoteTab.linkViewRatingDetails.click();

        // Check if new algo is implemented
        assertThat(range.contains( PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier"))).isTrue();

        // Issue Policy
        PropertyQuoteTab.RatingDetailsView.close();
        premiumsAndCoveragesQuoteTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        //Initiate renewal and Navigate to P&C page calculate premium
        policyType.get().renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.calculatePremium();

        // Check if the algo is implemented
        PropertyQuoteTab.RatingDetailsView.open();
        assertThat(range.contains( PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier"))).isTrue();
        PropertyQuoteTab.RatingDetailsView.close();
        mainApp().close();

    }

    public void pas7024_TestPARatingValueSection(PolicyType policyType) {


        // TODO This needs to be removed after 5/28/18 (new algo implementation)
        verifyAlgoDate();

        mainApp().open();
        createCustomerIndividual();
        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
        TestData tdHome = getTdWithAutoPolicy(tdAuto, policyType);

        // Policy Navigate to P&C page calculate premium

        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        // Adjust TestData for DP3 policies
        if (policyType.equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        applicantTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        PropertyQuoteTab.linkViewRatingDetails.click();

        // Check if new algo is implemented
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Persistency points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Age points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Reinstatements points")).isNotEmpty();


        // Issue Policy
        PropertyQuoteTab.RatingDetailsView.close();
        premiumsAndCoveragesQuoteTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        //Initiate renewal and Navigate to P&C page calculate premium
        policyType.get().renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.calculatePremium();

        // Check if the algo is implemented
        PropertyQuoteTab.RatingDetailsView.open();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Persistency points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Age points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Reinstatements points")).isNotEmpty();

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


    public void verifyAlgoDate() {
        LocalDateTime algoEffectiveDate = LocalDateTime.of(2018, Month.JUNE, 1, 0, 0);
        if (TimeSetterUtil.getInstance().getCurrentTime().isBefore(algoEffectiveDate)) {
            TimeSetterUtil.getInstance().nextPhase(algoEffectiveDate);
        }
    }
}
