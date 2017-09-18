package aaa.modules.delta.home_ss.ho3;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.delta.templates.MDDeltaScenario1;
import toolkit.utils.TestInfo;

public class TestMDDeltaScenario1 extends MDDeltaScenario1 {
	
	public String scenarioPolicyType = "HO3-Heritage";
	
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Parameters({"state"})
	@Test(groups = { Groups.DELTA, Groups.HIGH })
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3) 
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
	public void TC03_verifyEndorsementHS0495(String state) {
		super.TC_verifyEndorsementHS0495(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC04_verifyEndorsementHS2338(String state) {
		super.TC_verifyEndorsementHS2338();
	}
	
	@Parameters({"state"})
	@Test
	public void TC05_verifyStormShutterDiscount(String state) {
		super.TC_verifyStormShutterDiscount();
	}
	
	@Parameters({"state"})
	@Test
	public void TC06_verifyUnderwritingApprovalTab (String state) {
		super.TC_verifyUnderwritingApprovalTab();
	}
	
	@Parameters({"state"})
	@Test
	public void TC07_purchasePolicy(String state) {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Parameters({"state"})
	@Test
	public void TC08_verifyCancelNoticeTab(String state) {
		super.TC_verifyCancelNoticeTab();
	}

}
