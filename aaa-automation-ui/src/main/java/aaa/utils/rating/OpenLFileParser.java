package aaa.utils.rating;

import aaa.utils.rating.openl_objects.OpenLPolicy;
import com.exigen.ipb.etcsa.utils.Dollar;
import toolkit.datax.TestData;

import java.io.File;
import java.util.List;

public abstract class OpenLFileParser {
	protected final static String POLICY_SHEET_NAME = "Batch- Policy";
	/*protected final static String CAPPING_DETAILS_SHEET_NAME = "Batch- CappingDetails";
	protected final static String VEHICLE_SHEET_NAME = "Batch- Vehicle";
	protected final static String ADDRESS_SHEET_NAME = "Batch- Address";
	protected final static String DRIVER_SHEET_NAME = "Batch- Driver";
	protected final static String COVERAGE_SHEET_NAME = "Batch- Coverage";
	protected final static String ENVIRONMENT_SHEET_NAME = "Environment";
	protected final static String TESTS_SHEET_NAME = "Tests";*/

	protected List<OpenLPolicy> openLPolicies;

	public List<OpenLPolicy> getPolicies() {
		return openLPolicies;
	}

	public abstract OpenLFileParser parse(File openLtestsFile);

	public abstract TestData generateTestData(OpenLPolicy openLPolicy);

	public abstract Dollar getFinalPremium(int policyNumber);
}
