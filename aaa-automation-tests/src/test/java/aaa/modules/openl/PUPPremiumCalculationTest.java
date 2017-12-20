package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.pup.PUPOpenLFile;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.helpers.openl.testdata_builder.PUPTestDataGenerator;
import aaa.main.modules.policy.PolicyType;

public class PUPPremiumCalculationTest extends OpenLRatingBaseTest<PUPOpenLPolicy> {
	public PUPPremiumCalculationTest() {
		super(new PUPTestDataGenerator());
	}

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		verifyPremiums(fileName, PUPOpenLFile.class, getPolicyNumbers(policyNumbers));
	}
}
