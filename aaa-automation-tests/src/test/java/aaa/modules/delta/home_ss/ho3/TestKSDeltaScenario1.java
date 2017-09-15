package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.KSDeltaScenario1;

public class TestKSDeltaScenario1 extends KSDeltaScenario1 { 
	public String scenarioPolicyType = "HO3-Heritage";
	
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
	public void TC03_verifyEndorsementsTab(String state) {
		super.TC_verifyEndorsementsTab();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_verifyELC(String state) {
		super.TC_verifyELC();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyHailResistanceRating(String state) {
		super.TC_verifyHailResistanceRating();
	}

	@Parameters({"state"})
	@Test
	public void TC06_purchasePolicy(String state) {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
}
