package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.INDeltaScenario1;
import toolkit.datax.TestData;

public class TestINDeltaScenario1 extends INDeltaScenario1 {
	public String scenarioPolicyType = "HO3-Heritage";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	public void TC01_createQuote() {		
		TestData td_sc1 = getTestSpecificTD("TestData");		
		super.TC01_createQuote(td_sc1, scenarioPolicyType);
	}

	@Test
	public void TC02_verifyEndorsements() {
		TestData td_add_Forms = getTestSpecificTD("TestData_add_Forms");
		super.TC02_verifyEndorsements(td_add_Forms);
	}
	
	@Test
	public void TC03_verifyHS2383() {
		TestData td_hs2383 = getTestSpecificTD("TestData_addHS2383"); 
		super.TC03_verifyHS2383(td_hs2383);
	}
	
	@Test
	public void TC04_verifyQuoteODD() {}
	
	
	@Test
	public void TC05_verifyHailResistiveRating() {
		super.TC05_verifyHailResistiveRating();
	}
	
	@Test
	public void TC06_verifyRoofTypeUneligible() {
		TestData td_sc1 = getTestSpecificTD("TestData");	
		super.TC06_verifyRoofTypeUneligible(td_sc1);
	}	
	
	@Test
	public void TC07_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");	
		super.TC07_purchasePolicy(td_sc1, scenarioPolicyType);		
	}
	
	@Test
	public void TC08_verifyPolicyODD() {}
	
}
