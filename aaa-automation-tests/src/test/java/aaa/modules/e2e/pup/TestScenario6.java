package aaa.modules.e2e.pup;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.PersonalUmbrellaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario6;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario6 extends Scenario6 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.CA, States.NJ, States.OK, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), PersonalUmbrellaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			endorsePolicy();
			payFirstBill();
			generateCancellNotice();
			generateSecondBill();
			paySecondBill();
			generateThirdBill(softly);
			payThirdBill();
			setDoNotRenewFlag();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration();
			updatePolicyStatus();
		});
	}
}
