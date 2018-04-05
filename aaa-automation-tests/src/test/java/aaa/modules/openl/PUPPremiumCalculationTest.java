package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.pup.PUPOpenLFile;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.helpers.openl.testdata_builder.PUPTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import toolkit.datax.TestData;

public class PUPPremiumCalculationTest extends OpenLRatingBaseTest<PUPOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new PrefillTab().getMetaKey());
	}

	@Override
	protected String createAndRateQuote(TestDataGenerator<PUPOpenLPolicy> tdGenerator, PUPOpenLPolicy openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesQuoteTab.class, false);
		new PremiumAndCoveragesQuoteTab().fillTab(quoteRatingData);
		return PremiumAndCoveragesQuoteTab.getPolicyTermPremium().toString();
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<PUPOpenLPolicy> tdGenerator = new PUPTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, PUPOpenLFile.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
