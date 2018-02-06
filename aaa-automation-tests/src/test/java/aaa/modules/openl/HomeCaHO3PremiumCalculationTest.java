package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLFile;
import aaa.helpers.openl.model.home_ca.ho3.HomeCaHO3OpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeCaHO3TestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;

public class HomeCaHO3PremiumCalculationTest extends OpenLRatingBaseTest<HomeCaHO3OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<HomeCaHO3OpenLPolicy> tdGenerator = new HomeCaHO3TestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, HomeCaHO3OpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
