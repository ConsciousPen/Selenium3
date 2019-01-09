package aaa.modules.openl;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import toolkit.datax.TestData;

public class AutoCaPremiumCalculationTest<P extends AutoCaOpenLPolicy<?, ?>> extends OpenLRatingBaseTest<P> {
	@Override
	protected TestData getRatingDataPattern() {
		return super.getRatingDataPattern().mask(new DriverTab().getMetaKey(), new VehicleTab().getMetaKey(), new PremiumAndCoveragesTab().getMetaKey(), new AssignmentTab().getMetaKey());
	}

	@Override
	protected String createQuote(P openLPolicy) {
		@SuppressWarnings("unchecked")
		TestDataGenerator<P> tdGenerator = (TestDataGenerator<P>) openLPolicy.getTestDataGenerator(getRatingDataPattern());
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.get().initiate();
		policy.get().getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		new PremiumAndCoveragesTab().getAssetList().fill(quoteRatingData);
		return Tab.labelPolicyNumber.getValue();
	}

	@Override
	protected Dollar calculatePremium(P openLPolicy) {
		new PremiumAndCoveragesTab().calculatePremium();
		return new Dollar(PremiumAndCoveragesTab.totalTermPremium.getValue());
	}

	@Override
	protected String createCustomerIndividual(AutoCaOpenLPolicy openLPolicy) {
		return createCustomerIndividual();
	}
}
