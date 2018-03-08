package aaa.modules.regression.sales.home_ss.ho6;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO6BaseTest;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
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

    @Parameters({"state"})
	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO6)
    public void testPolicyCreationFull(@Optional("") String state) {
        mainApp().open();
        createCustomerIndividual();
        policy.createPolicy(getTestSpecificTD("TestDataFull"));

        assertThat(PolicySummaryPage.labelPolicyStatus.getValue()).isEqualTo(ProductConstants.PolicyStatus.POLICY_ACTIVE);
        log.info("TEST: HSS06 Full Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
    }
}
