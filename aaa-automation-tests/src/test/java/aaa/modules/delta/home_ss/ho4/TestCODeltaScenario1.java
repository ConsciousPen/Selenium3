package aaa.modules.delta.home_ss.ho4;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CODeltaScenario1;

public class TestCODeltaScenario1 extends CODeltaScenario1{ 
	
	public String scenarioPolicyType = "HO4";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
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
	public void TC03_verifyEndorsements(String state) {
		super.TC_verifyEndorsementsTab();
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_verifyQuoteODD(String state) {
		super.TC_verifyQuoteODD();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyAdverselyImpacted(String state) {
		super.TC_verifyAdverselyImpacted();
	}
	
	@Parameters({"state"})
	@Test
	public void TC06_verifyIneligibleRoofType(String state) {
		super.TC_verifyIneligibleRoofType();
	}
	
	@Parameters({"state"})
	@Test
	public void TC07_purchasePolicy(String state) {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC08_verifyPolicyODD(String state) {
		super.TC_verifyPolicyODD();
	}

		
}
