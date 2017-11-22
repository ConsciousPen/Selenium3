package aaa.utils.openl.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import aaa.utils.openl.model.OpenLPolicy;

public abstract class OpenLFileParser<P extends OpenLPolicy> {
	protected static final String POLICY_SHEET_NAME = "Batch- Policy";
	/*protected final static String CAPPING_DETAILS_SHEET_NAME = "Batch- CappingDetails";
	protected final static String VEHICLE_SHEET_NAME = "Batch- Vehicle";
	protected final static String ADDRESS_SHEET_NAME = "Batch- Address";
	protected final static String DRIVER_SHEET_NAME = "Batch- Driver";
	protected final static String COVERAGE_SHEET_NAME = "Batch- Coverage";
	protected final static String ENVIRONMENT_SHEET_NAME = "Environment";
	protected final static String TESTS_SHEET_NAME = "Tests";*/

	protected File openLFile;
	protected List<P> openLPolicies;

	public OpenLFileParser(String openLFilePath) {
		this(new File(openLFilePath));
	}

	public OpenLFileParser(File openLFile) {
		this.openLFile = openLFile;
		this.openLPolicies = new ArrayList<>();
	}

	public File getOpenLFile() {
		return openLFile;
	}

	public List<P> getPolicies() {
		return this.openLPolicies;
	}

	protected abstract boolean parse(File openLFile);
}
