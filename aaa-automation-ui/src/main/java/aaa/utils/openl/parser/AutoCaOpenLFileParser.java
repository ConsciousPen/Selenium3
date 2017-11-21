package aaa.utils.openl.parser;

import java.io.File;
import org.apache.commons.lang3.NotImplementedException;
import aaa.utils.openl.model.AutoCaOpenLPolicy;
import aaa.utils.openl.testdata_builder.AutoCaTestDataBuilder;

public class AutoCaOpenLFileParser extends OpenLFileParser<AutoCaOpenLPolicy, AutoCaTestDataBuilder> {

	public AutoCaOpenLFileParser() {
		super(new AutoCaTestDataBuilder());
	}

	@Override
	public OpenLFileParser parse(File openLtestsFile) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}
}
