package aaa.modules.delta.home_ss.ho4;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.INDeltaScenario1;
import toolkit.datax.TestData;

public class TestINDeltaScenario1 extends INDeltaScenario1{ 
public String scenarioPolicyType = "HO4";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO4;
	}
	
	@Test
	public void TC01_createQuote() {		
		TestData td_sc1 = getTestSpecificTD("TestData");		
		super.TC01_createQuote(td_sc1, scenarioPolicyType);
	}

	@Test
	public void TC02_verifyLOVsOfImmediatePriorCarrier() {
		super.TC02_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Test
	public void TC03_verifyEndorsements() {
		TestData td_add_Forms = getTestSpecificTD("TestData_add_Forms");
		super.TC03_verifyEndorsements(td_add_Forms);
	}
	
	@Test
	public void TC05_verifyQuoteODD() {}
	
	
	@Test
	public void TC06_verifyHailResistanceRating() {
		super.TC06_verifyHailResistanceRating();
	}
	
	@Test
	public void TC07_verifyRoofTypeUneligible() {
		TestData td_sc1 = getTestSpecificTD("TestData");	
		super.TC07_verifyRoofTypeUneligible(td_sc1);
	}	
	
	@Test
	public void TC08_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");	
		super.TC08_purchasePolicy(td_sc1, scenarioPolicyType);		
	}
	
	@Test
	public void TC09_verifyPolicyODD() {}
	
}
