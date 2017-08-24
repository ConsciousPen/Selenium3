package aaa.modules.delta.home_ss.dp3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.OHDeltaScenario1;

public class TestOHDeltaScenario1 extends OHDeltaScenario1 {
	
	public String scenarioPolicyType = "DP3";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}
	
	@Test
	public void TC01_createQuote() {				
		super.TC_createQuote(scenarioPolicyType);
	}
	
	@Test
	public void TC02_verifyImmediatePriorCarrier() {
		super.TC_verifyImmediatePriorCarrier();
	}
	
	@Test
	public void TC03_verifyEndorsementsTab() {
		super.TC_verifyEndorsementsTab();
	}
	
	@Test
	public void TC04_verifyHailResistanceRating() {
		super.TC_verifyHailResistanceRating();
	}
	
	@Test
	public void TC05_verifyIneligibleRoofType() {	
		super.TC_verifyIneligibleRoofType();
	}
	
	@Test
	public void TC06_purchasePolicy() {	
		super.TC_purchasePolicy(scenarioPolicyType);
	}

}
