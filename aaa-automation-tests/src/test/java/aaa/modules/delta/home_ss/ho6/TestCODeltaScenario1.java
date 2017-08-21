package aaa.modules.delta.home_ss.ho6;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CODeltaScenario1;
import toolkit.datax.TestData;

public class TestCODeltaScenario1 extends CODeltaScenario1 {
	
	public String scenarioPolicyType = "HO6";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO6;
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
	public void TC03_verifyQuoteODD() {
		super.TC03_verifyQuoteODD();
	}
	
	@Test
	public void TC04_verifyAdverselyImpacted() {
		super.TC04_verifyAdverselyImpacted();
	}
	
	@Test
	public void TC05_verifyRoofTypeUneligible() {
		TestData td_sc1 = getTestSpecificTD("TestData");
		super.TC05_verifyRoofTypeUneligible(td_sc1);
	}
	
	@Test
	public void TC06_purchasePolicy() {
		TestData td_sc1 = getTestSpecificTD("TestData");
		super.TC06_purchasePolicy(td_sc1, scenarioPolicyType);
	}
	
	@Test
	public void TC07_verifyPolicyODD() {
		super.TC07_verifyPolicyODD();
	}
	
}

