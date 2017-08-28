package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.CODeltaScenario1;
import toolkit.utils.TestInfo;

public class TestCODeltaScenario1 extends CODeltaScenario1 {
	
	public String scenarioPolicyType = "HO3-Heritage";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3) 
	public void TC01_createQuote() {				
		super.TC_createQuote(scenarioPolicyType);
	}

	@Test
	public void TC02_verifyLOVsOfImmediatePriorCarrier() {
		super.TC_verifyLOVsOfImmediatePriorCarrier();
	}
	
	@Test
	public void TC03_verifyEndorsementsTab() {
		super.TC_verifyEndorsementsTab();
	}
/*	
	@Test
	public void TC04_verifyQuoteODD() {
		super.TC_verifyQuoteODD();
	}
*/	
	@Test
	public void TC05_verifyAdverselyImpacted() {
		super.TC_verifyAdverselyImpacted();
	}
	
	@Test
	public void TC06_verifyIneligibleRoofType() {
		super.TC_verifyIneligibleRoofType();
	}
	
	@Test
	public void TC07_purchasePolicy() {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
/*	
	@Test
	public void TC08_verifyPolicyODD() {
		super.TC_verifyPolicyODD();
	}
*/
}

