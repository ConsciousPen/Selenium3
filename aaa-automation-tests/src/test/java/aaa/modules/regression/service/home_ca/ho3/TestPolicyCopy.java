package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCopy;

public class TestPolicyCopy extends PolicyCopy {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
	
	/**
    * @author Jurij Kuznecov
    * @name Test Policy Copy
    * @scenario
    * 1. Create new or open existent Customer Individual;
    * 2. Initiate CAH quote creation, set effective date to today;
    * 3. Fill all mandatory fields and purchase policy;
    * 4. Copy policy and purchase;
    * 5. Verify that new policy number is not the same as initial policy number;
    **/
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyCopy() {
    	super.testPolicyCopy();
    }

}
