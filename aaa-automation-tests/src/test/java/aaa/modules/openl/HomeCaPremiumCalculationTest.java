package aaa.modules.openl;

import com.exigen.ipb.eisa.utils.Dollar;
import aaa.common.Tab;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import toolkit.datax.TestData;

public class HomeCaPremiumCalculationTest<P extends HomeCaOpenLPolicy<?, ?>> extends OpenLRatingBaseTest<P> {
	@Override
	protected TestData getRatingDataPattern() {
		return getPolicyTD("DataGather", "TestData_CA").mask(new PurchaseTab().getMetaKey());
	}

	@Override
	protected String createQuote(P openLPolicy) {
		@SuppressWarnings("unchecked")
		TestDataGenerator<P> tdGenerator = (TestDataGenerator<P>) openLPolicy.getTestDataGenerator(getRatingDataPattern());
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.get().initiate();
		policy.get().getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);
		new PremiumsAndCoveragesQuoteTab().getAssetList().fill(quoteRatingData);
		return Tab.labelPolicyNumber.getValue();
	}

	@Override
	protected Dollar calculatePremium(P openLPolicy) {
		new PremiumsAndCoveragesQuoteTab().calculatePremium();
		return PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
	}

	@Override
	protected String createCustomerIndividual(HomeCaOpenLPolicy openLPolicy) {
		return createCustomerIndividual();
	}
}
