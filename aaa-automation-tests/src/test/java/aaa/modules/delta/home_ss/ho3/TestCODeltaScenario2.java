package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CODeltaScenario1;

public class TestCODeltaScenario2 extends CODeltaScenario1 {
	
	public String scenarioPolicyType = "HO3-Legasy";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createQuote(@Optional("") String state) {				
		super.TC_createQuote(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC02_verifyEndorsementsTab(@Optional("") String state) {
		super.TC_verifyEndorsementsTab();
	}
	
	@Parameters({"state"})
	@Test
	public void TC03_verifyQuoteODD(@Optional("") String state) {
		super.TC_verifyQuoteODD();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_purchasePolicy(@Optional("") String state) {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyPolicyODD(@Optional("") String state) {
		super.TC_verifyPolicyODD();
	}

}
