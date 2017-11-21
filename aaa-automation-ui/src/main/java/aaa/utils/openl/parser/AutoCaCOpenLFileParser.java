package aaa.utils.openl.parser;

import java.io.File;
import org.apache.commons.lang3.NotImplementedException;
import aaa.utils.openl.model.AutoCaCOpenLPolicy;
import aaa.utils.openl.testdata_builder.AutoCaCTestDataBuilder;

public class AutoCaCOpenLFileParser extends OpenLFileParser<AutoCaCOpenLPolicy, AutoCaCTestDataBuilder> {
	public AutoCaCOpenLFileParser() {
		super(new AutoCaCTestDataBuilder());
	}

	@Override
	public OpenLFileParser parse(File openLtestsFile) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}
}
