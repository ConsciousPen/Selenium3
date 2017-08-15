package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CTDeltaScenario1;
import toolkit.datax.TestData;

public class TestCTDeltaScenario1 extends CTDeltaScenario1 {
	
	public String scenarioPolicyType = "HO3-Heritage";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	public void TC01_createQuote() {		
		TestData td_sc1 = getTestSpecificTD("TestData");		
		super.TC01_createQuote(td_sc1, scenarioPolicyType);
	}

	@Test
	public void TC02_verifyWindstormMitigationDiscount() {
		super.TC02_verifyWindstormMitigationDiscount();
	}
	
	@Test
	public void TC03_verifyELC() {
		super.TC03_verifyELC();
	}
	
	@Test
	public void TC04_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");
		super.TC04_purchasePolicy(td_sc1, scenarioPolicyType);
	}
	
	@Test
	public void TC05_verifyODDPolicy() {} 
	
	@Test
	public void TC06_verifyCancelNoticeTab() {
		TestData td_plus33days = getTestSpecificTD("TestData_Plus33Days");
		TestData td_plus34days = getTestSpecificTD("TestData_Plus34Days");
		super.TC06_verifyCancelNoticeTab(td_plus33days, td_plus34days);
	}
	
}
