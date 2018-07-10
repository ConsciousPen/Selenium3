package aaa.modules.openl;

import aaa.main.modules.policy.PolicyType;

public class HomeSSHO3PremiumCalculationTest extends HomeSSPremiumCalculationTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
}
