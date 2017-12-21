package aaa.helpers.openl.testdata_builder;

import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.home_ss.HomeSSOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class HomeSSTestDataGenerator extends TestDataGenerator<HomeSSOpenLPolicy> {
	public HomeSSTestDataGenerator(String state) {
		super(state);
	}

	public HomeSSTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(HomeSSOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
