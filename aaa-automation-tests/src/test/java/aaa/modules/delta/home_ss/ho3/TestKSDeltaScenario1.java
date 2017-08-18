package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.KSDeltaScenario1;
import toolkit.datax.TestData;

public class TestKSDeltaScenario1 extends KSDeltaScenario1 { 
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
	public void TC02_verifyEndorsements() {
		TestData td_add_Forms = getTestSpecificTD("TestData_add_Forms");
		super.TC02_verifyEndorsements(td_add_Forms);
	}
	
	@Test
	public void TC03_verifyELC() {
		super.TC03_verifyELC();
	}
	
	@Test
	public void TC04_verifyHailResistiveRating() {
		super.TC04_verifyHailResistiveRating();
	}

	@Test
	public void TC05_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");
		super.TC05_purchasePolicy(td_sc1, scenarioPolicyType);
	}
}
