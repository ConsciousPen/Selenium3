package aaa.modules.delta.home_ss.ho4;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.IDDeltaScenario1;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestIDDeltaScenario1 extends IDDeltaScenario1 {
	
	public String scenarioPolicyType = "HO4";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}
	
	@Parameters({"state"})
	@StateList(states = States.ID)
	@Test(groups = {Groups.DELTA, Groups.HIGH})
	public void ID_Delta_Scenario1(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData td = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		createQuote(td, scenarioPolicyType);

		SoftAssertions.assertSoftly(softly -> {
			verifyLOVsOfImmediatePriorCarrier();
			purchasePolicy(td, scenarioPolicyType);
			verifyODDPolicy();
		});
	}
}
