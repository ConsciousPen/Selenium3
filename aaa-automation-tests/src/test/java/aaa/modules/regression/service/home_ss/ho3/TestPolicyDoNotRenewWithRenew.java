package aaa.modules.regression.service.home_ss.ho3;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import toolkit.utils.TestInfo;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.service.template.PolicyDoNotRenewWithRenew;

/**
 * @author Lina Li
 * @name Test Policy do not renewal with Renewal
 * @scenario
 * 1. Find customer or create new if customer does not exist;
 * 2. Create new Policy;
 * 3. Mark do not renewal flag on policy
 * 4. Verify Policy status is 'Policy Active'
 * 5. Verify 'Do Not Renew' flag is displayed in the policy overview header
 * 6. Change time to renewal image generation date, run jobs renewalOfferGenerationPart1 and renewalOfferGenerationPart2
 * 7. Verify 'Renewals' button is not displayed in the policy overview header
 * @details
 */

public class TestPolicyDoNotRenewWithRenew extends PolicyDoNotRenewWithRenew{
	@Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }
    
	@Parameters({"state"})
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
    public void TC01_CreatePolicyAddDoNotRenew(@Optional("") String state) {

        super.TC01_CreatePolicyAddDoNotRenew();
    }
    
	@Parameters({"state"})
	@Test(dependsOnMethods = "TC01_CreatePolicyAddDoNotRenew",
			groups = {Groups.REGRESSION, Groups.CRITICAL})
	@TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3)
	public void TC02_RenewPolicy(@Optional("") String state) {
		super.TC02_RenewPolicy();
	}
}
