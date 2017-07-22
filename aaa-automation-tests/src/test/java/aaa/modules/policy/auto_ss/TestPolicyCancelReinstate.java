package aaa.modules.policy.auto_ss;


import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.AutoSSBaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * @author amitjukovs
 * @name Test Cancellation flat and Reinstate Auto SS Policy
 * @scenario
 * 1. Create Customer
 * 2. Create Auto SS Policy
 * 3. Cancel policy
 * 4. Verify Policy status is 'Policy Cancelled'
 * 5. Reinstate policy
 * 6. Verify Policy status is 'Policy Active'
 * @details
 */
public class TestPolicyCancelReinstate extends AutoSSBaseTest {

    @Test
    @TestInfo(component = "Policy.AutoSS")
    public void testPolicyCancelReinstate() {
        mainApp().open();

        createCustomerIndividual();
        createPolicy();

        log.info("TEST: Cancellation Policy #" + PolicySummaryPage.labelPolicyNumber.getValue());
        
        policy.cancel().perform(tdPolicy.getTestData("Cancellation", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);

        policy.reinstate().perform(getStateTestData(tdPolicy, "Reinstatement", "TestData"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        
    }
}
