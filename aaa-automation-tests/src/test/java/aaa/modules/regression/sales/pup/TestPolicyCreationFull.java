package aaa.modules.regression.sales.pup;

import org.testng.Assert;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;


/**
 * @author Yonggang Sun
 * @name Test Create PUP Policy Full
 * @scenario
 * 1. Create new or open existent Customer;
 * 2. Create Umbrella Quote
 * 3. Issue policy;
 * 4. Check Policy status is Active.
 */
public class TestPolicyCreationFull extends PersonalUmbrellaBaseTest {

    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.PUP)
    public void testPolicyCreation() {

        mainApp().open();
        createCustomerIndividual();
        Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		createPolicy(
				policy.getDefaultView().getTab(PrefillTab.class).adjustWithRealPolicies(getTestSpecificTD("TestData"), 
						getPrimaryPoliciesForPup(getTestSpecificTD("TestData_UpdateCovE"), null)));
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}