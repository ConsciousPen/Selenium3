package aaa.modules.openl;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.helpers.openl.model.OpenLVehicle;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLDriver;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.AutoCaTestDataGenerator;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.modules.policy.auto_ca.defaulttabs.AssignmentTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.DriverTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.PremiumAndCoveragesTab;
import aaa.main.modules.policy.auto_ca.defaulttabs.VehicleTab;
import toolkit.datax.TestData;
import toolkit.utils.datetime.DateTimeUtils;

public class AutoCaPremiumCalculationTest<D extends AutoCaOpenLDriver, V extends OpenLVehicle, P extends AutoCaOpenLPolicy<D, V>> extends OpenLRatingBaseTest<P> {
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
	protected String createCustomerIndividual(P openLPolicy) {
		AutoCaTestDataGenerator<D, V, P> tdGenerator = (AutoCaTestDataGenerator<D, V, P>) openLPolicy.getTestDataGenerator(getRatingDataPattern());
		openLPolicy.getDrivers().get(0).setDriverAge(tdGenerator.getDriverAge(openLPolicy.getDrivers().get(0)));
		TestData td = getCustomerIndividualTD("DataGather", "TestData")
				.adjust(TestData.makeKeyPath(CustomerMetaData.GeneralTab.class.getSimpleName(), CustomerMetaData.GeneralTab.DATE_OF_BIRTH.getLabel()),
						openLPolicy.getEffectiveDate().minusYears(openLPolicy.getDrivers().get(0).getDriverAge()).format(DateTimeUtils.MM_DD_YYYY));
		return createCustomerIndividual(td);
	}
}
