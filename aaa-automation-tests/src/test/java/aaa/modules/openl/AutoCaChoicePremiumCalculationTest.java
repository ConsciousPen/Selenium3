package aaa.modules.openl;

import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLPolicy;
import aaa.main.modules.policy.PolicyType;

public class AutoCaChoicePremiumCalculationTest extends AutoCaPremiumCalculationTest<AutoCaChoiceOpenLPolicy> {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}
}
