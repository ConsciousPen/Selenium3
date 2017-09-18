package aaa.modules.delta.home_ss.ho4;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.KSDeltaScenario1;

public class TestKSDeltaScenario1 extends KSDeltaScenario1 { 
public String scenarioPolicyType = "HO4";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
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
	public void TC03_verifyEndorsementsTab(@Optional("") String state) {
		super.TC_verifyEndorsementsTab();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_verifyELC(@Optional("") String state) {
		super.TC_verifyELC();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyHailResistanceRating(@Optional("") String state) {
		super.TC_verifyHailResistanceRating();
	}

	@Parameters({"state"})
	@Test
	public void TC06_purchasePolicy(@Optional("") String state) {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
}
