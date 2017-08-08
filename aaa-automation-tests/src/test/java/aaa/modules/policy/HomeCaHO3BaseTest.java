/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.metadata.policy.HomeCaMetaData.GeneralTab.*;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;

public class HomeCaHO3BaseTest extends PolicyBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	protected TestData getBackDatedPolicyTD() {
		return getBackDatedPolicyTD(DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
	}

	protected TestData getBackDatedPolicyTD(String date) {
		String effDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), HomeCaMetaData.GeneralTab.POLICY_INFO.getLabel(), PolicyInfo.EFFECTIVE_DATE.getLabel());
		String baseDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), HomeCaMetaData.GeneralTab.CURRENT_CARRIER.getLabel(), CurrentCarrier.BASE_DATE_WITH_AAA.getLabel());
		return getPolicyTD("DataGather", "TestData").adjust(effDateKey, date).adjust(baseDateKey, date);
	}
}
