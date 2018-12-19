package aaa.helpers.openl.testdata_generator;

import aaa.helpers.TestDataHelper;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import aaa.main.metadata.policy.HomeSSMetaData;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

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
		TestData overrideErrorData = super.getOverrideErrorData(openLPolicy);

		if (Double.parseDouble(openLPolicy.getCoverages().stream().filter(c -> "CovC".equals(c.getCode())).findFirst().get().getLimit()) > 100000.00) {

		}
		return overrideErrorData;
	}
}
