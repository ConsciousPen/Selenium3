package aaa.modules.regression.sales.home_ca.dp3.functional;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestEndorsementPremiumEntriesTemplate;

public class TestEndorsementPremiumEntries extends TestEndorsementPremiumEntriesTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_DP3;
    }

}
