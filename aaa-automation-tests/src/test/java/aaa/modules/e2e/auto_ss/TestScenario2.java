package aaa.modules.e2e.auto_ss;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario2;
import toolkit.datax.TestData;

public class TestScenario2 extends Scenario2 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
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
			renewalImageGeneration();
			payNinthBill();
			if (getState().equals(Constants.States.KY)) {
				renewalPreviewGeneration();
			}
			generateTenthBill();
			if (getState().equals(Constants.States.NJ) || getState().equals(Constants.States.PA) || getState().equals(Constants.States.SD)) {
				renewalPreviewGeneration();
			}
			payTenthBill();
			if (getState().equals(Constants.States.MD)) {
				renewalPreviewGeneration();
			}
			if (!getState().equals(Constants.States.KY) && !getState().equals(Constants.States.MD) && !getState().equals(Constants.States.NJ)
					&& !getState().equals(Constants.States.PA) && !getState().equals(Constants.States.SD)) {
				renewalPreviewGeneration();
			}
			renewalOfferGeneration();
			renewalPremiumNotice();
			verifyDocGenForms();
			removeAutoPay();
			renewalPaymentNotGenerated();
			updatePolicyStatus();
			makeManualPaymentInFullRenewalOfferAmount();
		});
	}
}
