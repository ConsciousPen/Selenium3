package aaa.modules.e2e.auto_ss;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario5;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario5 extends Scenario5 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.CO, States.IN, States.KS, States.KY, States.NJ, States.NY, 
			States.OH, States.OR, States.PA, States.SD, States.UT, States.VA})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBillOneDayBefore();
			generateFirstBill(softly);
			payFirstBillOneDayBefore();
			payFirstBill();
			generateSecondBill(softly);
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
