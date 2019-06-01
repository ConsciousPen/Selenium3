package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelReinstate;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * <b> Test Cancel and reinstate home ho3 policy </b>
 * <p> Steps:
 * <p> see parent class
 *
 */
public class TestPolicyCancelReinstate extends PolicyCancelReinstate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }
    
    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyCancelReinstate(@Optional("CA") String state) {

		testPolicyCancelReinstate();
        
    }
}
