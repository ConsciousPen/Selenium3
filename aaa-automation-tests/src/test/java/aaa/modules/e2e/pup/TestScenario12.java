package aaa.modules.e2e.pup;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario12;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario12 extends Scenario12 {
	
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
			//payFirstBill();
			generateCancelNotice();
			generateCancellation();
			createRemittanceFile();
			payCancellationNoticeByRemittance();
			policyReinstatement();			
			if (isRefundAfterImageGeneration()) {
				renewalImageGeneration();
				generateRefund();
			}
			else {
				generateRefund();	
				renewalImageGeneration();
			}
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			if (!getState().equals(Constants.States.CA)) {
				generateRenewalBill();
			}
			changePaymentPlan();
			enableAutoPay();
			payRenewalBill();
			updatePolicyStatus();
			generateFirstBillOfFirstRenewal(softly);
			payFirstBillOfFirstRenewal();
			generateSecondBillOfFirstRenewal(softly);
			paySecondBillOfFirstRenewal();
			generateThirdBillOfFirstRenewal(softly);
			payThirdBillOfFirstRenewal();
			renewalImageGeneration_FirstRenewal();
			renewalPreviewGeneration_FirstRenewal();
			renewalOfferGeneration_FirstRenewal(softly);
			changePaymentPlan_FirstRenewal();
			if (!getState().equals(Constants.States.CA)) {
				generateRenewalBill_FirstRenewal();
			}
			payRenewalBill_FirstRenewal();
			updatePolicyStatus_FirstRenewal();
			generateFirstBillOfSecondRenewal(softly);
			payFirstBillOfSecondRenewal();
		});
	}
}
