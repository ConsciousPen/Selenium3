package aaa.modules.delta.home_ss.ho6;

import aaa.common.enums.Constants.States;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.INDeltaScenario1;
import aaa.utils.StateList;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.TestData;

public class TestINDeltaScenario1 extends INDeltaScenario1 {
	public String scenarioPolicyType = "HO6";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
	}
	
	@Parameters({"state"})
	@StateList(states = States.IN)
	@Test(groups = {Groups.DELTA, Groups.HIGH})
	public void IN_Delta_Scenario1(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData td = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());
		createQuote(td, scenarioPolicyType);

		SoftAssertions.assertSoftly(softly -> {
			verifyLOVsOfImmediatePriorCarrier();
			verifyEndorsementsTab();
			verifyEndorsementHS2383();
			//verifyQuoteODD();
			verifyHailResistanceRating();
			verifyIneligibleRoofType();
			purchasePolicy(td, scenarioPolicyType);
			verifyPolicyODD();
		});
	}	
}
