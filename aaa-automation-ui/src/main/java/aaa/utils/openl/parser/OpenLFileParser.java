package aaa.utils.openl.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.exigen.ipb.etcsa.utils.Dollar;
import aaa.utils.openl.model.OpenLPolicy;
import aaa.utils.openl.testdata_builder.OpenLTestDataBuilder;
import toolkit.datax.TestData;

public abstract class OpenLFileParser<P extends OpenLPolicy, T extends OpenLTestDataBuilder<P>> {
	protected static final String POLICY_SHEET_NAME = "Batch- Policy";
	/*protected final static String CAPPING_DETAILS_SHEET_NAME = "Batch- CappingDetails";
	protected final static String VEHICLE_SHEET_NAME = "Batch- Vehicle";
	protected final static String ADDRESS_SHEET_NAME = "Batch- Address";
	protected final static String DRIVER_SHEET_NAME = "Batch- Driver";
	protected final static String COVERAGE_SHEET_NAME = "Batch- Coverage";
	protected final static String ENVIRONMENT_SHEET_NAME = "Environment";
	protected final static String TESTS_SHEET_NAME = "Tests";*/

	protected Map<Integer, P> openLPolicies;
	protected Map<Integer, Dollar> finalPremiums;
	protected T testDataBuilder;

	public OpenLFileParser(T testDataBuilder) {
		this.testDataBuilder = testDataBuilder;
	}

	public List<P> getPolicies() {
		return new ArrayList<>(this.openLPolicies.values());
	}

	public TestData generateTestData(P openLPolicy) {
		return testDataBuilder.buildRatingData(openLPolicy);
	}


	/*public P getPolicy(int number) {
		return getPolicies().get(number);
	}*/

	public Dollar getFinalPremium(P openLPolicy) {
		return this.finalPremiums.get(openLPolicy.getNumber());
	}

	public abstract OpenLFileParser parse(File openLtestsFile);
}
