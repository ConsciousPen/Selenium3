package aaa.modules.conversion.manual;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestBindPupWithoutAuto extends PersonalUmbrellaBaseTest {

    private BindTab bindTab = policy.getDefaultView().getTab(BindTab.class);
    private ErrorTab errorTab = policy.getDefaultView().getTab(ErrorTab.class);
    private PurchaseTab purchaseTab = policy.getDefaultView().getTab(PurchaseTab.class);

    /**
     * @author Josh Carpenter
     * @name Test that a PUP without an active Auto Policy or Exclusion endorsement can be bound for NB
     * @scenario
     * 1. Create customer
     * 2. Create HO3 policy
     * 3. Initiate and attempt to bind PUP policy
     * 4. Verify error with code AAA_PUP_SS507440 exists and can be overridden
     * 5. Bind/purchase policy
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP)
    public void testBindPupWithoutAutoNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Create HO3 policy
        PolicyType.HOME_SS_HO3.get().createPolicy(getTdHome());

        TestData tdOtherActive = getTestSpecificTD("TestData_ActiveUnderlyingPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy Number"), PolicySummaryPage.getPolicyNumber());
        TestData tdPUP = getPolicyTD()
                .adjust(TestData.makeKeyPath("PrefillTab", PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel()), tdOtherActive)
                .adjust(TestData.makeKeyPath("UnderlyingRisksAutoTab", PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()), getTestSpecificTD("TestData_NoAuto"));

        // Initiate PUP policy
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        // Verify error is present and override
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_PUP_SS5071440);
        errorTab.overrideAllErrors();
        errorTab.override();

        // Finish binding policy
        policy.getDefaultView().fillFromTo(tdPUP, BindTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();

        assertThat(PolicySummaryPage.labelPolicyStatus.getWebElement().getText()
                .equals(ProductConstants.PolicyStatus.POLICY_ACTIVE));
    }

    /**
     * @author Josh Carpenter
     * @name Test that a PUP without an active Auto Policy or Exclusion endorsement can be bound during manual entry
     * @scenario
     * 1. Create customer
     * 2. Create HO3 policy
     * 3. Initiate Manual Renewal Entry for PUP policy
     * 4. Verify error with code AAA_PUP_SS507440 exists and can be overridden
     * 5. Bind/purchase policy
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP)
    public void testBindPupWithoutAutoConversion(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();



    }

    private TestData getTdHome() {
        return getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather"), "TestData_NJ")
                .adjust(TestData.makeKeyPath("ApplicantTab", HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()),
                        getTestSpecificTD("TestData_NoActivePolicies"));
    }
}
