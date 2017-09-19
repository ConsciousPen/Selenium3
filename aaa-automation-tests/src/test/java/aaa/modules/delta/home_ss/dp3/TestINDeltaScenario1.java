package aaa.modules.delta.home_ss.dp3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.INDeltaScenario1;

public class TestINDeltaScenario1 extends INDeltaScenario1{ 
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
	public void TC03_verifyEndorsementsTab(@Optional("") String state) {
		super.TC_verifyEndorsementsTab();
	}

	/*
	@Parameters({"state"})
	@Test
	public void TC04_verifyEndorsementHS2383(@Optional("") String state) {
		super.TC_verifyEndorsementHS2383();
	}
	*/
	@Parameters({"state"})
	@Test
	public void TC04_verifyQuoteODD(@Optional("") String state) {}
	
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyHailResistanceRating(@Optional("") String state) {
		super.TC_verifyHailResistanceRating();
	}
	
	@Parameters({"state"})
	@Test
	public void TC06_verifyIneligibleRoofType(@Optional("") String state) {	
		super.TC_verifyIneligibleRoofType();
	}	
	
	@Parameters({"state"})
	@Test
	public void TC07_purchasePolicy(@Optional("") String state) {
		super.TC_purchasePolicy(scenarioPolicyType);		
	}
	
	@Parameters({"state"})
	@Test
	public void TC08_verifyPolicyODD(@Optional("") String state) {}
	
}
