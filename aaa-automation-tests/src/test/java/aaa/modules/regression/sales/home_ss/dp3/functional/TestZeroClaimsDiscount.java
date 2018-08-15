package aaa.modules.regression.sales.home_ss.dp3.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestZeroClaimsDiscountTemplate;
import toolkit.utils.TestInfo;

public class TestZeroClaimsDiscount extends TestZeroClaimsDiscountTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}

	/**
	 * @author Josh Carpenter
	 * @name Test that the system is not applying the Zero Claims Discount when a renewal image has a claim that is included in rating
	 * @scenario
	 * 1. Create customer
	 * 2. Initiate SS DP3 quote
	 * 3. Fill up to P & C tab with default data (No claims present on Property Info Tab)
	 * 4. Validate the zero claims discount is present and capture the premium amount
	 * 5. Navigate to Property Info tab and add a claim:
	 * 		a.
	 * 		b.
	 * 		c.
	 * 6. Navigate to P & C tab, calculate premium
	 * 7. Validate no zero claims discount is present
	 * 8. Validate the premium has increased
	 * @details
	 **/
	@Parameters({"state"})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH}, description = "Test Zero Claims Discount for SS DP3")
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-9088")
	public void pas9088_testZeroClaimsDiscountDP3Quote(@Optional("") String state) {
		pas9088_testZeroClaimsDiscountQuote();
	}
}
