package aaa.modules.e2e.home_ss.ho3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario12;
import toolkit.datax.TestData;

public class TestScenario12 extends Scenario12 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
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
			generateRefund();
						
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration(softly);
			generateRenewalBill();
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
			generateRenewalBill_FirstRenewal();
			payRenewalBill_FirstRenewal();
			updatePolicyStatus_FirstRenewal();
			generateFirstBillOfSecondRenewal(softly);
			payFirstBillOfSecondRenewal();
		});
	}
}
