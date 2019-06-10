package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyCopy;
import aaa.utils.StateList;
import toolkit.utils.TestInfo;

public class TestPolicyCopy extends PolicyCopy {
	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}
	
	/**
    * @author Jurij Kuznecov
	 * <b> Test Policy Copy </b>
	 * <p> Steps:
	 * <p> 1. Create new or open existent Customer Individual;
	 * <p> 2. Initiate CAH quote creation, set effective date to today;
	 * <p> 3. Fill all mandatory fields and purchase policy;
	 * <p> 4. Copy policy and purchase;
	 * <p> 5. Verify that new policy number is not the same as initial policy number;
    **/
	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyCopy(@Optional("CA") String state) {
		testPolicyCopy();
    }

}
