package aaa.helpers.openl.testdata_builder;

import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeCaTestDataGenerator extends TestDataGenerator<HomeCaOpenLPolicy> {
	@Override
	public TestData getRatingData(HomeCaOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
	}
}
