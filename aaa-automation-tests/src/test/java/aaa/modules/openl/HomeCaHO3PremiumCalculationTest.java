package aaa.modules.openl;

import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.main.modules.policy.PolicyType;

public class HomeCaHO3PremiumCalculationTest extends HomeCaPremiumCalculationTest<HomeCaHO3OpenLPolicy> {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
}
