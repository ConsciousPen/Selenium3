package aaa.modules.openl;

import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ca.dp3.HomeCaDP3OpenLPolicy;
import aaa.main.modules.policy.PolicyType;

public class HomeCaDP3PremiumCalculationTest extends HomeCaPremiumCalculationTest<HomeCaDP3OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_DP3;
	}
	
	@Parameters({"state"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, ITestContext context) {
		verifyPremiums(context);
	}
}
