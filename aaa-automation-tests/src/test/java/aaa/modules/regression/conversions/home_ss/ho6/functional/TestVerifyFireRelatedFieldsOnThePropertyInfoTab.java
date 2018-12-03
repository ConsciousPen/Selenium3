package aaa.modules.regression.conversions.home_ss.ho6.functional;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.conversions.template.FireRelatedFieldsOnThePropertyInfoTabTemplate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestVerifyFireRelatedFieldsOnThePropertyInfoTab extends FireRelatedFieldsOnThePropertyInfoTabTemplate {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
	}

	/**
	 * @author R. Kazlauskiene
	 * @name Test Verify fire related fields on the Property info tab
	 * @scenario
	 * 1. Create Individual Customer / Account
	 * 2. Create converted SS home policy
	 * 3. Navigate to Property Information tab > Public protection class (PPC)
	 * 4. Fields should be editable and enabled:
	 * Fire department type
	 * Distance to fire hydrant
	 * Fire protection area
	 **/
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.NJ, Constants.States.AZ, Constants.States.PA, Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO6, testCaseId = "PAS-10703")
	public void testVerifyFireRelatedFieldsOnThePropertyInfoTab(@Optional("NJ") String state) {
		verifyFireRelatedFieldsOnThePropertyInfoTab();
	}

	/**
	 * @author R. Kazlauskiene
	 * @name Test Verify fire related fields on the Property info tab - Second Renewal
	 * @scenario
	 * 1. Create Individual Customer / Account
	 * 2. Create converted SS home policy
	 * 3. Initiate the Second Renewal
	 * 4. Navigate to Property Information tab > Public protection class (PPC)
	 * 5. Fields should be disabled:
	 * Fire department type
	 * Distance to fire hydrant
	 * Fire protection area
	 **/
	@Parameters({"state"})
	@StateList(states = {Constants.States.VA, Constants.States.DE, Constants.States.NJ, Constants.States.AZ, Constants.States.PA, Constants.States.MD})
	@Test(groups = {Groups.FUNCTIONAL, Groups.HIGH, Groups.TIMEPOINT})
	@TestInfo(component = ComponentConstant.Conversions.HOME_SS_HO6, testCaseId = "PAS-10703")
	public void testVerifyFireRelatedFieldsOnThePropertyInfoTabSecondRenewal(@Optional("NJ") String state) {
		verifyFireRelatedFieldsOnThePropertyInfoTabSecondRenewal();
	}

}
