package aaa.modules.conversion.manual.pup;


import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ErrorEnum;
import aaa.main.enums.ProductConstants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.modules.policy.pup.defaulttabs.BindTab;
import aaa.main.modules.policy.pup.defaulttabs.ErrorTab;
import aaa.main.modules.policy.pup.defaulttabs.PurchaseTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.conversion.manual.ConvPUPBaseTest;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import static toolkit.verification.CustomAssertions.assertThat;

public class TestBindPupWithoutAuto extends ConvPUPBaseTest {

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
     * 6. Verify the policy is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6957")
    public void pas6957TestBindPupWithoutAutoNB(@Optional("NJ") String state) {

        // Create customer
        mainApp().open();
        createCustomerIndividual();

        // Create HO3 policy
        PolicyType.HOME_SS_HO3.get().createPolicy(getTdHome());

        // Create Test Data
        TestData tdOtherActive = getTestSpecificTD("TestData_ActiveUnderlyingPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy Number"), PolicySummaryPage.getPolicyNumber());
        TestData tdPUP = getPolicyTD()
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(), PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel()), tdOtherActive)
                .adjust(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(), PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()),
                        getTestSpecificTD("TestData_NoAuto"));

        // Initiate PUP policy
        policy.initiate();
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        verifyOverrideErrorAndBind(tdPUP);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
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
     * 6. Verify the policy is active
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.MEDIUM})
    @TestInfo(component = ComponentConstant.Sales.PUP, testCaseId = "PAS-6957")
    public void pas6957TestBindPupWithoutAutoConversion(@Optional("NJ") String state) {

        // Create Customer
        mainApp().open();
        createCustomerIndividual();

        // Create HO3 policy
        PolicyType.HOME_SS_HO3.get().createPolicy(getTdHome());

        // Create Test Data
        TestData tdOtherActive = getTestSpecificTD("TestData_ActiveUnderlyingPolicies")
                .adjust(TestData.makeKeyPath("ActiveUnderlyingPoliciesSearch", "Policy Number"), PolicySummaryPage.getPolicyNumber());
        TestData tdPUP = getPupConversionTdNoPolicyCreation()
                .adjust(TestData.makeKeyPath(PrefillTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.PrefillTab.ACTIVE_UNDERLYING_POLICIES.getLabel()), tdOtherActive)
                .mask(TestData.makeKeyPath(UnderlyingRisksAutoTab.class.getSimpleName(),
                        PersonalUmbrellaMetaData.UnderlyingRisksAutoTab.AUTOMOBILES.getLabel()));

        // Initiate manual renewal entry for PUP
        customer.initiateRenewalEntry().perform(getManualConversionInitiationTd35());
        policy.getDefaultView().fillUpTo(tdPUP, BindTab.class, true);
        bindTab.submitTab();

        verifyOverrideErrorAndBind(tdPUP);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }

    /**
     * @return Test Data for an HO3 policy with no other active policies
     */
    private TestData getTdHome() {
        return getStateTestData(testDataManager.policy.get(PolicyType.HOME_SS_HO3).getTestData("DataGather"), "TestData_NJ")
                .mask(TestData.makeKeyPath(ApplicantTab.class.getSimpleName(), HomeSSMetaData.ApplicantTab.OTHER_ACTIVE_AAA_POLICIES.getLabel()));
    }

    /**
     * Verifies the expected error message, overrides, and finishes binding the policy
     * @param td Test Data to be used to finish binding the PUP policy
     */
    private void verifyOverrideErrorAndBind(TestData td) {
        errorTab.verify.errorsPresent(ErrorEnum.Errors.ERROR_AAA_PUP_SS5071440);
        errorTab.overrideAllErrors();
        errorTab.override();
        policy.getDefaultView().fillFromTo(td, BindTab.class, PurchaseTab.class, true);
        purchaseTab.submitTab();
    }
}
