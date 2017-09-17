package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.INDeltaScenario1;

public class TestINDeltaScenario1 extends INDeltaScenario1 {
	public String scenarioPolicyType = "HO3-Heritage";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createQuote(String state) {				
		super.TC_createQuote(scenarioPolicyType);
	}

	@Parameters({"state"})
	@Test
	public void TC02_verifyLOVsOfImmediatePriorCarrier(String state) {
		super.TC_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Parameters({"state"})
	@Test
	public void TC03_verifyEndorsementsTab(String state) {
		super.TC_verifyEndorsementsTab();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_verifyEndorsementHS2383(String state) {
		super.TC_verifyEndorsementHS2383();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyQuoteODD(String state) {}
	
	@Parameters({"state"})
	@Test
	public void TC06_verifyHailResistanceRating(String state) {
		super.TC_verifyHailResistanceRating();
	}
	
	@Parameters({"state"})
	@Test
	public void TC07_verifyIneligibleRoofType(String state) {	
		super.TC_verifyIneligibleRoofType();
	}	
	
	@Parameters({"state"})
	@Test
	public void TC08_purchasePolicy(String state) {	
		super.TC_purchasePolicy(scenarioPolicyType);		
	}
	
	@Parameters({"state"})
	@Test
	public void TC09_verifyPolicyODD(String state) {}
	
}
