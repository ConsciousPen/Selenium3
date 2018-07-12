package aaa.modules.openl;

import aaa.helpers.openl.model.home_ca.ho4.HomeCaHO4OpenLPolicy;
import aaa.main.modules.policy.PolicyType;

public class HomeCaHO4PremiumCalculationTest extends HomeCaPremiumCalculationTest<HomeCaHO4OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO4;
	}
}
