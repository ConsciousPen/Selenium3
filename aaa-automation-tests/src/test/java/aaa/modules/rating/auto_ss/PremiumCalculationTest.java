package aaa.modules.rating.auto_ss;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLFile;
import aaa.helpers.openl.model.auto_ss.AutoSSOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoSSTestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.rating.RatingBaseTest;

public class PremiumCalculationTest extends RatingBaseTest<AutoSSOpenLPolicy> {
	public PremiumCalculationTest() {
		super(new AutoSSTestDataGenerator());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_SS;
	}

	@Parameters({"state"})
	@Test
	public void premiumCalculationTest(@Optional("") String state) {
		String openLFileName = getState() + "Tests-20170915.xls";
		verifyPremiums(openLFileName, AutoSSOpenLFile.class);
	}
}
