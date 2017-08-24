package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CTDeltaScenario1;

public class TestCTDeltaScenario1 extends CTDeltaScenario1 {
	
	public String scenarioPolicyType = "HO3-Heritage";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
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
	public void TC03_verifyWindstormMitigationDiscount() {
		super.TC_verifyWindstormMitigationDiscount();
	}
	
	@Test
	public void TC04_verifyELC() {
		super.TC_verifyELC();
	}
	
	@Test
	public void TC05_purchasePolicy() {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Test
	public void TC06_verifyODDPolicy() {} 
	
	@Test
	public void TC07_verifyCancelNoticeTab() {
		super.TC_verifyCancelNoticeTab();
	}	
}
