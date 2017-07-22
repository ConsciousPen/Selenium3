package aaa.modules.policy.cea;

import org.testng.annotations.Test;
import aaa.main.enums.ProductConstants;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.CaliforniaEarthquakeBaseTest;
import toolkit.utils.TestInfo;

/**
 * @author Olga Reva
 * @name Test Create CEA Policy from CAH policy
 * @scenario
 * 1. Create new or open existed customer.
 * 2. Initiate new CA HO3 quote creation.
 * 3. Fill all mandatory fields on all tabs. 
 * 4. Calculate premium and choose CEA product.
 * 5. Bind and purchase policy.
 * 6. New CEA quote General tab is opened.
 * 7. Fill all mandatory fields for CEA quote. 
 * 8. Calculate premium for CEA quote. 
 * 9. Purchase CEA policy.
 * 10. Verify policy status is Bound on Consolidated Policy View.
 * @details
 */
public class TestPolicyCreationFromHO extends CaliforniaEarthquakeBaseTest {

	@Test
	@TestInfo(component = "Policy.PersonalLines.CEA")
	public void testPolicyCreationFromHO() {
		mainApp().open();

		createPolicyFromCa();

		PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.BOUND);

	}

}
