package aaa.modules.cft;

import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * Controlled Financial Testing Scenario 1
 * For any product and any defined state from params
 * NB W/O Emp Ben
 * Down pay_Cash
 * 1st installment
 * Cancel with future date
 * Earned Premium Write off
 */
public class TestCFTScenario1 extends ControlledFinancialBaseTest {

	@Test(groups = {Groups.CFT})
	@TestInfo(component = "CFT")
	@Parameters({"LOB"})
	public void testCFTScenario1(String lob) {

		mainApp().open();
		createCustomerIndividual();
		createQuote(PolicyType.getPolicy(lob));


	}



}
