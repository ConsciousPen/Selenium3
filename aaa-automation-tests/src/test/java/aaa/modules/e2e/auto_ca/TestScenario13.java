package aaa.modules.e2e.auto_ca;

import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario13;
import toolkit.datax.TestData;

public class TestScenario13 extends Scenario13 {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
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
			deletePendingEndorsement();
			generateSecondBill();
			paySecondBill();
			generateThirdBill();
			payThirdBill();
			generateFourthBill(); 
			payFourthBill(); 
			generateFifthBill();
			removeAutoPay();
			payFifthBill();
			changePaymentPlan(); 
			generateSixthBill(); 
			paySixthBill();
			refundGeneration(); 
			cancelNoticeNotGenerated(); 
			//cancellationNotGenerated(); 
			renewalImageGeneration(); 
			renewalPreviewGeneration(); 
			renewalOfferGeneration();  
			/* method is not implemented for AutoCA
			createRenewalVersion();
			*/
			payRenewalBill(); 
			updatePolicyStatus();			
		});
	}
}
