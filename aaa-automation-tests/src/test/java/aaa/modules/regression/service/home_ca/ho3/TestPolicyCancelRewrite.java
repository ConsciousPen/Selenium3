package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelRewrite;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * @name Test Policy rewrite
 * @scenario
 * see parent class
 * @details
 */
public class TestPolicyCancelRewrite extends PolicyCancelRewrite {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_CA_HO3;
    }
    
	@Test
	@TestInfo(component = "Policy.HomeCA")
	public void testPolicyCancelRewrite() {
		
	    super.testPolicyCancelRewrite();

	}
}
