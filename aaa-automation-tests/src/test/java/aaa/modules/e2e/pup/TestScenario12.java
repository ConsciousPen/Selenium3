package aaa.modules.e2e.pup;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario12;
import toolkit.datax.TestData;

public class TestScenario12 extends Scenario12 {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		SoftAssertions.assertSoftly(softly -> {
			generateFirstBill();
			//payFirstBill();
			generateCancelNotice();
			generateCancellation();
			createRemittanceFile();
			payCancellationNoticeByRemittance();
			policyReinstatement();
			generateRefund();
						
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration(); 
			if (!getState().equals(Constants.States.CA)) {
				generateRenewalBill();
			}
			changePaymentPlan();
			enableAutoPay();
			payRenewalBill();
			updatePolicyStatus();
			generateFirstBillOfFirstRenewal();
			payFirstBillOfFirstRenewal();
			generateSecondBillOfFirstRenewal();
			paySecondBillOfFirstRenewal();
			generateThirdBillOfFirstRenewal();
			payThirdBillOfFirstRenewal();
			renewalImageGeneration_FirstRenewal();
			renewalPreviewGeneration_FirstRenewal();
			renewalOfferGeneration_FirstRenewal();
			changePaymentPlan_FirstRenewal();
			if (!getState().equals(Constants.States.CA)) {
				generateRenewalBill_FirstRenewal();
			}
			payRenewalBill_FirstRenewal();
			updatePolicyStatus_FirstRenewal();
			generateFirstBillOfSecondRenewal();
			payFirstBillOfSecondRenewal();
		});
	}
}
