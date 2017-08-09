package aaa.modules.regression.sales.home_ss.ho6;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * Created by lkazarnovskiy on 8/7/2017.
 * * @name TestPolicyCreation SS HO6 Full
 * @scenario:
 * 1. Create new or open existent Customer;
 * 2. Initiate SS HO6 quote creation, set effective date to today, set Policy Form=HO6;
 * 3. Fill all mandatory fields;
 * 4. Add Endorsement form
 * 5. Calculate premium;
 * 6. Issue policy;
 * 7. Check Policy status is Active.
 */
public class TestPolicyCreationFull extends HomeSSHO6BaseTest {

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testQuoteCreation() {
        mainApp().open();
        createCustomerIndividual();
        policy.createPolicy(getPolicyTD("DataGather", "TestDataFull"));

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: HSS06 Full Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
    }
}