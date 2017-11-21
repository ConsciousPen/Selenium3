package aaa.utils.openl.parser;

import java.io.File;
import org.apache.commons.lang3.NotImplementedException;
import aaa.utils.openl.model.HomeCaOpenLPolicy;
import aaa.utils.openl.testdata_builder.HomeCaTestDataBuilder;

public class HomeCaOpenLFileParser extends OpenLFileParser<HomeCaOpenLPolicy, HomeCaTestDataBuilder> {
	public HomeCaOpenLFileParser() {
		super(new HomeCaTestDataBuilder());
	}

	@Override
	public OpenLFileParser parse(File openLtestsFile) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}
}
