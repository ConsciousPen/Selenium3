package aaa.modules.financials;

import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.AfterSuite;
import aaa.modules.policy.PolicyBaseTest;

public class FinancialsBaseTest extends PolicyBaseTest {

	protected static final List<String> POLICIES = new ArrayList<>();

	@AfterSuite
	public void testPolicyLogging() {
		for (String policy : POLICIES) {
			log.info(policy);
		}
	}

}
