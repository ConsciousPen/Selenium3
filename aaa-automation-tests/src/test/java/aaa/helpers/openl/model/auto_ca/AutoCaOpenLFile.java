package aaa.helpers.openl.model.auto_ca;

import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import aaa.helpers.openl.model.OpenLFile;

public class AutoCaOpenLFile extends OpenLFile<AutoCaOpenLPolicy> {
	@Override
	public List<AutoCaOpenLPolicy> getPolicies() {
		throw new NotImplementedException("getPolicies() not implemented yet");
	}
}
