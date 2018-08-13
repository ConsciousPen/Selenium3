package aaa.modules.regression.sales.pup;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
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

    @Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.PUP)
    public void testPolicyCreation(@Optional("") String state) {

        mainApp().open();
        createCustomerIndividual();
        Assert.assertNotNull(getPolicyType(), "PolicyType is not set");
		createPolicy(
				policy.getDefaultView().getTab(PrefillTab.class).adjustWithRealPolicies(getTestSpecificTD("TestData"), 
						getPrimaryPoliciesForPup(getTestSpecificTD("TestData_UpdateCovE"), null)));
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
