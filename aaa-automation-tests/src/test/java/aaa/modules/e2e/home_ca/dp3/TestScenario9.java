package aaa.modules.e2e.home_ca.dp3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario9;
import toolkit.datax.TestData;

public class TestScenario9 extends Scenario9 {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_DP3;
	}

	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("CA") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), HomeCaMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
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
			if (isBillGenDateAfterRenewImageGenDate()) {
				renewalImageGeneration();
				generateLastBill(softly);
			}
			else {
				generateLastBill(softly);
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
			dontPayRenewalBill();
			updatePolicyStatus();
			customerDeclineRenewal();
			generateEarnedPremiumWriteOff();
		});
	}
}
