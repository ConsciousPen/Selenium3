package aaa.modules.e2e.auto_ss;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario9;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario9 extends Scenario9 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.NJ, States.NY, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill();
			generateSecondBill(softly);
			paySecondBill();
			payNextSevenInstallments();
			verifyThirdBillNotGenerated();
			verifyPaymentNotGenerated();
			renewalImageGeneration();
			generateLastBill(softly);
			if (getState().equals(Constants.States.NJ)) {
				renewalPreviewGeneration(); 
				payLastBill();
				removeAutoPay();
			} 
			else {
				payLastBill();
				removeAutoPay();
				renewalPreviewGeneration();
			}
			renewalOfferGeneration(softly);
			endorsementOnCurrentTerm();
			generateRenewalBill();
			dontPayRenewalBill();
			updatePolicyStatus();
			customerDeclineRenewal();
			generateEarnedPremiumWriteOff();
		});
	}

}
