package aaa.modules.delta.home_ss.ho6;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.ORDeltaScenario1;

public class TestORDeltaScenario1 extends ORDeltaScenario1 {

	public String scenarioPolicyType = "HO6";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
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
	public void TC03_verifyUnderwritingApprovalTab(String state) {
		super.TC_verifyUnderwritingApprovalTab();
	}

	@Parameters({"state"})
	@Test
	public void TC04_verifyClaims(String state) {
		super.TC_verifyClaims();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_purchasePolicy(String state) {	
		super.TC_purchasePolicy(scenarioPolicyType);
	}

}
