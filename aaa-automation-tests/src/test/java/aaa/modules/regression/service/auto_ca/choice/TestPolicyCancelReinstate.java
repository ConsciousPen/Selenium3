package aaa.modules.regression.service.auto_ca.choice;

import org.testng.annotations.Test;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelReinstate;
import toolkit.utils.TestInfo;

/**
 * @author Lina Li
 * @name Test Policy Cancel Reinstate
 * @scenario
 * see parent class
 * @details
 */
public class TestPolicyCancelReinstate extends PolicyCancelReinstate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }
    
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.AUTO_CA_CHOICE)
    public void testPolicyCancelReinstate() {
        
    	super.testPolicyCancelReinstate();
        
    }
}