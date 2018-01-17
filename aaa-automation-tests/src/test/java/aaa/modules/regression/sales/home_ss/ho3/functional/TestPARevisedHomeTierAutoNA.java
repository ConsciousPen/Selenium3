package aaa.modules.regression.sales.home_ss.ho3.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.SearchEnum;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
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

    /**
     * @author Josh Carpenter
     * @name Test assign N/A value to Auto Tier with existing PA Auto policy under 'Other Active Policies' section
     * @scenario
     * 1. Create customer
     * 2. Create Auto policy for PA
     * 3. Initiate HO3 policy for PA with an effective date on or after 5/28/18
     * 4. Add the above create Auto companion policy on the Applicant Tab
     * 5. Verify the Auto 'Policy Tier' is displayed in place of Auto Insurance Score
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-6849")
    public void pas6849_TestDisplayAutoTierOnApplicantTab(@Optional("PA") String state) {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData");
        TestData tdHome = getPolicyDefaultTD();

        // Change time to meet new algo effective date requirement (on or after 5/28/18)
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

        TestData tdApplicantTab = getTestSpecificTD("TestData_ApplicantTab_nonPA");
        TestData tdAutoOH = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS).getTestData("DataGather"), "TestData_OH");
        TestData tdPrefillTabOH = getTestSpecificTD("TestData_Prefill_OH");

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Create non-PA Auto policy (OH)
//        autoPolicy.initiate();
//        autoPolicy.getDefaultView().fillUpTo(tdPrefillTabOH, PrefillTab.class, true);
//        prefillTab.submitTab();
//        autoPolicy.getDefaultView().fillFromTo(tdAutoOH, GeneralTab.class, PurchaseTab.class, true);
//        purchaseTab.submitTab();

        String autoPolicyOH = PolicySummaryPage.getPolicyNumber();
        tdApplicantTab.adjust(TestData.makeKeyPath("ApplicantTab", "OtherActiveAAAPolicies", "ActiveUnderlyingPoliciesSearch", "Policy Number"), autoPolicyOH);

        // Initiate PA HO3 quote with companion Auto policy created above
        policy.initiate();
        policy.getDefaultView().fillUpTo(getPolicyTD(), ApplicantTab.class);
        applicantTab.fillTab(tdApplicantTab);

        //Verify 'Policy Tier' field exists
        assertThat(applicantTab.getAssetList().getAsset(HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES)
                .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.ACTIVE_UNDERLYING_POLICIES_MANUAL)
                .getAsset(HomeSSMetaData.ApplicantTab.OtherActiveAAAPolicies.OtherActiveAAAPoliciesManual.POLICY_TIER).isPresent());

        // Submit Applicant tab and finish filling up to Premiums & Coverages
        applicantTab.submitTab();
        //policy.getDefaultView().fillFromTo(getPolicyTD(), ReportsTab.class, PremiumsAndCoveragesQuoteTab.class, true);

        //TODO Verify N/A in Rating Details and Policy tier field on Other Active AAA Policies section
    }
}