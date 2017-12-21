package aaa.helpers.openl.testdata_builder;

import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.auto_ca.AutoCaOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoCaTestDataGenerator extends TestDataGenerator<AutoCaOpenLPolicy> {
	public AutoCaTestDataGenerator(String state) {
		super(state);
	}

	public AutoCaTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
