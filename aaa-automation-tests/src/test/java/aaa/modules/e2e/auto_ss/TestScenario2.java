package aaa.modules.e2e.auto_ss;

import org.assertj.core.api.SoftAssertions;
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
