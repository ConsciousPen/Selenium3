package aaa.modules.regression.service.pup;


import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelReinstate;

import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Cancel and reinstate home ho3 policy
 * @scenario
 * see parent class
 * @details
 */
public class TestPolicyCancelReinstate extends PolicyCancelReinstate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.PUP;
    }
    
    @Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.PUP)
    public void testPolicyCancelReinstate() {
        
    	super.testPolicyCancelReinstate();
        
    }
}
