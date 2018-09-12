package aaa.modules.regression.finance.billing;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.PolicyBaseTest;

public class FinanceBaseTest extends PolicyBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
}
