package aaa.modules.regression.sales.home_ss.helper;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang3.Range;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.home_ss.actiontabs.RenewActionTab;
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
    private Range<String> rangeMarketTier = Range.between("A", "J");
    private IntRange rangeAutoTier = new IntRange(1, 16);

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


    public void pas6676_TestPAViewRatingDetails(PolicyType policyType) {

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

        // Calculate Premium and open View Rating details
        applicantTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        PropertyQuoteTab.RatingDetailsView.open();

        // Auto Tier Value is in range of 1-16. PAS-6676
        assertThat(rangeAutoTier.containsInteger(Integer.parseInt(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier")))).isTrue();

        // Market Tier is in range of A-J. PAS-7025
        assertThat(rangeMarketTier.contains(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier"))).isTrue();

        // Persistency, Age and Reinstatements points values are displayed. PAS-7024
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Persistency points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Age points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Reinstatements points")).isNotEmpty();

        // Issue Policy
        PropertyQuoteTab.RatingDetailsView.close();
        premiumsAndCoveragesQuoteTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        // Initiate renewal navigate to P&C
        policyType.get().renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

        // Calculate Premium and open View Rating details
        premiumsAndCoveragesQuoteTab.calculatePremium();
        PropertyQuoteTab.RatingDetailsView.open();

        // Auto Tier Value is in range of 1-16. PAS-6676
        assertThat(rangeAutoTier.containsInteger(Integer.parseInt(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier")))).isTrue();

        // Market Tier is in range of A-J. PAS-7025
        assertThat(rangeMarketTier.contains(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier"))).isTrue();

        // Persistency, Age and Reinstatements points values are displayed. PAS-7024
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Persistency points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Age points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Reinstatements points")).isNotEmpty();

        PropertyQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }

    public void pas6829_TestPrivelegeToEditCompanionAutoTier(PolicyType policyType) {

        // TODO This needs to be removed after 5/28/18 (new algo implementation)
        verifyAlgoDate();

        // Log in with default User with privilege to edit policy tier
        mainApp().open();
        createCustomerIndividual();

        // Create Required TestData
        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
        TestData tdHome = getTdWithAutoPolicy(tdAuto, policyType);

        // Initiate Home Policy and add Auto policy as a companion
        policyType.get().initiate();

        // Check if policy tier is enabled
        assertThat(policyTier).isEnabled();

        // Fill Property till Applicant Tab
        policyType.get().getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        if (policyType.equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Save quote number and close application
        applicantTab.saveAndExit();
        String quoteNr = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // Log in with User with no privilege to edit policy tier
        TestData loginTD = initiateLoginTD().adjust("Groups", "A30");
        loginTD.adjust("User", "qa_roles");
        mainApp().open(loginTD);

        // Search for the Quote and navigate to applicant tab
        SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNr);
        policyType.get().dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

        // Check if policy tier is disabled
        assertThat(policyTier).isDisabled();

        // Issue Policy
        applicantTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHome, ReportsTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        String policyNr = PolicySummaryPage.getPolicyNumber();

        // Endorse Policy
        policyType.get().endorse().perform(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_Endorse"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

        // Check if policy tier is disabled
        assertThat(policyTier).isDisabled();

        // Close App and log in with privileged user
        mainApp().close();
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNr);

        // Renew Policy and check if policy tier is enabled
        policyType.get().renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        assertThat(policyTier).isEnabled();
        mainApp().close();
    }



    public void pas6829_TestPrivelegeToEditManualCompanionAutoTier(PolicyType policyType) {

        // TODO This needs to be removed after 5/28/18 (new algo implementation)
        verifyAlgoDate();

        // Log in with default User with privilege to edit policy tier
        mainApp().open();
        createCustomerIndividual();

        // Create Required TestData
        TestData tdHomeManualAuto = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_OtherActive"));

        // Initiate Home Policy and add Auto policy as a companion
        policyType.get().initiate();

        // Fill Property till Applicant Tab
        policyType.get().getDefaultView().fillUpTo(tdHomeManualAuto, ApplicantTab.class, true);

        // Check if policy tier is enabled
        assertThat(policyTier).isEnabled();

        if (policyType.equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Save quote number and close application
        applicantTab.saveAndExit();
        String quoteNr = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // Log in with User with no privilege to edit policy tier
        TestData loginTD = initiateLoginTD().adjust("Groups", "A30");
        loginTD.adjust("User", "qa_roles");
        mainApp().open(loginTD);

        // Search for the Quote and navigate to applicant tab
        SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNr);
        policyType.get().dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

        // Check if policy tier is disabled
        assertThat(policyTier).isDisabled();


        // Issue Policy
        applicantTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHomeManualAuto, ReportsTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        String policyNr = PolicySummaryPage.getPolicyNumber();

        // Endorse Policy
        policyType.get().endorse().perform(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_Endorse"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

        // Check if policy tier is disabled
        assertThat(policyTier).isDisabled();

        // Close App and log in with privileged user
        mainApp().close();
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNr);

        // Renew Policy and check if policy tier is enabled
        policyType.get().renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        assertThat(policyTier).isEnabled();
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
