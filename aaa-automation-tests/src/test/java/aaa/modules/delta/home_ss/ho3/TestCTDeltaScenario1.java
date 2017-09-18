package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CTDeltaScenario1;

public class TestCTDeltaScenario1 extends CTDeltaScenario1 {
	
	public String scenarioPolicyType = "HO3-Heritage";
	
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
	public void TC02_verifyLOVsOfImmediatePriorCarrier(@Optional("") String state) {
		super.TC_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Parameters({"state"})
	@Test
	public void TC03_verifyWindstormMitigationDiscount(@Optional("") String state) {
		super.TC_verifyWindstormMitigationDiscount();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_verifyELC(@Optional("") String state) {
		super.TC_verifyELC();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_purchasePolicy(@Optional("") String state) {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC06_verifyODDPolicy(@Optional("") String state) {} 
	
	@Parameters({"state"})
	@Test
	public void TC07_verifyCancelNoticeTab(@Optional("") String state) {
		super.TC_verifyCancelNoticeTab();
	}	
}
