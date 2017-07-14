package aaa.modules.policy.cea;

import org.testng.annotations.Test;

import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.CaliforniaEarthquakeBaseTest;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test Create CEA Policy
 * @scenario
 * 1. Create new or open existed customer.
 * 2. CAH policy should be created.
 * 3. Initiate new CEA quote creation.  
 * 4. Fill all mandatory section, use existed CAH policy for prefill.
 * 5. Calculate premium. 
 * 6. Issue policy.
 * 7. Verify policy status is Bound on Consolidated policy view.
 * @details
 */
public class TestPolicyCreation extends CaliforniaEarthquakeBaseTest {
	
	@Test
	@TestInfo(component = "Policy.PersonalLines.CEA")
	public void testPolicyCreation() {
		mainApp().open();
		
		TestData td = tdPolicy.getTestData("DataGather", "TestData");
		createPolicy(td);
		
		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.BOUND);
		
	}

}
