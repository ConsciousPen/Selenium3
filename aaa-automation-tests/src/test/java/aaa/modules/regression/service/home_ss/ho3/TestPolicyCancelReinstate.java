package aaa.modules.regression.service.home_ss.ho3;


import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelReinstate;
import aaa.utils.StateList;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
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
        return PolicyType.HOME_SS_HO3;
    }
    
    @Parameters({"state"})
    @StateList(statesExcept = { States.CA })
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
    public void testPolicyCancelReinstate(@Optional("") String state) {
        
    	super.testPolicyCancelReinstate();
        
    }
}
