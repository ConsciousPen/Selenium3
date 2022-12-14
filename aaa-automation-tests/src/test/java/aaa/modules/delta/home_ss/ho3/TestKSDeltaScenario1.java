package aaa.modules.delta.home_ss.ho3;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.KSDeltaScenario1;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestKSDeltaScenario1 extends KSDeltaScenario1 { 
	public String scenarioPolicyType = "HO3-Heritage";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Parameters({"state"})
	@StateList(states = States.KS)
	@Test(groups = {Groups.DELTA, Groups.HIGH})
	public void KS_Delta_Scenario1(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData td = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		createQuote(td, scenarioPolicyType);

		SoftAssertions.assertSoftly(softly -> {
			verifyLOVsOfImmediatePriorCarrier();
			verifyEndorsementsTab();
			verifyELC();
			verifyHailResistanceRating();
			purchasePolicy(td, scenarioPolicyType);
			verifyODDPolicy();

		});
	}
}
