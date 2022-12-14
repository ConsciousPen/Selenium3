package aaa.modules.e2e.home_ss.ho3;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants.States;
import aaa.main.metadata.policy.AutoSSMetaData;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ss.actiontabs.EndorsementActionTab;
import aaa.modules.e2e.templates.Scenario13;
import aaa.utils.StateList;
import toolkit.datax.TestData;

public class TestScenario13 extends Scenario13 {
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state"})
	@StateList(states = {States.AZ, States.UT})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		endorsementReasonDataKeys = new String[] {new EndorsementActionTab().getMetaKey(), AutoSSMetaData.EndorsementActionTab.ENDORSEMENT_REASON.getLabel()};
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill(); 
			deletePendingEndorsement();
			generateSecondBill(softly);
			paySecondBill();
			generateThirdBill(softly);
			payThirdBill();
			generateFourthBill(softly);
			payFourthBill(); 
			generateFifthBill(softly);
			removeAutoPay();
			payFifthBill();
			changePaymentPlan(softly);
			generateSixthBill(softly);
			paySixthBill();
			smallBalanceGeneration(softly);
			cancelNoticeNotGenerated();  
			//cancellationNotGenerated(); 
			renewalImageGeneration(); 
			renewalPreviewGeneration(); 
			renewalOfferGeneration(softly);
			generateRenewalBill(); 
			createRenewalVersion();
			payRenewalBill(); 
			updatePolicyStatus();			
		});
	}

}
