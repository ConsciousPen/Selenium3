package aaa.modules.e2e.pup;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario10;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario10 extends Scenario10 { 
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	@Parameters({"state"})
	@StateList(states = {States.CA, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill();
			generateSecondBill(softly);
			paySecondBill();
			generateThirdBill(softly);
			payThirdBill();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			if (!getState().equals(Constants.States.CA)) {
				generateRenewalBill();
			}
			enableAutoPay();
			if (getState().equals(Constants.States.CA)) {
				changePaymentPlanForCA(softly);
				payRenewalOffer();
			}
			else {
				changePaymentPlan();
				payRenewalBill();
			}
			updatePolicyStatus();
			generateFirstBillOfRenewal(softly);
			payFirstBillOfRenewal();
		});
	}

}
