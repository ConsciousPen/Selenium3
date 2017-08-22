package aaa.modules.delta.home_ss.ho4;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CODeltaScenario1;

public class TestCODeltaScenario1 extends CODeltaScenario1{ 
	
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
	public void TC02_verifyEndorsements() {
		super.TC_verifyEndorsementsTab();
	}
	
	@Test
	public void TC03_verifyQuoteODD() {
		super.TC_verifyQuoteODD();
	}
	
	@Test
	public void TC04_verifyAdverselyImpacted() {
		super.TC_verifyAdverselyImpacted();
	}
	
	@Test
	public void TC05_verifyIneligibleRoofType() {
		super.TC_verifyIneligibleRoofType();
	}
	
	@Test
	public void TC06_purchasePolicy() {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Test
	public void TC07_verifyPolicyODD() {
		super.TC_verifyPolicyODD();
	}

		
}
