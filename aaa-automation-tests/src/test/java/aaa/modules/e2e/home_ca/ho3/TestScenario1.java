package aaa.modules.e2e.home_ca.ho3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario1;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario1 extends Scenario1 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state"})
	@StateList(states = States.CA)
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), HomeCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
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
			payRenewalBill();
			updatePolicyStatus();
		});
	}
}
