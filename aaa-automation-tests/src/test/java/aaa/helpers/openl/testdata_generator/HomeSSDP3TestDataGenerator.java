package aaa.helpers.openl.testdata_generator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import aaa.main.modules.policy.home_ss.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ss.defaulttabs.DocumentsTab;
import aaa.main.modules.policy.home_ss.defaulttabs.ErrorTab;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.datax.impl.SimpleDataProvider;

public class HomeSSDP3TestDataGenerator extends HomeSSTestDataGenerator {
	public HomeSSDP3TestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {
		return super.getRatingData(openLPolicy);
	}

	@Override
	public TestData getProofData(HomeSSOpenLPolicy openLPolicy) {
		TestData proofData = super.getProofData(openLPolicy);
		if (openLPolicy.getPolicyDiscountInformation().isUnderlyingRenterPolicy() && openLPolicy.getPolicyDiscountInformation().getProofOfTenant()) {
			proofData = TestDataHelper.merge(DataProviderFactory.dataOf(HomeSSMetaData.DocumentsTab.DOCUMENTS_TO_BIND.getLabel(), DataProviderFactory.dataOf(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_UNDERLYING_INSURANCE_POLICY.getLabel(), "Yes")), proofData);
		}
		return proofData;
	}

	@Override
	public TestData getOverrideErrorData(HomeSSOpenLPolicy openLPolicy) {
		if (Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCode())).findFirst().get().getLimit()) > 100000.00) {
			LinkedHashMap<String, String> tdMap = new LinkedHashMap<>();
			if ("Central".equals(openLPolicy.getPolicyDiscountInformation().getTheftAlarmType())) {
				tdMap.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_CENTRAL_THEFT_ALARM.getLabel(), "Yes");
			}
			if (openLPolicy.getPolicyDiscountInformation().isUnderlyingRenterPolicy()) {
				tdMap.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_UNDERLYING_INSURANCE_POLICY.getLabel(), "Yes");
			}
			if (openLPolicy.getPolicyDiscountInformation().getGreenHomeDiscApplicability()) {
				tdMap.put(HomeSSMetaData.DocumentsTab.DocumentsToBind.PROOF_OF_ENERGY_STAR_APPLIANCES.getLabel(), "Yes");
			}
			TestData overrideErrorData = TestDataHelper.merge(getRatingDataPattern().getTestData(HomeSSMetaData.DocumentsTab.class.getSimpleName()), DataProviderFactory.dataOf(HomeSSMetaData.DocumentsTab.DOCUMENTS_TO_BIND.getLabel(), new SimpleDataProvider(tdMap)));
			overrideErrorData = DataProviderFactory.dataOf(new DocumentsTab().getMetaKey(), overrideErrorData)
					.adjust(TestData.makeKeyPath(new BindTab().getMetaKey()), DataProviderFactory.emptyData())
					.adjust(TestData.makeKeyPath(new ErrorTab().getMetaKey()), DataProviderFactory.dataOf(
							HomeSSMetaData.ErrorTab.ERROR_OVERRIDE.getLabel(), DataProviderFactory.dataOf(
									HomeSSMetaData.ErrorTab.ErrorsOverride.MESSAGE.getLabel(), "contains=UW approval is required to bind due to Personal Property Value",
									HomeSSMetaData.ErrorTab.ErrorsOverride.OVERRIDE.getLabel(), true,
									HomeSSMetaData.ErrorTab.ErrorsOverride.APPROVAL.getLabel(), true,
									HomeSSMetaData.ErrorTab.ErrorsOverride.DURATION.getLabel(), "Life",
									HomeSSMetaData.ErrorTab.ErrorsOverride.REASON_FOR_OVERRIDE.getLabel(), "index=1"
							)
					));
			return overrideErrorData;
		}
		return DataProviderFactory.emptyData();
	}
}
