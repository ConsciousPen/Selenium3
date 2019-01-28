/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package aaa.modules.policy;

import java.time.LocalDateTime;
import com.exigen.ipb.eisa.utils.TimeSetterUtil;
import aaa.helpers.jobs.JobUtils;
import aaa.helpers.jobs.Jobs;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.defaulttabs.GeneralTab;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoSSBaseTest extends PolicyBaseTest {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_SS;
    }

    @Override
	public TestData getBackDatedPolicyTD() {
		return getBackDatedPolicyTD(DateTimeUtils.getCurrentDateTime().minusDays(2).format(DateTimeUtils.MM_DD_YYYY));
	}

    @Override
    public TestData getBackDatedPolicyTD(String date) {
		String effDateKey = TestData.makeKeyPath(new GeneralTab().getMetaKey(), AutoSSMetaData.GeneralTab.POLICY_INFORMATION.getLabel(), AutoSSMetaData.GeneralTab.PolicyInformation.EFFECTIVE_DATE.getLabel());
		return getPolicyTD().adjust(effDateKey, date);
	}

	@Override
	protected void moveTimeAndRunRenewJobs(LocalDateTime nextPhaseDate) {
		mainApp().close();
        TimeSetterUtil.getInstance().nextPhase(nextPhaseDate);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart1);
        JobUtils.executeJob(Jobs.renewalOfferGenerationPart2);
    }
}
