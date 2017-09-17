package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CODeltaScenario1;

public class TestCODeltaScenario3 extends CODeltaScenario1 { 
	
	public String scenarioPolicyType = "HO3-Prestige";
	
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
	public void TC02_verifyEndorsementsTab(String state) {
		super.TC_verifyEndorsementsTab();
	}
	
	@Parameters({"state"})
	@Test
	public void TC03_verifyQuoteODD(String state) {
		super.TC_verifyQuoteODD();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_purchasePolicy(String state) {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyPolicyODD(String state) {
		super.TC_verifyPolicyODD();
	}

}
