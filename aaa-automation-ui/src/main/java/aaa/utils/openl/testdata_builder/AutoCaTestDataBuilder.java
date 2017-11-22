package aaa.utils.openl.testdata_builder;

import aaa.utils.openl.model.AutoCaOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoCaTestDataBuilder implements TestDataBuilder<AutoCaOpenLPolicy> {
	@Override
	public TestData buildRatingData(AutoCaOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}
}
