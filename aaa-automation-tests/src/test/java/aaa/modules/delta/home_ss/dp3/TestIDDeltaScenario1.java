package aaa.modules.delta.home_ss.dp3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.IDDeltaScenario1;

public class TestIDDeltaScenario1 extends IDDeltaScenario1 {
	
public String scenarioPolicyType = "DP3";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_DP3;
	}
	
	@Parameters({"state"})
	@Test
	public void TC01_createQuote(@Optional("") String state) {		
		super.TC_createQuote(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC02_verifyLOVsOfImmediatePriorCarrier(@Optional("") String state) {
		super.TC_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Parameters({"state"})
	@Test
	public void TC03_verifyErrorForZipCode83213(@Optional("") String state) {
		super.TC_verifyErrorForZipCode83213();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_purchasePolicy(@Optional("") String state) {	
		super.TC_purchasePolicy(scenarioPolicyType);
	}

}
