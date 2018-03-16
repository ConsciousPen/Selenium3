package aaa.modules.e2e.auto_ca;

import toolkit.verification.CustomSoftAssertions;
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
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill();
			billingOnHold();
			billNotGenerated();
			generateSecondBill();
			paySecondBill(softly);
			generateThirdBill(softly);
			payThirdBill();
			generateFourthBill(softly);
			payFourthBill();
			generateFifthBill(softly);
			payFifthBill();
			generateSixthBill(softly);
			paySixthBill();
			generateSeventhBill(softly);
			paySeventhBill();
			generateEighthBill(softly);
			payEighthBill();
			generateNinthBill(softly);
			payNinthBill();
			renewalImageGeneration(); // from 01.03 (after February)
			generateTenthBill();
			//renewalImageGeneration(); // February fall into period from R-81 to R
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
