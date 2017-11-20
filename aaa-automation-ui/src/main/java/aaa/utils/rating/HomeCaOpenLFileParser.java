package aaa.utils.rating;

import aaa.utils.rating.openl_objects.OpenLPolicy;
import com.exigen.ipb.etcsa.utils.Dollar;
import org.apache.commons.lang3.NotImplementedException;
import toolkit.datax.TestData;

import java.io.File;

public class HomeCaOpenLFileParser extends OpenLFileParser {
	@Override
	public OpenLFileParser parse(File openLtestsFile) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}

	@Override
	public TestData generateTestData(OpenLPolicy openLPolicy) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}

	@Override
	public Dollar getFinalPremium(int policyNumber) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}
}
