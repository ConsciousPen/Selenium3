package aaa.modules.e2e.auto_ca;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario5;
import toolkit.datax.TestData;

public class TestScenario5 extends Scenario5 {
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
		SoftAssertions.assertSoftly(softly -> {
			generateFirstBillOneDayBefore();
			generateFirstBill();
			payFirstBillOneDayBefore();
			payFirstBill();
			generateSecondBill();
			paySecondBill();
			declinePayments();
			generateCancellNoticeOneDayBefore();
			generateCancellNotice();
			verifyFormAH34XX();
			cancelPolicyOneDayBefore();
			cancelPolicy(installmentDueDates.get(2));
			verifyFormAH67XX();
			generateFirstEPBillOneDayBefore();
			generateFirstEPBill();
			generateSecondEPBill();
			generateThirdEPBill();
			generateEPWriteOffOneDayBefore();
			generateEPWriteOff();
			renewalImageGeneration();
			renewalPreviewGeneration();
			renewalOfferGeneration();
		});
	}
}
