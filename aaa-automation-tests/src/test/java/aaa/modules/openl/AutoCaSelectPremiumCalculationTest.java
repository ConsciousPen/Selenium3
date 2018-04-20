package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLFile;
import aaa.helpers.openl.model.auto_ca.select.AutoCaSelectOpenLPolicy;
import aaa.helpers.openl.testdata_builder.AutoCaSelectTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import toolkit.datax.TestData;

public class AutoCaSelectPremiumCalculationTest extends AutoCaPremiumCalculationTest<AutoCaSelectOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.AUTO_CA_SELECT;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new AssignmentTab().getMetaKey());
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<AutoCaSelectOpenLPolicy> tdGenerator = new AutoCaSelectTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, AutoCaSelectOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
