package aaa.modules.regression.conversions.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.conversions.template.ManualConversionTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

@StateList(states = {Constants.States.KS, Constants.States.KY, Constants.States.WV, Constants.States.AZ, Constants.States.WY,
		Constants.States.CT, Constants.States.UT, Constants.States.ID, Constants.States.OR, Constants.States.MT, Constants.States.SD})
public class ManualConversionTest extends ManualConversionTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.AUTO_SS)
	public void manualRenewalEntryToActivePolicy(@Optional("") String state) {
		manualRenewalEntryToActivePolicy();
	}
}