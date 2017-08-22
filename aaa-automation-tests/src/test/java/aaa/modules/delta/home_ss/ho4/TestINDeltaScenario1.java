package aaa.modules.delta.home_ss.ho4;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.INDeltaScenario1;

public class TestINDeltaScenario1 extends INDeltaScenario1{ 
public String scenarioPolicyType = "HO4";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}
	
	@Test
	public void TC01_createQuote() {				
		super.TC_createQuote(scenarioPolicyType);
	}

	@Test
	public void TC02_verifyLOVsOfImmediatePriorCarrier() {
		super.TC_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Test
	public void TC03_verifyEndorsementsTab() {
		super.TC_verifyEndorsementsTab();
	}
	
	@Test
	public void TC04_verifyQuoteODD() {}
	
	
	@Test
	public void TC05_verifyHailResistanceRating() {
		super.TC_verifyHailResistanceRating();
	}
	
	@Test
	public void TC06_verifyIneligibleRoofType() {	
		super.TC_verifyIneligibleRoofType();
	}	
	
	@Test
	public void TC07_purchasePolicy() {
		super.TC_purchasePolicy(scenarioPolicyType);		
	}
	
	@Test
	public void TC08_verifyPolicyODD() {}
	
}
