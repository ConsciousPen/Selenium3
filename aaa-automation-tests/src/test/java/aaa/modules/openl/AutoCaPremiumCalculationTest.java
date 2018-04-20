package aaa.modules.openl;

import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import toolkit.datax.TestData;

public class AutoCaPremiumCalculationTest<P extends AutoCaOpenLPolicy<?, ?>> extends OpenLRatingBaseTest<P> {
	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new DriverTab().getMetaKey(), new VehicleTab().getMetaKey(), new PremiumAndCoveragesTab().getMetaKey());
	}

	@Override
	protected String createAndRateQuote(TestDataGenerator<P> tdGenerator, P openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		new PremiumAndCoveragesTab().fillTab(quoteRatingData);
		return PremiumAndCoveragesTab.totalTermPremium.getValue();
	}
}
