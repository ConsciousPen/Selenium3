package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLFile;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeSSTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;

public class HomeSSPremiumCalculationTest extends OpenLRatingBaseTest<HomeSSOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<HomeSSOpenLPolicy> tdGenerator = new HomeSSTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, HomeSSOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
