package aaa.modules.regression.sales.template.functional;

import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import aaa.modules.regression.sales.home_ca.ho3.functional.TestEndorsementWithAutoSelectCompanion;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import static toolkit.verification.CustomAssertions.assertThat;

public class EndorsementWithPendingAutoSelectCompanion extends PolicyBaseTest {

    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();


    public void pas8786_TestEndorsementCalculatePremiumWithPendingAutoSelectCompanion(PolicyType policyType) {

        String autoEffective = TimeSetterUtil.getInstance().getCurrentTime().plusWeeks(2).format(DateTimeUtils.MM_DD_YYYY);

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
                        AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), autoEffective);

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get test data with CA select Auto
        TestData tdHome = getTdWithAutoPolicy(tdAuto, policyType);

        // Create  HO policy with companion Auto policy created above
        policyType.get().createPolicy(tdHome);
        policyType.get().endorse().perform(getStateTestData(testDataManager.policy.get(policyType).getTestData("Endorsement"), "TestData_Plus1Week"));
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());
        premiumsAndCoveragesQuoteTab.calculatePremium();
        assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel())).isPresent();

    }


    private TestData getTdWithAutoPolicy(TestData tdAuto, PolicyType policyType) {
        PolicyType.AUTO_CA_SELECT.get().createPolicy(tdAuto);
        TestData tdOtherActive = testDataManager.getDefault(TestEndorsementWithAutoSelectCompanion.class).getTestData("TestData_OtherActiveAAAPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy number"), PolicySummaryPage.getPolicyNumber());
        return getStateTestData(testDataManager.policy.get(policyType).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActive);
    }
}