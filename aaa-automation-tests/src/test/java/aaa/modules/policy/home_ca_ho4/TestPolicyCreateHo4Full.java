package aaa.modules.policy.home_ca_ho4;

import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO4BaseTest;

/**
 * @author Alexander Tinkovan
 * @name Test Create Home California Policy with HO4 Full
 * @scenario
 * 1. Create new or open existent Customer;
 * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO4;
 * 3. Fill all mandatory fields;
 * 4. Add Endorsement form
 * 5. Calculate premium;
 * 6. Issue policy;
 * 7. Check Policy status is Active.
 */
public class TestPolicyCreateHo4Full extends HomeCaHO4BaseTest {

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testPolicyCreateHo4Full() {
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getPolicyTD("DataGather", "TestData_Full").adjust(getPolicyTD("DataGather", "TestData_AddForm_HO210")).resolveLinks());;
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
