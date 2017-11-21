package aaa.utils.openl.parser;

import java.io.File;
import org.apache.commons.lang3.NotImplementedException;
import aaa.utils.openl.model.HomeSSOpenLPolicy;
import aaa.utils.openl.testdata_builder.HomeSSTestDataBuilder;

public class HomeSSOpenLFileParser extends OpenLFileParser<HomeSSOpenLPolicy, HomeSSTestDataBuilder> {
	public HomeSSOpenLFileParser() {
		super(new HomeSSTestDataBuilder());
	}

	@Override
	public OpenLFileParser parse(File openLtestsFile) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}
}
