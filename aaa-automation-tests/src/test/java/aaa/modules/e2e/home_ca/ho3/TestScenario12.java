package aaa.modules.e2e.home_ca.ho3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario12;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario12 extends Scenario12 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
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
			changePaymentPlanForCA();
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
			changePaymentPlanForCA_FirstRenewal();
			payRenewalBill_FirstRenewal();
			updatePolicyStatus_FirstRenewal();
			generateFirstBillOfSecondRenewal(softly);
			payFirstBillOfSecondRenewal();
		});
	}

}
