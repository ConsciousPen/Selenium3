package aaa.modules.delta.home_ss.ho6;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CODeltaScenario1;

public class TestCODeltaScenario1 extends CODeltaScenario1 {
	
	public String scenarioPolicyType = "HO6";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
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
	public void TC04_verifyQuoteODD(@Optional("") String state) {
		super.TC_verifyQuoteODD();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyAdverselyImpacted(@Optional("") String state) {
		super.TC_verifyAdverselyImpacted();
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
	public void TC08_verifyPolicyODD(@Optional("") String state) {
		super.TC_verifyPolicyODD();
	}
	
}

