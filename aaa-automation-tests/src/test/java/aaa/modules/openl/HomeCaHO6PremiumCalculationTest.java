package aaa.modules.openl;

import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLPolicy;
import aaa.main.modules.policy.PolicyType;

public class HomeCaHO6PremiumCalculationTest extends HomeCaPremiumCalculationTest<HomeCaHO6OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO6;
	}
	
	@Parameters({"state"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, ITestContext context) {
		verifyPremiums(context);
	}
}
