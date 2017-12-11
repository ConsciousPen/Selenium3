package aaa.helpers.openl.testdata_builder;

import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.pup.PUPOpenLPolicy;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;

public class PUPTestDataGenerator extends TestDataGenerator<PUPOpenLPolicy> {
	@Override
	public TestData getRatingData(PUPOpenLPolicy openLPolicy) {
		return DataProviderFactory.emptyData();
	}

	@Override
	public void setRatingDataPattern(TestData ratingDataPattern) {
		//TODO-dchubkov: to be implemented
		throw new NotImplementedException("setRatingDataPattern(TestData ratingDataPattern) not implemented yet");
	}
}
