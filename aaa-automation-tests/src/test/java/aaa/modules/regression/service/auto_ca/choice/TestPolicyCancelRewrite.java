package aaa.modules.regression.service.auto_ca.choice;

import org.testng.annotations.Test;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.policy.templates.PolicyCancelRewrite;
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
        return PolicyType.AUTO_CA_CHOICE;
    }
    
	@Test
	@TestInfo(component = "Policy.AutoCA")
	public void testPolicyCancelRewrite() {
		
	    super.testPolicyCancelRewrite();

	}
}
