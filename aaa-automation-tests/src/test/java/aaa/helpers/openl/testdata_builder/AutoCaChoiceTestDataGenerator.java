package aaa.helpers.openl.testdata_builder;

import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.auto_ca.choice.AutoCaChoiceOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class AutoCaChoiceTestDataGenerator extends TestDataGenerator<AutoCaChoiceOpenLPolicy> {
	public AutoCaChoiceTestDataGenerator(String state) {
		super(state);
	}

	public AutoCaChoiceTestDataGenerator(String state, TestData ratingDataPattern) {
		super(state, ratingDataPattern);
	}

	@Override
	public TestData getRatingData(AutoCaChoiceOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
