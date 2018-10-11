package aaa.modules.regression.sales.template.functional;

import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.INSURANCE_SCORE_REPORT;
import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.InsuranceScoreReportRow.CUSTOMER_AGREEMENT;
import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT;
import static toolkit.verification.CustomAssertions.assertThat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.MortgageesTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PropertyInfoTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import org.apache.commons.lang3.Range;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.dp3.functional.TestPARevisedHomeTierAutoNA;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestDisableReorderReport;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;

public class RevisedHomeTierPATemplate extends PolicyBaseTest {

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

    protected void pas6849_TestDisplayAutoTierOnApplicantTab() {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get test data with PA Auto policy
        TestData tdHome = getTdWithAutoPolicy(tdAuto, getPolicyType());

        // Create PA HO policy with companion Auto policy created above
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        // Verify the Auto 'Policy Tier' field is present and select N/A
        assertThat(policyTier).isPresent();
        policyTier.setValue("N/A");

        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            // Add HO policy manually for DP3 requirement
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Submit and continue to the Premiums & Coverages Tab
        applicantTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        // Open the rating details dialogue box and verify Auto Tier
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier")).isEqualTo("N/A");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();
    }

    protected void pas6849_TestAutoNAValueWithNonPACompanionAuto() {
        TestData tdAutoOH = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData_OH")
                .adjust(PrefillTab.class.getSimpleName(), testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_PrefillTab_OH"));

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get test data with PA Auto policy
        TestData tdHome = getTdWithAutoPolicy(tdAutoOH, getPolicyType());

        // Initiate HO policy
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        // Verify the 'Policy Tier' is prefilled to 'N/A' and is disabled
        assertThat(policyTier.getValue()).isEqualTo("N/A");
        assertThat(policyTier.isEnabled()).isFalse();

        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Submit and continue to the Premiums & Coverages Tab
        applicantTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        // Open the rating details dialogue box and verify Auto Tier
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier")).isEqualTo("N/A");
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.close();

        // Verify policy can be bound
        premiumsAndCoveragesQuoteTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    protected void pas6676_TestPAViewRatingDetails() {

        List<String> rangeAutoTier = IntStream.rangeClosed(1, 16).boxed().map(String::valueOf).collect(Collectors.toList());
        rangeAutoTier.add("N/A");

        mainApp().open();
        createCustomerIndividual();

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
        TestData tdHome = getTdWithAutoPolicy(tdAuto, getPolicyType());

        // Initiate Home Policy and add Auto policy as a companion
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Calculate Premium and open View Rating details
        applicantTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);
        PropertyQuoteTab.RatingDetailsView.open();

        // Auto Tier Value is in range of 1-16 or N/A. PAS-6676
        assertThat(rangeAutoTier).contains(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier"));

        // Market Tier is in range of A-J. PAS-7025
        assertThat(rangeMarketTier.contains(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier"))).isTrue();

        // Persistency, Age and Reinstatements points values are displayed. PAS-7024
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Persistency points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Age points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Reinstatements points")).isNotEmpty();

        // Issue Policy
        PropertyQuoteTab.RatingDetailsView.close();
        premiumsAndCoveragesQuoteTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        // Initiate renewal navigate to P&C
        policy.renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

        // Calculate Premium and open View Rating details
        premiumsAndCoveragesQuoteTab.calculatePremium();
        PropertyQuoteTab.RatingDetailsView.open();

        // Auto Tier Value is in range of 1-16 or N/A. PAS-6676
        assertThat(rangeAutoTier).contains(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Auto tier"));

        // Market Tier is in range of A-J. PAS-7025
        assertThat(rangeMarketTier.contains(PropertyQuoteTab.RatingDetailsView.propertyInformation.getValueByKey("Market tier"))).isTrue();

        // Persistency, Age and Reinstatements points values are displayed. PAS-7024
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Persistency points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Age points")).isNotEmpty();
        assertThat(PropertyQuoteTab.RatingDetailsView.values.getValueByKey("Reinstatements points")).isNotEmpty();

        PropertyQuoteTab.RatingDetailsView.close();
        mainApp().close();
    }

    protected void pas6829_TestPrivelegeToEditCompanionAutoTier() {

        // Log in with default User with privilege to edit policy tier
        mainApp().open();
        createCustomerIndividual();

        // Create Required TestData
        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
        TestData tdHome = getTdWithAutoPolicy(tdAuto, getPolicyType());

        // Initiate Home Policy and add Auto policy as a companion
        policy.initiate();

        // Fill Property till Applicant Tab
        policy.getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        // Check if policy tier is enabled
        assertThat(policyTier).isEnabled();

        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Save quote number and close application
        applicantTab.saveAndExit();
        String quoteNr = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // Log in with User with no privilege to edit policy tier
        openAppNonPrivilegedUser("A30");

        // Search for the Quote and navigate to applicant tab
        SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNr);
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

        // Check if policy tier is disabled
        assertThat(policyTier).isDisabled();

        // Issue Policy
        applicantTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHome, ReportsTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        String policyNr = PolicySummaryPage.getPolicyNumber();

        // Endorse Policy
        policy.endorse().perform(getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("Endorsement"), "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

        // Check if policy tier is disabled
        assertThat(policyTier).isDisabled();

        // Close App and log in with privileged user
        mainApp().close();
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNr);

        // Renew Policy and check if policy tier is enabled
        policy.renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        assertThat(policyTier).isEnabled();
        mainApp().close();
    }

    protected void pas6829_TestPrivelegeToEditManualCompanionAutoTier() {

        // Log in with default User with privilege to edit policy tier
        mainApp().open();
        createCustomerIndividual();

        // Create Required TestData
        TestData tdHomeManualAuto = getStateTestData(testDataManager.policy.get(getPolicyType()).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_OtherActive"));

        // Initiate Home Policy and add Auto policy as a companion
        policy.initiate();

        // Fill Property till Applicant Tab
        policy.getDefaultView().fillUpTo(tdHomeManualAuto, ApplicantTab.class, true);

        // Check if policy tier is enabled
        assertThat(policyTier).isEnabled();

        if (getPolicyType().equals(PolicyType.HOME_SS_DP3)) {
            applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES).getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ADD_BTN).click();
            policySearchDialog.cancel();
            applicantTab.fillTab(testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_ManualPolicy"));
        }

        // Save quote number and close application
        applicantTab.saveAndExit();
        String quoteNr = PolicySummaryPage.getPolicyNumber();
        mainApp().close();

        // Log in with User with no privilege to edit policy tier
        openAppNonPrivilegedUser("A30");

        // Search for the Quote and navigate to applicant tab
        SearchPage.search(SearchEnum.SearchFor.QUOTE, SearchEnum.SearchBy.POLICY_QUOTE, quoteNr);
        policy.dataGather().start();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

        // Check if policy tier is disabled
        assertThat(policyTier).isDisabled();

        // Issue Policy
        applicantTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHomeManualAuto, ReportsTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
        String policyNr = PolicySummaryPage.getPolicyNumber();

        // Endorse Policy
        policy.endorse().perform(getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("Endorsement"), "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());

        // Check if policy tier is disabled
        assertThat(policyTier).isDisabled();

        // Close App and log in with privileged user
        mainApp().close();
        mainApp().open();
        SearchPage.search(SearchEnum.SearchFor.POLICY, SearchEnum.SearchBy.POLICY_QUOTE, policyNr);

        // Renew Policy and check if policy tier is enabled
        policy.renew().start().submit();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        assertThat(policyTier).isEnabled();
        mainApp().close();
    }

    public void pas6795_disableReorderReportEndorsement(PolicyType policyType) {

        mainApp().open();
        createPolicyVerifyOverrideLink(policyType);

        // Initiate Endorsement and verify Override Link
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData"));
        verifyReportsTabAndBindPolicy();

    }

    public void pas6827_disableReorderReportRenewal(PolicyType policyType) {

        // Create HO Policy
        mainApp().open();
        createPolicyVerifyOverrideLink(policyType);

        // Initiate Renewal, navigate to Reports Tab
        policyType.get().renew().perform();
        verifyReportsTabAndBindPolicy();
        
    }

    private TestData getTdWithAutoPolicy(TestData tdAuto, PolicyType policyType) {
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        TestData tdOtherActive = testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_OtherActiveAAAPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy number"), PolicySummaryPage.getPolicyNumber());
        return getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActive);
    }

    private void createPolicyVerifyOverrideLink(PolicyType policyType) {
        TestData tdHO = getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData");

        createCustomerIndividual();
        policyType.get().initiate();
        policyType.get().getDefaultView().fillUpTo(tdHO, ReportsTab.class, true);
        assertThat(reportsTab.tblInsuranceScoreOverride.getRow(1).getCell(6).controls.links.getFirst()).isEnabled();
        reportsTab.submitTab();
        policyType.get().getDefaultView().fillFromTo(tdHO, PropertyInfoTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
    }

    private void verifyReportsTabAndBindPolicy() {
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");

        // Verify links
        verifyLinks();

        // Adjust First Name of Primary Insured and verify links
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.getNamedInsuredAssetList().getAsset(HomeSSMetaData.ApplicantTab.NamedInsured.FIRST_NAME).setValue("Test");
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        verifyLinks();

        // Navigate to Applicant tab, add another named insured, and verify links
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.fillTab(testDataManager.getDefault(TestDisableReorderReport.class).getTestData("TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.getAssetList().getAsset(INSURANCE_SCORE_REPORT.getLabel(), FillableTable.class).getAsset(CUSTOMER_AGREEMENT.getLabel(), RadioGroup.class).setValue("Customer agrees");
        reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
        verifyLinks();
        assertThat(reportsTab.tblInsuranceScoreReport.getRow(2).getCell("Report").controls.links.getFirst()).isPresent(false);

        // Bind policy and confirm policy summary page
        reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.get("Re-order report").click();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.submitTab();
        assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();
    }

    private void verifyLinks() {
        reportsTab.tblInsuranceScoreOverride.getRows().forEach(i -> assertThat(i.getCell(6).controls.links.getFirst()).isPresent(false));
        assertThat(reportsTab.tblInsuranceScoreReport.getRow(1).getCell("Report").getValue()).isEqualTo("View report");
        assertThat(reportsTab.tblInsuranceScoreReport.getRow(1).getCell("Report").controls.links.getFirst()).isPresent(true);
    }
}