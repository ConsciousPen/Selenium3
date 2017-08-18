package aaa.modules.delta.home_ss.dp3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.KSDeltaScenario1;
import toolkit.datax.TestData;

public class TestKSDeltaScenario1 extends KSDeltaScenario1 { 
public String scenarioPolicyType = "DP3";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}
	
	@Test
	public void TC01_createQuote() {		
		TestData td_sc1 = getTestSpecificTD("TestData");		
		super.TC01_createQuote(td_sc1, scenarioPolicyType);
	}

	@Test
	public void TC02_verifyLOVsOfImmediatePriorCarrier() {
		super.TC02_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Test
	public void TC03_verifyEndorsements() {
		TestData td_add_Forms = getTestSpecificTD("TestData_add_Forms");
		super.TC03_verifyEndorsements(td_add_Forms);
	}
	
	@Test
	public void TC04_verifyELC() {
		super.TC04_verifyELC();
	}
	
	@Test
	public void TC05_verifyHailResistiveRating() {
		super.TC05_verifyHailResistiveRating();
	}

	@Test
	public void TC06_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");
		super.TC06_purchasePolicy(td_sc1, scenarioPolicyType);
	}
}
