package aaa.modules.rating.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.rating.OpenLTestsHolder;
import aaa.helpers.rating.RatingBaseTest;
import aaa.main.modules.policy.PolicyType;
import aaa.utils.openl.model.AutoSSOpenLPolicy;
import aaa.utils.openl.parser.AutoSSOpenLFileParser;

public class PremiumCalculationTest extends RatingBaseTest {
	private OpenLTestsHolder<AutoSSOpenLFileParser, AutoSSOpenLPolicy> openLTests;

	public PremiumCalculationTest() {
		openLTests = new OpenLTestsHolder<>(String.format("%1$s//%2$sTests-20170915.xls", OPENL_RATING_TESTS_FOLDER, getState()), new AutoSSOpenLFileParser());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void premiumCalculationTest(@Optional("") String state) {
		verifyPremiums(openLTests);
	}
}
