package aaa.modules.openl;

import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import toolkit.datax.TestData;

public class AutoCaSelectPremiumCalculationTest extends AutoCaPremiumCalculationTest<AutoCaSelectOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new AssignmentTab().getMetaKey());
	}
}
