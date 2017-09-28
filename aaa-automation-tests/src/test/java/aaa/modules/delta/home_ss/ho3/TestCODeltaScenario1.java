package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
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
	
	@Parameters({"state"})
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3) 
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
	public void TC04_verifyQuoteODD(@Optional("") String state) {
		super.TC_verifyQuoteODD();
	}
*/	
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
/*	
	@Parameters({"state"})
	@Test
	public void TC08_verifyPolicyODD(@Optional("") String state) {
		super.TC_verifyPolicyODD();
	}
*/
}

