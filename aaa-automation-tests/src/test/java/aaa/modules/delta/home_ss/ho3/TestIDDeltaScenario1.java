package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.IDDeltaScenario1;

public class TestIDDeltaScenario1 extends IDDeltaScenario1 {
	
	public String scenarioPolicyType = "HO3";
	
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
	public void TC02_verifyLOVsOfImmediatePriorCarrier(String state) {
		super.TC_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Parameters({"state"})
	@Test
	public void TC03_verifyErrorForZipCode83213(String state) {
		super.TC_verifyErrorForZipCode83213();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_purchasePolicy(String state) {	
		super.TC_purchasePolicy(scenarioPolicyType);
	}

}
