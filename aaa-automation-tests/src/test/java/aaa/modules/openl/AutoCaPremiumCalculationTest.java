package aaa.modules.openl;

import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.common.Tab;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import aaa.helpers.openl.testdata_generator.TestDataGenerator;
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
	protected String createQuote(P openLPolicy) {
		@SuppressWarnings("unchecked")
		TestDataGenerator<P> tdGenerator = (TestDataGenerator<P>) openLPolicy.getTestDataGenerator(getState(), getRatingDataPattern());
		TestData quoteRatingData = tdGenerator.getRatingData(openLPolicy);
		policy.initiate();
		policy.getDefaultView().fillUpTo(quoteRatingData, PremiumAndCoveragesTab.class, false);
		new PremiumAndCoveragesTab().getAssetList().fill(quoteRatingData);
		return Tab.labelPolicyNumber.getValue();
	}

	@Override
	protected Dollar calculatePremium(P openLPolicy) {
		new PremiumAndCoveragesTab().calculatePremium();
		return new Dollar(PremiumAndCoveragesTab.totalTermPremium.getValue());
	}

	/*@Override
	protected Map<String, String> getOpenLFieldsMapFromTest(P openLPolicy) {
		Map<String, String> openLFieldsMap = super.getOpenLFieldsMapFromTest(openLPolicy);

		Pattern driverIdPattern = Pattern.compile("^policy\\.drivers\\[\\d+\\]\\.id$");
		Pattern vehicleIdPattern = Pattern.compile("^policy\\.vehicles\\[\\d+\\]\\.id$");
		// additionalLimitAmount is applicable for altCoverages only, for regular coverage should be always null
		Pattern coverageAdditionalLimitAmountPattern = Pattern.compile("^policy\\.vehicles\\[\\d+\\]\\.coverages\\[\\d+\\]\\.additionalLimitAmount$");

		openLFieldsMap.entrySet().removeIf(e -> "policy.policyNumber".equals(e.getKey())
				|| driverIdPattern.matcher(e.getKey()).matches()
				|| vehicleIdPattern.matcher(e.getKey()).matches()
				|| coverageAdditionalLimitAmountPattern.matcher(e.getKey()).matches());

		List<String> coverageCDsList = openLFieldsMap.entrySet().stream().filter(e -> e.getKey().endsWith("coverageCd")).map(Map.Entry::getKey).collect(Collectors.toList());
		coverageCDsList.forEach(cd -> openLFieldsMap.put(cd.replace("coverageCd", "coverageCD"), openLFieldsMap.remove(cd)));
		return openLFieldsMap;
	}*/
}
