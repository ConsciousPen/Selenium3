package aaa.modules.regression.sales.home_ss.ho4;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO4BaseTest;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * Created by lkazarnovskiy on 8/23/2017.
 */
public class TestPolicyCreationFull extends HomeSSHO4BaseTest {

	@Test(groups= {Groups.REGRESSION, Groups.HIGH})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO4)
	public void testQuoteCreation() {
		mainApp().open();
		createCustomerIndividual();
		policy.createPolicy(getTestSpecificTD("TestDataFull"));

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_ACTIVE);
		log.info("TEST: Hss HO4 Full Policy created with #" + PolicySummaryPage.labelPolicyNumber.getValue());
	}
}
