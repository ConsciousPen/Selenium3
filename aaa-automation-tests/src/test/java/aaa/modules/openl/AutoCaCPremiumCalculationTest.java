package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.auto_ca.AutoCaCOpenLFile;
import aaa.helpers.openl.model.auto_ca.AutoCaCOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoCaCTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;

public class AutoCaCPremiumCalculationTest extends OpenLRatingBaseTest<AutoCaCOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_CHOICE;
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<AutoCaCOpenLPolicy> tdGenerator = new AutoCaCTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, AutoCaCOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
