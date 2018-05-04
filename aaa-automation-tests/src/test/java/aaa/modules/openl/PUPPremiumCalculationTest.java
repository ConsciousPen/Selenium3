package aaa.modules.openl;

import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.OpenLTest;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.helpers.openl.testdata_builder.PUPTestDataGenerator;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import toolkit.datax.TestData;

public class PUPPremiumCalculationTest extends OpenLRatingBaseTest<PUPOpenLPolicy, OpenLTest> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new PrefillTab().getMetaKey(), new UnderlyingRisksAutoTab().getMetaKey());
	}

	@Override
	protected Dollar createAndRateQuote(TestDataGenerator<PUPOpenLPolicy> tdGenerator, PUPOpenLPolicy openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesQuoteTab.class, false);
		new PremiumAndCoveragesQuoteTab().fillTab(quoteRatingData);
		return PremiumAndCoveragesQuoteTab.getPolicyTermPremium();
	}

	@Parameters({"state", "fileName", "policyNumbers"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, String fileName, @Optional("") String policyNumbers) {
		TestDataGenerator<PUPOpenLPolicy> tdGenerator = new PUPTestDataGenerator(getState(), getRatingDataPattern());
		verifyPremiums(fileName, PUPOpenLPolicy.class, OpenLTest.class, tdGenerator, getPolicyNumbers(policyNumbers));
	}
}
