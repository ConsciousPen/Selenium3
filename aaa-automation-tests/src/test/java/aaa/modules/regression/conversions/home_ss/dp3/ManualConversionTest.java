package aaa.modules.regression.conversions.home_ss.dp3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.conversions.template.ManualConversionTemplate;
import toolkit.utils.TestInfo;

public class ManualConversionTest  extends ManualConversionTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_DP3)
	public void manualRenewalEntryToActivePolicy(@Optional("") String state) {
		manualRenewalEntryToActivePolicy();
	}
}