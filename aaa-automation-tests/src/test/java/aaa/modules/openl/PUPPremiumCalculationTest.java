package aaa.modules.openl;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import aaa.main.modules.policy.pup.defaulttabs.UnderlyingRisksAutoTab;
import toolkit.datax.TestData;

public class PUPPremiumCalculationTest extends OpenLRatingBaseTest<PUPOpenLPolicy> {

	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new PrefillTab().getMetaKey(), new UnderlyingRisksAutoTab().getMetaKey());
	}

	@Override
	protected String createQuote(PUPOpenLPolicy openLPolicy) {
		TestData quoteRatingData = openLPolicy.getTestDataGenerator(getRatingDataPattern()).getRatingData(openLPolicy);
		policy.get().initiate();
		policy.get().getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesQuoteTab.class, false);
		new PremiumAndCoveragesQuoteTab().getAssetList().fill(quoteRatingData);
		return Tab.labelPolicyNumber.getValue();
	}

	@Override
	protected Dollar calculatePremium(PUPOpenLPolicy openLPolicy) {
		new PremiumAndCoveragesQuoteTab().calculatePremium();
		return Constants.States.KY.equals(getState()) || Constants.States.WV.equals(getState())
				? PremiumAndCoveragesQuoteTab.getPUPCoveragePremium()
				: PremiumAndCoveragesQuoteTab.getPolicyTermPremium();
	}

	@Override
	protected String createCustomerIndividual(PUPOpenLPolicy openLPolicy) {
		return createCustomerIndividual();
	}
}
