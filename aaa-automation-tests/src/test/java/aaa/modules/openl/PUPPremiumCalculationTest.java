package aaa.modules.openl;

import aaa.common.enums.Constants;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import com.exigen.ipb.etcsa.utils.Dollar;
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
		return Constants.States.KY.equals(getState()) || Constants.States.WV.equals(getState()) ? PremiumAndCoveragesQuoteTab.getPUPCoveragePremium() : PremiumAndCoveragesQuoteTab.getPolicyTermPremium();
	}
}
