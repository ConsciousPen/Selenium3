package aaa.modules.openl;

import aaa.main.modules.policy.PolicyType;

public class HomeSSHO4PremiumCalculationTest extends HomeSSPremiumCalculationTest {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}
}
