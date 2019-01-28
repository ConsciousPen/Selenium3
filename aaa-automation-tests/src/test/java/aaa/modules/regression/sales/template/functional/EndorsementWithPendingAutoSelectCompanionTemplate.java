package aaa.modules.regression.sales.template.functional;

import static toolkit.verification.CustomAssertions.assertThat;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class EndorsementWithPendingAutoSelectCompanionTemplate extends PolicyBaseTest {

    private PremiumsAndCoveragesQuoteTab premiumsAndCoveragesQuoteTab = new PremiumsAndCoveragesQuoteTab();


    protected void pas8786_TestEndorsementRateWithPendingAutoSelectCompanion() {

        String autoEffective = TimeSetterUtil.getInstance().getCurrentTime().plusWeeks(2).format(DateTimeUtils.MM_DD_YYYY);

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_CA_SELECT).getTestData("DataGather"), "TestData")
                .adjust(TestData.makeKeyPath(AutoCaMetaData.GeneralTab.class.getSimpleName(),
                        AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoCaMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel()), autoEffective);

        // Create the customer
        mainApp().open();
        createCustomerIndividual();

        // Get test data with CA select Auto
        TestData tdHome = getTdWithAutoPolicy(tdAuto);

        // Create Property policy with companion Auto policy created above
        createPolicy(tdHome);

        // Endorse Policy with effective date Prior to Pending Auto Select Policy
        policy.endorse().perform(getPolicyTD("Endorsement", "TestData_Plus1Week"));

        // Navigate from Applicant Tab to reports tab the error should not be thrown
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.APPLICANT.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.REPORTS.get());

        // Navigate to P&C
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES.get());
        NavigationPage.toViewTab(NavigationEnum.HomeCaTab.PREMIUMS_AND_COVERAGES_QUOTE.get());

        // Calculate Premium and check that Error tab is not thrown
        premiumsAndCoveragesQuoteTab.calculatePremium();
        assertThat(premiumsAndCoveragesQuoteTab.getAssetList().getAsset(HomeCaMetaData.PremiumsAndCoveragesQuoteTab.PAYMENT_PLAN.getLabel())).isPresent();
    }


    private TestData getTdWithAutoPolicy(TestData tdAuto) {
        PolicyType.AUTO_CA_SELECT.get().createPolicy(tdAuto);
        TestData tdOtherActive = testDataManager.getDefault(EndorsementWithPendingAutoSelectCompanionTemplate.class).getTestData("TestData_OtherActiveAAAPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy number"), PolicySummaryPage.getPolicyNumber());
        return getPolicyTD("DataGather", "TestData")
                .adjust(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeCaMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()), tdOtherActive);
    }
}