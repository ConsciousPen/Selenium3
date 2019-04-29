package aaa.modules.regression.conversions.home_ss.ho3;

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

@StateList(statesExcept = Constants.States.CA)
public class ManualConversionTest extends ManualConversionTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO3)
	public void manualRenewalEntryToActivePolicy(@Optional("") String state) {
		manualRenewalEntryToActivePolicy();
	}
}