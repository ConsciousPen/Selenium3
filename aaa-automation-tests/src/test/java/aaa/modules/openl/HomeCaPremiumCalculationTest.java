package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLFile;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.helpers.openl.testdata_builder.HomeCaTestDataGenerator;
import aaa.main.modules.policy.PolicyType;

public class HomeCaPremiumCalculationTest extends OpenLRatingBaseTest<HomeCaOpenLPolicy> {
	public HomeCaPremiumCalculationTest() {
		super(new HomeCaTestDataGenerator());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_CA_HO3;
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		verifyPremiums(fileName, HomeCaOpenLFile.class, getPolicyNumbers(policyNumbers));
	}
}
