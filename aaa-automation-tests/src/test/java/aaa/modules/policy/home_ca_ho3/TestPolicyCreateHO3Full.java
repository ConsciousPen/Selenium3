package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;

public class TestPolicyCreateHO3Full extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
     * @name Test Create CAH Policy HO3-Full
     * @scenario
     * 1. Create new or open existent Customer;
     * 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO3;
     * 3. Fill all mandatory fields;
     * 4. Add Endorsement form
     * 5. Calculate premium;
     * 6. Issue policy;
     * 7. Check Policy status is Active.
     */

    @Test
    @TestInfo(component = "Policy.PersonalLines")
    public void testPolicyCreation() {

        //CustomAssert.assertTrue("NOT COMPLETED TEST: add HO3_FULL test data from old project's file \"CA_HSS_Smoke.xls\"", false);

        mainApp().open();
        createCustomerIndividual();
        createPolicy(tdSpecific.getTestData("TestData").adjust(tdPolicy.getTestData("Endorsement", "TestData_AddForm_HO75")).resolveLinks());

        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        mainApp().close();
    }
}
