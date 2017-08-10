/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import java.util.Map;

import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData.GeneralTab.PolicyInfo;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class PersonalUmbrellaBaseTest extends PolicyBaseTest {

	public PersonalUmbrellaBaseTest() {
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	protected TestData adjustWithRealPolicies(TestData td, Map<String, String> policies) {
		PrefillTab prefillTab = new PrefillTab();
		return prefillTab.adjustWithRealPolicies(td, policies);
	}
	
	@Override
	public TestData getBackDatedPolicyTD() {
		return getBackDatedPolicyTD(DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
	}

	@Override
	public TestData getBackDatedPolicyTD(String date) {
		String effDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), PersonalUmbrellaMetaData.GeneralTab.POLICY_INFO.getLabel(), PolicyInfo.EFFECTIVE_DATE.getLabel());
		return getPolicyTD().adjust(effDateKey, date);
	}
}
