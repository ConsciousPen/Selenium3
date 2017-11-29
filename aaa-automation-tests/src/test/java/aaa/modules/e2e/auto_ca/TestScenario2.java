package aaa.modules.e2e.auto_ca;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario2;
import toolkit.datax.TestData;

public class TestScenario2 extends Scenario2 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
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
			billingOnHold();
			billNotGenerated();
			generateSecondBill();
			paySecondBill();
			generateThirdBill();
			payThirdBill();
			generateFourthBill();
			payFourthBill();
			generateFifthBill();
			payFifthBill();
			generateSixthBill();
			paySixthBill();
			generateSeventhBill();
			paySeventhBill();
			generateEighthBill();
			payEighthBill();
			generateNinthBill();
			payNinthBill();
			generateTenthBill();
			renewalImageGeneration();
			payTenthBill();
			renewalPreviewGeneration();
			renewalOfferGeneration();
			verifyDocGenForms();
			removeAutoPay();
			renewalPaymentNotGenerated();
			updatePolicyStatus();
			makeManualPaymentInFullRenewalOfferAmount();
		});
	}
}
