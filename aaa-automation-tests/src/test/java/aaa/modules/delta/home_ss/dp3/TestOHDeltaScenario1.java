package aaa.modules.delta.home_ss.dp3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.OHDeltaScenario1;

public class TestOHDeltaScenario1 extends OHDeltaScenario1 {
	
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
	public void TC02_verifyImmediatePriorCarrier(@Optional("") String state) {
		super.TC_verifyImmediatePriorCarrier();
	}
	
	@Parameters({"state"})
	@Test
	public void TC03_verifyEndorsementsTab(@Optional("") String state) {
		super.TC_verifyEndorsementsTab();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_verifyHailResistanceRating(@Optional("") String state) {
		super.TC_verifyHailResistanceRating();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyIneligibleRoofType(@Optional("") String state) {	
		super.TC_verifyIneligibleRoofType();
	}
	
	@Parameters({"state"})
	@Test
	public void TC06_purchasePolicy(@Optional("") String state) {	
		super.TC_purchasePolicy(scenarioPolicyType);
	}

}
