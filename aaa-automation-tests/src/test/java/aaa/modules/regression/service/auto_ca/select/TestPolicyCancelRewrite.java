package aaa.modules.regression.service.auto_ca.select;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCancelRewrite;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

/**
 * @author Jelena Dembovska
 * <b> Test Policy rewrite </b>
 * <p> Steps:
 * <p> see parent class
 *
 */
public class TestPolicyCancelRewrite extends PolicyCancelRewrite {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }
    
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Service.AUTO_CA_SELECT)
	public void testPolicyCancelRewrite(@Optional("CA") String state) {

		testPolicyCancelRewrite();

	}
}
