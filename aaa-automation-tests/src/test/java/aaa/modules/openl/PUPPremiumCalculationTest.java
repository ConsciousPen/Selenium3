package aaa.modules.openl;

import java.util.Map;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.common.enums.Constants;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.pup.defaulttabs.PremiumAndCoveragesQuoteTab;
import toolkit.datax.TestData;

public class PUPPremiumCalculationTest extends OpenLRatingBaseTest<PUPOpenLPolicy> {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.PUP;
	}

	@Override
	protected String createQuote(PUPOpenLPolicy openLPolicy) {
		TestData quoteRatingData = openLPolicy.getTestDataGenerator(getState(), getRatingDataPattern()).getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesQuoteTab.class, false);
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
	protected Map<String, String> getOpenLFieldsMapFromTest(PUPOpenLPolicy openLPolicy) {
		Map<String, String> openLFieldsMap = super.getOpenLFieldsMapFromTest(openLPolicy);
		//does not affect rating
		openLFieldsMap.remove("policy.dwelling.viciousDogCount");
		openLFieldsMap.remove("policy.dwelling.address.county");
		return openLFieldsMap;
	}
}
