package aaa.modules.e2e.auto_ca;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.enums.DocGenEnum;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario2;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario2 extends Scenario2 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state"})
	@StateList(states = States.CA)
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
			generateTenthBill();
			renewalImageGeneration();
			payTenthBill();
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			verifyDocGenForms();
			removeAutoPay();
			renewalPaymentNotGenerated();
			updatePolicyStatus();
			makeManualPaymentInFullRenewalOfferAmount();
			makeManualPaymentInTotalDueAmount(); //PAS-24650
			verifyDocGenForms(false, DocGenEnum.Documents._55_5003, DocGenEnum.Documents._55_5080);
		});
	}
}
