package aaa.modules.e2e.auto_ss;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario9;
import toolkit.datax.TestData;

public class TestScenario9 extends Scenario9 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		SoftAssertions.assertSoftly(softly -> {
			generateFirstBill();
			payFirstBill();
			generateSecondBill();
			paySecondBill();
			payNextSevenInstallments();
			verifyThirdBillNotGenerated();
			verifyPaymentNotGenerated();
			renewalImageGeneration();
			generateLastBill(); 
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
			renewalOfferGeneration();
			endorsementOnCurrentTerm();
			generateRenewalBill();
			dontPayRenewalBill();
			updatePolicyStatus();
			customerDeclineRenewal();
			generateEarnedPremiumWriteOff();
		});
	}

}
