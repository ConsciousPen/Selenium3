package aaa.modules.e2e.home_ss.ho3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario2;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario2 extends Scenario2 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.CO, States.IN, States.KS, States.KY, States.MT, States.NJ, States.NY, 
			States.OK, States.OR, States.PA, States.SD, States.UT, States.VA})
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
			if (getState().equals(Constants.States.KY)) {
				renewalImageGeneration();
			}
			payNinthBill();
			if (getState().equals(Constants.States.MT) || getState().equals(Constants.States.NY)) {
				renewalImageGeneration();
				generateTenthBill();
			} else {
				generateTenthBill();
				if (getState().equals(Constants.States.KY)) {
					renewalPreviewGeneration(); // from 01.03
				}
			//	generateTenthBill();
				if (!getState().equals(Constants.States.KY)) {
					renewalImageGeneration();
				}
			}
			//if (getState().equals(Constants.States.KY)) {
			//	renewalPreviewGeneration(); // February fall into period from R-80 to R
			//}
			payTenthBill();
			if (!getState().equals(Constants.States.KY)) {
				renewalPreviewGeneration();
			}
			renewalOfferGeneration(softly);
			renewalPremiumNotice();
			verifyDocGenForms();
			removeAutoPay();
			renewalPaymentNotGenerated();
			updatePolicyStatus();
			makeManualPaymentInFullRenewalOfferAmount();
		});
	}

}
