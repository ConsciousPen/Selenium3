package aaa.modules.regression.sales.pup.functional;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.abstract_tabs.PropertyQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.*;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPupInfoSectionViewRatingDetails extends PersonalUmbrellaBaseTest {

    PremiumAndCoveragesQuoteTab premiumAndCoveragesQuoteTab = new PremiumAndCoveragesQuoteTab();

    /**
     * @author Dominykas Razgunas
     * @name Test PUP policies that have an underlying DP3 policy with multiple units is rated properly for SS states.
     * @scenario
     * 1. Create customer
     * 2. Create Auto policy
     * 3. Create HO3 policy with underlying Auto from above
     * 4. Initiate PUP Policy
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-10397")
    public void pas10397_testPupInfoSectionViewRatingDetails(@Optional("PA") String state) {

        TestData tdAuto = getStateTestData(testDataManager.policy.get(PolicyType.AUTO_SS), "DataGather", "TestData");
        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");

        String otherActiveKeyPath = TestData.makeKeyPath(HomeSSMetaData.ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel());
        Map<String, String> policies = new HashMap<>();

        verifyAlgoDate();

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create Auto Policy
        PolicyType.AUTO_SS.get().createPolicy(tdAuto);
        policies.put("autoPolicy", PolicySummaryPage.getPolicyNumber());
        TestData tdOtherActiveAuto = getTestSpecificTD("OtherActiveAAAPolicies").adjust("ActiveUnderlyingPoliciesSearch|Policy number", policies.get("autoPolicy"));

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3.adjust(otherActiveKeyPath, tdOtherActiveAuto));
        policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());

        // Initiate PUP and verify  in rating details dialog
        initiateFillPup(getPupTD(policies));

        // Open rating details and verify
        PropertyQuoteTab.RatingDetailsViewPUP.open();
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier")).isNotEmpty();
        PropertyQuoteTab.RatingDetailsViewPUP.close();
        mainApp().close();
    }

    /**
     * @author Dominykas Razgunas
     * @name Test PUP policies that have an underlying DP3 policy with multiple units is rated properly for SS states.
     * @scenario
     * 1. Create customer
     * 2. Create HO3 policy without underlying Auto
     * 3. Initiate PUP Policy
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-10397")
    public void pas10397_testPupInfoSectionViewRatingDetailsNoAuto(@Optional("PA") String state) {

        TestData tdHO3 = getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3), "DataGather", "TestData");

        Map<String, String> policies = new HashMap<>();

        verifyAlgoDate();

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create HO3 Policy with underlying Auto policy
        PolicyType.HOME_SS_HO3.get().createPolicy(tdHO3);
        policies.put("ho3Policy", PolicySummaryPage.getPolicyNumber());

        // Initiate PUP and verify  in rating details dialog
        initiateFillPup(getPupTDNoAuto(policies));

        // Open rating details and verify
        PropertyQuoteTab.RatingDetailsViewPUP.open();
        assertThat(PropertyQuoteTab.RatingDetailsViewPUP.pupInformation.getValueByKey("Auto tier")).isNotEmpty();
        PropertyQuoteTab.RatingDetailsViewPUP.close();
        mainApp().close();
    }

    private TestData getPupTD(Map<String, String> policies) {
        TestData prefillTab = getTestSpecificTD("TestData_PrefillTab")
                .adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("ho3Policy"))
                .adjust("ActiveUnderlyingPolicies[1]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("autoPolicy"));
        return getPolicyTD().adjust(PrefillTab.class.getSimpleName(), prefillTab).mask(UnderlyingRisksAutoTab.class.getSimpleName());
    }

    private TestData getPupTDNoAuto(Map<String, String> policies) {
        TestData prefillTab = getTestSpecificTD("TestData_PrefillTab_NoAuto")
                .adjust("ActiveUnderlyingPolicies[0]|ActiveUnderlyingPoliciesSearch|Policy Number", policies.get("ho3Policy"));
        return getPolicyTD().adjust(PrefillTab.class.getSimpleName(), prefillTab).mask(UnderlyingRisksAutoTab.class.getSimpleName());
    }

    private void initiateFillPup(TestData tdPUP) {
        // Initiate PUP policy, fill, and calculate premium
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, UnderlyingRisksPropertyTab.class);
        NavigationPage.toViewTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES.get());
        NavigationPage.toViewSubTab(NavigationEnum.PersonalUmbrellaTab.PREMIUM_AND_COVERAGES_QUOTE.get());
        new PremiumAndCoveragesQuoteTab().calculatePremium();
    }

    private void verifyAlgoDate() {
        LocalDateTime algoEffectiveDate = LocalDateTime.of(2018, Month.JUNE, 1, 0, 0);
        if (TimeSetterUtil.getInstance().getCurrentTime().isBefore(algoEffectiveDate)) {
            TimeSetterUtil.getInstance().nextPhase(algoEffectiveDate);
        }
}
}
