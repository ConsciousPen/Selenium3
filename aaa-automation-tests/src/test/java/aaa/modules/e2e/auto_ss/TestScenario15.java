package aaa.modules.e2e.auto_ss;

import toolkit.verification.CustomSoftAssertions;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.common.enums.Constants;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.e2e.templates.Scenario15;
import toolkit.datax.TestData;

public class TestScenario15 extends Scenario15 { 
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createPolicy(@Optional("") String state) {
		tdPolicy = testDataManager.policy.get(getPolicyType());
		TestData policyCreationTD = getStateTestData(tdPolicy, "DataGather", "TestData").adjust(getTestSpecificTD("TestData").resolveLinks());

		createTestPolicy(policyCreationTD);
		
		CustomSoftAssertions.assertSoftly(softly -> {
			generateFirstBill(softly);
			payFirstBill(); 
			generateSecondBill(softly);
			paySecondBill(); 
			removeAutoPay(); 
			generateThirdBill(softly);
			renewalImageGeneration();
			if (getState().equals(Constants.States.NJ)) {
				renewalPreviewGeneration(); 
				generateCancelNotice(); 
				generateCancellation(); 
				renewalOfferNotGenerated();	
			}
			else if (getState().equals(Constants.States.KY)) {
				payThirdBill(); 
				generateCancelNotice();
				renewalPreviewGeneration(); 
				generateCancellation();
				renewalOfferNotGenerated();
			}
			else if (getState().equals(Constants.States.SD)) {
				payThirdBill(); 
				generateCancelNotice(); 
				renewalPreviewGeneration();  
				generateCancellation(); 
				renewalOfferNotGenerated();	
			}
			else {
				payThirdBill(); 
				generateCancelNotice(); 
				generateCancellation();
				renewalPreviewNotGenerated(); 
				renewalOfferNotGenerated();	
			}			
		});
		
	}

}
