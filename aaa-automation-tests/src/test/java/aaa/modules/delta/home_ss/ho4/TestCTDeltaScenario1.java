package aaa.modules.delta.home_ss.ho4;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CTDeltaScenario1;

public class TestCTDeltaScenario1 extends CTDeltaScenario1 {
	
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
	public void TC02_verifyWindstormMitigationDiscount() {
		super.TC_verifyWindstormMitigationDiscount();
	}
	
	@Test
	public void TC03_verifyELC() {
		super.TC_verifyELC();
	}
	
	@Test
	public void TC04_purchasePolicy() {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Test
	public void TC05_verifyODDPolicy() {} 
	
	@Test
	public void TC06_verifyCancelNoticeTab() {
		super.TC_verifyCancelNoticeTab();
	}
}
