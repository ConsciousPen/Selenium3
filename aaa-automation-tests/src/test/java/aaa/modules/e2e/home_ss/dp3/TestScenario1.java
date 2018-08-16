package aaa.modules.e2e.home_ss.dp3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario1;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario1 extends Scenario1 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			endorsePolicy();
			payFirstBill();
			generateSecondBill(softly);
			paySecondBill();
			generateThirdBill(softly);
			payThirdBill();
			earlyRenewNotGenerated();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			renewalPremiumNotice();
			payRenewalBill();
			updatePolicyStatus();
		});
	}
}
