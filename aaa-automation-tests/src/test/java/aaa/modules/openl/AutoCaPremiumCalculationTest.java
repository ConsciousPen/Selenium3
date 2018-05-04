package aaa.modules.openl;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.helpers.openl.model.OpenLTest;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_builder.TestDataGenerator;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import toolkit.datax.TestData;

public class AutoCaPremiumCalculationTest<P extends AutoCaOpenLPolicy<?, ?>, T extends OpenLTest> extends OpenLRatingBaseTest<P, T> {
	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new DriverTab().getMetaKey(), new VehicleTab().getMetaKey(), new PremiumAndCoveragesTab().getMetaKey());
	}

	@Override
	protected Dollar createAndRateQuote(TestDataGenerator<P> tdGenerator, P openLPolicy) {
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		new PremiumAndCoveragesTab().fillTab(quoteRatingData);
		return new Dollar(PremiumAndCoveragesTab.totalTermPremium.getValue());
	}
}
