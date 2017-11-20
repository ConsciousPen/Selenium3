package aaa.modules.rating.auto_ss;

import aaa.main.modules.policy.PolicyType;
import aaa.helpers.rating.RatingBaseTest;
import aaa.helpers.rating.OpenLTestsHolder;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class PremiumCalculationTest extends RatingBaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void premiumCalculationTest(@Optional("") String state) {
		OpenLTestsHolder openLtests = new OpenLTestsHolder(String.format("%1$s//%2$sTests-20170915.xls", OPENL_RATING_TESTS_FOLDER, getState()), getPolicyType());
		super.verifyPremiums(openLtests);
	}
}
