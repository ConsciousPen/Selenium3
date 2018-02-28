package aaa.modules.e2e.home_ca.ho4;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario10;
import toolkit.datax.TestData;

public class TestScenario10 extends Scenario10 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO4;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		SoftAssertions.assertSoftly(softly -> {
			generateFirstBill();
			payFirstBill();
			generateSecondBill();
			paySecondBill();
			generateThirdBill();
			payThirdBill();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration();
			enableAutoPay();
			changePaymentPlanForCA();
			payRenewalOffer();
			updatePolicyStatus();
			generateFirstBillOfRenewal();
			payFirstBillOfRenewal();
		});
	}

}
