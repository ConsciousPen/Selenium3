package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.DCDeltaScenario1;

public class TestDCDeltaScenario1 extends DCDeltaScenario1 {
	
public String scenarioPolicyType = "HO3";
	
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
	public void TC03_purchasePolicy() {	
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Test
	public void TC04_verifyDeclarationDocumentsGenerated() {} 
	
	@Test
	public void TC05_verifyPolicyODD() {}

}
