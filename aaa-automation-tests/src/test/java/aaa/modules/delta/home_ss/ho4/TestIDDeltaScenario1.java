package aaa.modules.delta.home_ss.ho4;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.IDDeltaScenario1;
import toolkit.datax.TestData;

public class TestIDDeltaScenario1 extends IDDeltaScenario1 {
	
public String scenarioPolicyType = "HO4";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}
	
	@Test
	public void TC01_createQuote() {		
		TestData td_sc1 = getTestSpecificTD("TestData");		
		super.TC_createQuote(td_sc1, scenarioPolicyType);
	}
	
	@Test
	public void TC02_verifyLOVsOfImmediatePriorCarrier() {
		super.TC_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Test
	public void TC03_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");	
		super.TC_purchasePolicy(td_sc1, scenarioPolicyType);
	}

}
