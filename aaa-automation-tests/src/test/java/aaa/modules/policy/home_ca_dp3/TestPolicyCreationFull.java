package aaa.modules.policy.home_ca_dp3;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaDP3BaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * Created by lkazarnovskiy on 8/7/2017.
 * @name TestPolicyCreation CA DP3 Full
 * @scenario:
 * 1. Create new or open existent Customer;
 * 2. Initiate CA DP3 quote creation, set effective date to today, set Policy Form=HO6;
 * 3. Fill all mandatory fields;
 * 4. Add Endorsement form
 * 5. Calculate premium;
 * 6. Issue policy;
 * 7. Check Policy status is Active.
 */
public class TestPolicyCreationFull extends HomeCaDP3BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteCreation() {
        mainApp().open();

        createCustomerIndividual();
        createPolicy(getPolicyTD("DataGather", "TestDataFull"));

//       TODO Add values in YAML for FULL test for tabs after Rating (Mortgagee, Underwriting&Approval, Documents, Bind)

//        CustomAssert.assertTrue("NOT COMPLETED TEST: Add values in YAML for FULL test for tabs after Rating (Mortgagee, Underwriting&Approval, Documents, Bind)", false);

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
