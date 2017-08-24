package aaa.modules.delta.home_ss.ho3;

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
	public void TC03_verifyEndorsementHS0495() {
		super.TC_verifyEndorsementHS0495(scenarioPolicyType);
	}
	
	@Test
	public void TC04_verifyEndorsementHS2338() {
		super.TC_verifyEndorsementHS2338();
	}
	
	@Test
	public void TC05_verifyStormShutterDiscount() {
		super.TC_verifyStormShutterDiscount();
	}
	
	@Test
	public void TC06_verifyUnderwritingApprovalTab () {
		super.TC_verifyUnderwritingApprovalTab();
	}
	
	@Test
	public void TC07_purchasePolicy() {
		super.TC_purchasePolicy(scenarioPolicyType);
	}
	
	@Test
	public void TC08_verifyCancelNoticeTab() {
		super.TC_verifyCancelNoticeTab();
	}

}
