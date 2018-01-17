package aaa.modules.regression.sales.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.PrefillTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ss.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ReportsTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;


public class TestPARevisedHomeTierAutoNA extends HomeSSHO3BaseTest {

    private ApplicantTab applicantTab = policy.getDefaultView().getTab(ApplicantTab.class);
    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = policy.getDefaultView().getTab(PremiumsAndCoveragesQuoteTab.class);

    /**
     * @author Josh Carpenter
     * @name Test that the Auto 'Policy Tier' is present on the Applicant tab when adding a PA companion Auto policy and that when the Auto Tier is not available
     * N/A is assigned as the value of Auto Tier after rating calculation.
     * @scenario
     * 1. Create customer
     * 2. Create Auto policy for PA
     * 3. Initiate HO3 policy for PA with an effective date on or after 5/28/18
     * 4. Add the above create Auto companion policy on the Applicant Tab
     * 5. Verify the Auto 'Policy Tier' is displayed in place of Auto Insurance Score
     * 6. Continue to the Premiums & Coverages Tab and calculate
     * 7. Open rating details dialog and confirm the value of N/A was assigned to the companion Auto Tier
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6849")
    public void pas6849_TestDisplayAutoTierOnApplicantTab(@Optional("PA") String state) {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
        TestData tdHome = getPolicyDefaultTD();

        // TODO This can be removed after 5/28/18 (effective date requirement for new rating algo)
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(5));

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto policy
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        TestData tdOtherActive = getTestSpecificTD("OtherActiveAAAPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy Number"), PolicySummaryPage.getPolicyNumber());
        tdHome.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActive);

        // Create PA HO3 policy with companion Auto policy created above
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        assertThat(applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
                .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL)
                .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER).isPresent());

        // Select N/A for the Auto Policy Tier
        applicantTab.getAssetList()
                .getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
                .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL)
                .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER).setValue("N/A");

        // Submit and continue to the Premiums & Coverages Tab
        applicantTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        // Open the rating details dialogue box and verify Auto Tier
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();

        //TODO Verify N/A in Rating Details dialog
    }

    /**
     * @author Josh Carpenter
     * @name Test assign N/A value to Auto Tier with existing companion auto policy from a non-PA state (OH) and verify that when the companion auto
     * tier is populated during pre-fill that the blank Auto Tier value is disabled (greyed out).
     * @scenario
     * 1. Create customer
     * 2. Create non-PA Auto policy (OH)
     * 3. Initiate HO3 policy for PA with an effective date on or after 5/28/18
     * 4. After the Auto Tier is populated during pre-fill, verify the blank Auto Policy Tier value is disabled (greyed out)
     * 5. Proceed with the policy and after calculating premium, open the ratings details dialog
     * 6. Verify Auto tier value assigned to 'N/A'
     * 7. Verify policy can be bound
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6849")
    public void pas6849_TestAutoNAValueWithNonPACompanionAuto(@Optional("PA") String state) {

        TestData tdAutoOH = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData_OH")
                .adjust(PrefillTab.class.getSimpleName(), getTestSpecificTD("TestData_Prefill_OH"));
        TestData tdHome = getPolicyDefaultTD();

        // TODO This can be removed after 5/28/18 (effective date requirement for new rating algo)
        TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime().plusMonths(5));

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Create non-PA Auto policy (OH)
        PolicyType.AUTO_SS.get().createPolicy(tdAutoOH);
        TestData tdOtherActive = getTestSpecificTD("OtherActiveAAAPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy Number"), PolicySummaryPage.getPolicyNumber());
        tdHome.adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActive);

        // Initiate HO policy
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdHome, ApplicantTab.class, true);

        // Verify the 'Policy Tier' is present and disabled
        assertThat(applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
                        .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL)
                        .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER).isPresent() &&

                !applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
                        .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL)
                        .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER).isEnabled());

        // Submit and continue to the Premiums & Coverages Tab
        applicantTab.submitTab();
        policy.getDefaultView().fillFromTo(tdHome, ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        // Open the rating details dialogue box and verify Auto Tier
        PremiumsAndCoveragesQuoteTab.RatingDetailsView.open();

        //TODO Verify N/A in Rating Details dialog

        // Verify policy can be bound
        //TODO bind policy
    }
}