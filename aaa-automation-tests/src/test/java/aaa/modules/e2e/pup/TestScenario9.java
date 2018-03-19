package aaa.modules.e2e.pup;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario9;
import toolkit.datax.TestData;

public class TestScenario9 extends Scenario9 { 
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), HomeSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
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
			if (isBillGenDateAfterRenewImageGenDate()) {
				renewalImageGeneration();
				generateLastBill();
			}
			else {
				generateLastBill();
				renewalImageGeneration();
			}
			if (isLastPaymentDateAfterRenewPreviewGenDate()) {
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
			if (!getState().equals(Constants.States.CA)) {
				generateRenewalBill();
			}
			dontPayRenewalBill();
			updatePolicyStatus();
			customerDeclineRenewal();
			generateEarnedPremiumWriteOff();
		});
	}

}
