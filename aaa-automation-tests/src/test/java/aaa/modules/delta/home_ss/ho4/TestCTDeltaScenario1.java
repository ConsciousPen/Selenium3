package aaa.modules.delta.home_ss.ho4;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CTDeltaScenario1;

public class TestCTDeltaScenario1 extends CTDeltaScenario1 {
	
	public String scenarioPolicyType = "HO4";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
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
	public void TC03_verifyWindstormMitigationDiscount(String state) {
		super.TC_verifyWindstormMitigationDiscount();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_verifyELC(String state) {
		super.TC_verifyELC();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_purchasePolicy(String state) {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC06_verifyODDPolicy(String state) {} 
	
	@Parameters({"state"})
	@Test
	public void TC07_verifyCancelNoticeTab(String state) {
		super.TC_verifyCancelNoticeTab();
	}
}
