package aaa.modules.openl;

import aaa.helpers.openl.model.home_ca.dp3.HomeCaDP3OpenLPolicy;
import aaa.main.modules.policy.PolicyType;

public class HomeCaDP3PremiumCalculationTest extends HomeCaPremiumCalculationTest<HomeCaDP3OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_DP3;
	}
}
