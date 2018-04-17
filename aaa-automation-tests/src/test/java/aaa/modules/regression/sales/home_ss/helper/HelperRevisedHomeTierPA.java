package aaa.modules.regression.sales.home_ss.helper;

import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.INSURANCE_SCORE_REPORT;
import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.InsuranceScoreReportRow.CUSTOMER_AGREEMENT;
import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.InsuranceScoreReportRow.ORDER_INSURANCE_SCORE;
import static aaa.main.metadata.policy.HomeSSMetaData.ReportsTab.SALES_AGENT_AGREEMENT;
import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
import aaa.main.modules.policy.home_ss.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ss.dp3.functional.TestPARevisedHomeTierAutoNA;
import aaa.modules.regression.sales.home_ss.ho3.functional.TestDisableReorderReport;
import aaa.toolkit.webdriver.customcontrols.FillableTable;
import aaa.toolkit.webdriver.customcontrols.dialog.SingleSelectSearchDialog;
import toolkit.datax.TestData;
import toolkit.webdriver.controls.ComboBox;
import toolkit.webdriver.controls.RadioGroup;
import toolkit.webdriver.controls.TextBox;

public class HelperRevisedHomeTierPA extends PolicyBaseTest {

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

    private final LocalDateTime algoDate = LocalDateTime.of(2018, Month.JUNE, 1, 0, 0);


    public void pas6849_TestDisplayAutoTierOnApplicantTab(PolicyType policyType) {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");

        // TODO This can be removed after 5/28/18 (effective date requirement for new rating algo)
        TimeSetterUtil.getInstance().confirmDateIsAfter(algoDate);

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
        TimeSetterUtil.getInstance().confirmDateIsAfter(algoDate);

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

        List<String> rangeAutoTier = IntStream.rangeClosed(1, 16).boxed().map(String::valueOf).collect(Collectors.toList());
        rangeAutoTier.add("N/A");

        // TODO This needs to be removed after 5/28/18 (new algo implementation)
        TimeSetterUtil.getInstance().confirmDateIsAfter(algoDate);

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
        policyType.get().getDefaultView().fillFromTo(tdHome, MortgageesTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        // Initiate renewal navigate to P&C
        policyType.get().renew().start().submit();
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


    public void pas6829_TestPrivelegeToEditCompanionAutoTier(PolicyType policyType) {

        // TODO This needs to be removed after 5/28/18 (new algo implementation)
        TimeSetterUtil.getInstance().confirmDateIsAfter(algoDate);

        // Log in with default User with privilege to edit policy tier
        mainApp().open();
        createCustomerIndividual();

        // Create Required TestData
        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
        TestData tdHome = getTdWithAutoPolicy(tdAuto, policyType);

        // Initiate Home Policy and add Auto policy as a companion
        policyType.get().initiate();

        // Fill Property till Applicant Tab
        policyType.get().getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

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
        loginA30();

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
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("Endorsement"), "TestData"));
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
        TimeSetterUtil.getInstance().confirmDateIsAfter(algoDate);

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
        loginA30();

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
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("Endorsement"), "TestData"));
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

    public void pas6795_disableReorderReportEndorsement(PolicyType policyType) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(algoDate);

        mainApp().open();
        createPolicyVerifyOverrideLink(policyType);

        // Initiate Endorsement and verify Override Link
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData"));
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportsTab.tblInsuranceScoreOverride.getRow(1).getCell(6).controls.links.getFirst()).isPresent(false);

        // Navigate to Applicant tab and add another named insured
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.fillTab(testDataManager.getDefault(TestDisableReorderReport.class).getTestData("TestData"));

        // Navigate to Reports tab; verify 'Reorder Report' radio and 'Override Score' link
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        reportsTab.tblInsuranceScoreReport.getRow(2).getCell(ORDER_INSURANCE_SCORE.getLabel()).controls.radioGroups.get(1).setValue("Yes");
        reportsTab.getAssetList().getAsset(INSURANCE_SCORE_REPORT.getLabel(), FillableTable.class).getAsset(CUSTOMER_AGREEMENT.getLabel(), RadioGroup.class).setValue("Customer agrees");
        reportsTab.getAssetList().getAsset(SALES_AGENT_AGREEMENT.getLabel(), RadioGroup.class).setValue("I Agree");
        assertThat(reportsTab.tblInsuranceScoreOverride.getRow(1).getCell(6).controls.links.getFirst()).isPresent(false);
        assertThat(reportsTab.tblInsuranceScoreReport.getRow(2).getCell("Report").controls.links.getFirst()).isPresent(false);

        // Bind the policy
        reportsTab.tblClueReport.getRow(1).getCell(6).controls.links.get("Re-order report").click();
        premiumsAndCoveragesQuoteTab.calculatePremium();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.BIND.get());
        bindTab.submitTab();

        // Verify the policy was bound without rules
        assertThat(PolicySummaryPage.labelPolicyNumber).isPresent();
    }

    public void pas6827_disableReorderReportRenewal(PolicyType policyType) {

        TimeSetterUtil.getInstance().confirmDateIsAfter(algoDate);

        mainApp().open();
        createPolicyVerifyOverrideLink(policyType);

        policyType.get().renew().perform();
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportsTab.tblInsuranceScoreOverride.getRow(1).getCell(6).controls.links.getFirst()).isPresent(false);
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.APPLICANT.get());
        applicantTab.getNamedInsuredAssetList().getAsset("First name", TextBox.class).setValue("Hello");
        NavigationPage.toViewTab(NavigationEnum.HomeSSTab.REPORTS.get());
        assertThat(reportsTab.tblInsuranceScoreReport.getRow(1).getCell("Report").controls.links.getFirst()).isPresent(false);
    }


    private TestData getTdWithAutoPolicy(TestData tdAuto, PolicyType policyType) {
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        TestData tdOtherActive = testDataManager.getDefault(TestPARevisedHomeTierAutoNA.class).getTestData("TestData_OtherActiveAAAPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy number"), PolicySummaryPage.getPolicyNumber());
        return getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActive);
    }


    private void loginA30(){
        TestData loginTD = initiateLoginTD().adjust("Groups", "A30");
        loginTD.adjust("User", "qa_roles");
        mainApp().open(loginTD);
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
}
