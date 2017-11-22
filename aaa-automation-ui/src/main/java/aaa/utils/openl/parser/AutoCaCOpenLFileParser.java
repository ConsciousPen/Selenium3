package aaa.utils.openl.parser;

import java.io.File;
import org.apache.commons.lang3.NotImplementedException;
import aaa.utils.openl.model.AutoCaCOpenLPolicy;

public class AutoCaCOpenLFileParser extends OpenLFileParser<AutoCaCOpenLPolicy> {
	public AutoCaCOpenLFileParser(String openLFilePath) {
		super(openLFilePath);
		parse(getOpenLFile());
	}

	@Override
	public final boolean parse(File openLtestsFile) {
		//TODO-dchubkov: implement this method
		throw new NotImplementedException("method is not implemented yet for " + this.getClass().getSimpleName());
	}
}
