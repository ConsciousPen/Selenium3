package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ca.dp3.HomeCaDP3OpenLFile;
import aaa.helpers.openl.model.home_ca.dp3.HomeCaDP3OpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeCaDP3TestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;

public class HomeCaDP3PremiumCalculationTest extends OpenLRatingBaseTest<HomeCaDP3OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_DP3;
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<HomeCaDP3OpenLPolicy> tdGenerator = new HomeCaDP3TestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, HomeCaDP3OpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
