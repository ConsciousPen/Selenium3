package aaa.modules.openl;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.main.modules.policy.home_ca.defaulttabs.PremiumsAndCoveragesQuoteTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import toolkit.datax.TestData;

public class HomeCaPremiumCalculationTest<P extends HomeCaOpenLPolicy<?>> extends OpenLRatingBaseTest<P> {
	@Override
	protected TestData getRatingDataPattern() {
		return getPolicyTD("DataGather", "TestData_CA").mask(new PurchaseTab().getMetaKey());
	}

	@Override
	protected Dollar createAndRateQuote(P openLPolicy) {
		@SuppressWarnings("unchecked")
		TestDataGenerator<P> tdGenerator = (TestDataGenerator<P>) openLPolicy.getTestDataGenerator(getState(), getRatingDataPattern());
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumsAndCoveragesQuoteTab.class, false);
		new PremiumsAndCoveragesQuoteTab().fillTab(quoteRatingData);
		return PremiumsAndCoveragesQuoteTab.getPolicyTermPremium();
	}
}
