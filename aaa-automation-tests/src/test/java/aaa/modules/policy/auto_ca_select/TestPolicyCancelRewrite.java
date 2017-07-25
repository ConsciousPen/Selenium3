package aaa.modules.policy.auto_ca_select;

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
        return PolicyType.AUTO_CA_SELECT;
    }
    
	@Test
	@TestInfo(component = "Policy.AutoCA")
	public void testPolicyCancelRewrite() {
		
	    super.testPolicyCancelRewrite();

	}
}
