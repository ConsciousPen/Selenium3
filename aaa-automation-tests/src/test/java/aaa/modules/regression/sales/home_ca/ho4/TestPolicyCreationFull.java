package aaa.modules.regression.sales.home_ca.ho4;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO4BaseTest;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Alexander Tinkovan
 * <b> Test Create Home California Policy with HO4 Full </b>
 * <p> Steps:
 * <p> 1. Create new or open existent Customer;
 * <p> 2. Initiate CAH quote creation, set effective date to today, set Policy Form=HO4;
 * <p> 3. Fill all mandatory fields;
 * <p> 4. Add Endorsement form
 * <p> 5. Calculate premium;
 * <p> 6. Issue policy;
 * <p> 7. Check Policy status is Active.
 */
public class TestPolicyCreationFull extends HomeCaHO4BaseTest {

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO4) 
    public void testPolicyCreationFull(@Optional("CA") String state) {
        mainApp().open();
        createCustomerIndividual();
        createPolicy(getPolicyTD("DataGather", "TestData_Full").adjust(getPolicyTD("DataGather", "TestData_AddForm_HO210")).resolveLinks());
	    assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
    }
}
