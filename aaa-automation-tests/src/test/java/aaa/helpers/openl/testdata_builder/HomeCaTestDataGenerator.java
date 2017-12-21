package aaa.helpers.openl.testdata_builder;

import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.home_ca.HomeCaOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeCaTestDataGenerator extends TestDataGenerator<HomeCaOpenLPolicy> {
	public HomeCaTestDataGenerator(String state) {
		super(state);
	}

	public HomeCaTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeCaOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
