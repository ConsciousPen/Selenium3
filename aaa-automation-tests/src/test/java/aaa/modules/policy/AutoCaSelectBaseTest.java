/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import aaa.main.metadata.policy.AutoCaMetaData;
import aaa.main.metadata.policy.AutoCaMetaData.GeneralTab.PolicyInformation;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.GeneralTab;
import aaa.modules.policy.auto_ca_select.TestPolicyBackdated;

public class AutoCaSelectBaseTest extends PolicyBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	protected TestData getBackDatedPolicyTD() {
		return getBackDatedPolicyTD(DateTimeUtils.getCurrentDateTime().minusDays(10).format(DateTimeUtils.MM_DD_YYYY));
	}

	protected TestData getBackDatedPolicyTD(String date) {
		String effDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoCaMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), PolicyInformation.EFFECTIVE_DATE.getLabel());
		return getPolicyTD().adjust(effDateKey, date).adjust(getPolicyTD(TestPolicyBackdated.class.getSimpleName(), "TestData").resolveLinks());
	}
}
