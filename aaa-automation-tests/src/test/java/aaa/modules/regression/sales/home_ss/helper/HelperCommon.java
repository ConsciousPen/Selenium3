package aaa.modules.regression.sales.home_ss.helper;

import aaa.common.pages.SearchPage;
import aaa.main.modules.policy.IPolicy;
import aaa.modules.policy.HomeSSHO3BaseTest;
import toolkit.datax.TestData;
import aaa.main.modules.policy.home_ss.defaulttabs.*;

public class HelperCommon extends HomeSSHO3BaseTest {

    // This creates a customer, policy and return the policy number as a String.
    public String createCustomerPolicyReturnPN(TestData in_customerData, TestData in_policyData)
    {
        createCustomerIndividual(in_customerData);
        return createPolicy(in_policyData);
    }

    // Retrieves an existing policy and initiates a generic endorsement.
    public void doSameDayEndorsement(String policyNumberToOpen, IPolicy in_policy, TestData endorsementTestData)
    {
        //Go to Created Policy.
        SearchPage.openPolicy(policyNumberToOpen);
        // Begin Endorsement. Use Default data for Endorsement Reason Page. Using Custom Data From Adjustment.
        in_policy.endorse().perform(endorsementTestData.adjust(getPolicyTD("Endorsement", "TestData")));
        in_policy.getDefaultView().fillUpTo(endorsementTestData, PurchaseTab.class, false);
    }
}