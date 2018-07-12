package aaa.modules.openl;

import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLPolicy;
import aaa.main.modules.policy.PolicyType;

public class HomeCaHO6PremiumCalculationTest extends HomeCaPremiumCalculationTest<HomeCaHO6OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO6;
	}
}
