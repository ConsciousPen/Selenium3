package aaa.modules.regression.sales.home_ss.ho4;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO4BaseTest;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * Created by lkazarnovskiy on 8/23/2017.
 */
public class TestPolicyCreationFull extends HomeSSHO4BaseTest {

	@Parameters({"state"})
	@StateList(statesExcept = { States.CA })
	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4)
	public void testPolicyCreationFull(@Optional("") String state) {
		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(getTestSpecificTD("TestDataFull"));

		assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Hss HO4 Full Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}
}
