package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.KSDeltaScenario1;

public class TestKSDeltaScenario1 extends KSDeltaScenario1 { 
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
	public void TC03_verifyEndorsementsTab() {
		super.TC_verifyEndorsementsTab();
	}
	
	@Test
	public void TC04_verifyELC() {
		super.TC_verifyELC();
	}
	
	@Test
	public void TC05_verifyHailResistanceRating() {
		super.TC_verifyHailResistanceRating();
	}

	@Test
	public void TC06_purchasePolicy() {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
}
