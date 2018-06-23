package aaa.modules.regression.service.auto_ss.functional;

import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.DocumentsAndBindTab;
import aaa.modules.regression.sales.auto_ss.functional.TestEValueDiscount;

public class TestMiniServicesDummy extends aaa.modules.regression.service.helper.TestMiniServicesDummy {

	private final DocumentsAndBindTab documentsAndBindTab = new DocumentsAndBindTab();
	private final TestEValueDiscount testEValueDiscount = new TestEValueDiscount();

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}


}


