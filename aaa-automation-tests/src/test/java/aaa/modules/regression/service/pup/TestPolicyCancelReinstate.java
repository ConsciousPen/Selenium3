package aaa.modules.regression.service.pup;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelReinstate;
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
        return PolicyType.PUP;
    }
    
    @Parameters({"state"})
    //@StateList("All")
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Service.PUP)
    public void testPolicyCancelReinstate(@Optional("") String state) {

		testPolicyCancelReinstate();
        
    }
}
