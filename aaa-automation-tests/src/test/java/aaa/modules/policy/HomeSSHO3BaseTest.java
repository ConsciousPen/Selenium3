/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.GeneralTab;

public class HomeSSHO3BaseTest extends PolicyBaseTest {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

	protected TestData getBackDatedPolicyTD() {
		return getBackDatedPolicyTD(DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
	}

	protected TestData getBackDatedPolicyTD(String date) {
		String effDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), HomeSSMetaData.GeneralTab.EFFECTIVE_DATE.getLabel());
		String propertyDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), HomeSSMetaData.GeneralTab.PROPERTY_INSURANCE_BASE_DATE_WITH_CSAA_IG.getLabel());
		return getPolicyTD().adjust(effDateKey, date).adjust(propertyDateKey, date);
	}
}
