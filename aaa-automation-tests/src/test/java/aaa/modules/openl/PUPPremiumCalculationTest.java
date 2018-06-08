package aaa.modules.openl;

import org.testng.ITestContext;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.constants.Groups;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import toolkit.datax.TestData;

public class PUPPremiumCalculationTest extends OpenLRatingBaseTest<PUPOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new PrefillTab().getMetaKey(), new UnderlyingRisksAutoTab().getMetaKey());
	}

	@Override
	protected Dollar createAndRateQuote(PUPOpenLPolicy openLPolicy) {
		TestData quoteRatingData = openLPolicy.getTestDataGenerator(getState(), getRatingDataPattern()).getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesQuoteTab.class, false);
		new PremiumAndCoveragesQuoteTab().fillTab(quoteRatingData);
		return "KY".equals(getState()) ? PremiumAndCoveragesQuoteTab.getPUPCoveragePremium() : PremiumAndCoveragesQuoteTab.getPolicyTermPremium();
	}
	
	@Parameters({"state"})
	@Test(groups = {Groups.OPENL, Groups.HIGH})
	public void premiumCalculationTest(@Optional("") String state, ITestContext context) {
		verifyPremiums(context);
	}
}
