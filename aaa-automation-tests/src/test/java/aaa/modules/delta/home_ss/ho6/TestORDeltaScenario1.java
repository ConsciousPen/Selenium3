package aaa.modules.delta.home_ss.ho6;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.ORDeltaScenario1;
import toolkit.datax.TestData;

public class TestORDeltaScenario1 extends ORDeltaScenario1 {

	public String scenarioPolicyType = "HO6";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
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
	public void TC03_verifyUnderwritingApprovalTab() {
		super.TC_verifyUnderwritingApprovalTab();
	}

	@Test
	public void TC04_verifyClaims() {
		super.TC_verifyClaims();
	}
	
	@Test
	public void TC05_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");	
		super.TC_purchasePolicy(td_sc1, scenarioPolicyType);
	}

}
