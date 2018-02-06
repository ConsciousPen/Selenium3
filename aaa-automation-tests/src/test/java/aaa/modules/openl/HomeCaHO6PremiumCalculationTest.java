package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLFile;
import aaa.helpers.openl.model.home_ca.ho6.HomeCaHO6OpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeCaHO6TestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;

public class HomeCaHO6PremiumCalculationTest extends OpenLRatingBaseTest<HomeCaHO6OpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO6;
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<HomeCaHO6OpenLPolicy> tdGenerator = new HomeCaHO6TestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, HomeCaHO6OpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
