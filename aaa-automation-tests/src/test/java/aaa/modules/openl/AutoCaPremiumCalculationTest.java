package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLFile;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoCaTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;

public class AutoCaPremiumCalculationTest extends OpenLRatingBaseTest<AutoCaOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<AutoCaOpenLPolicy> tdGenerator = new AutoCaTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, AutoCaOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
