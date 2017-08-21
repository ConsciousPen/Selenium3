package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.OHDeltaScenario1;
import toolkit.datax.TestData;

public class TestOHDeltaScenario1 extends OHDeltaScenario1 {
	
public String scenarioPolicyType = "HO3-Heritage";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	public void TC01_createQuote() {		
		TestData td_sc1 = getTestSpecificTD("TestData");		
		super.TC_createQuote(td_sc1, scenarioPolicyType);
	}
	
	@Test
	public void TC02_verifyImmediatePriorCarrier() {
		super.TC_verifyImmediatePriorCarrier();
	}
	
	@Test
	public void TC03_verifyEndorsements() {
		TestData td_add_Forms = getTestSpecificTD("TestData_add_Forms");
		super.TC_verifyEndorsements(td_add_Forms);
	}
	
	@Test
	public void TC04_verifyHailResistanceRating() {
		super.TC_verifyHailResistanceRating();
	}
	
	@Test
	public void TC05_verifyRoofTypeUneligible() {
		TestData td_sc1 = getTestSpecificTD("TestData");	
		super.TC_verifyRoofTypeUneligible(td_sc1);
	}
	
	@Test
	public void TC06_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");	
		super.TC_purchasePolicy(td_sc1, scenarioPolicyType);
	}

}
