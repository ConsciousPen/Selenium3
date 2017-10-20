package aaa.modules.bct;

import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import oracle.net.aso.q;
import org.apache.xpath.operations.Bool;
import org.testng.SkipException;
import toolkit.db.DBService;
import toolkit.verification.CustomAssert;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BackwardCompatibilityBaseTest extends BaseTest {

	protected static ConcurrentHashMap<List<String>, List<Map<String, String>>> queryResult = new ConcurrentHashMap<>();

	protected BctType getBctType() {
		return BctType.ONLINE_TEST;
	}

	protected void executeBatchTest(String name, Job job) {
		List<String> preKey = Collections.unmodifiableList(Arrays.asList(name, "PreValidation"));
		getQueryOnce(preKey);
		List<String> foundPolicies = getPoliciesFromQuery(queryResult.get(preKey), "PreValidation");

//		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(job);

		List<String> postKey = Collections.unmodifiableList(Arrays.asList(name, "PostValidation"));
		getQueryOnce(postKey);
		List<String> processedPolicies = getPoliciesFromQuery(queryResult.get(postKey), "PostValidation");

		CustomAssert.enableSoftMode();
		foundPolicies.forEach(policy -> CustomAssert.assertTrue("Policy " + policy + " was processed by " + job.getJobName(), processedPolicies.contains(policy)));
		CustomAssert.disableSoftMode();
	}

	protected List<String> getPoliciesByQuery(String testName, String queryName) {
		return getPoliciesFromQuery(getQueryResult(testName, queryName), queryName);
	}

	private List<Map<String, String>> getQueryResult(String testName, String queryName) {
		String executionDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue(queryName);
		query = query.replace("/EXECDATE/", executionDate);
		query = query.replace("/STATE/", getState());
		query = query.replace("pasadm.", "");
		query = query.replace("PASADM.", "");

		return DBService.get().getRows(query);
	}

	private List<String> getPoliciesFromQuery(List<Map<String, String>> queryResult, String queryName) {
		List<String> policies = queryResult.stream()
				.filter(map -> (!map.containsKey("RISKSTATECD") || map.get("RISKSTATECD").equals(getState())) && (!map.containsKey("RISKSTATE") || map.get("RISKSTATE").equals(getState())))
				.map(map -> map.get("POLICYNUMBER")).collect(Collectors.toList());
		if (policies.size()==0) {
			log.error("No policies found by '" + queryName + "' query");
			throw new SkipException("No policies found by '" + queryName + "' query");
		}
		log.info("Policies found by '" + queryName + "' query: " + policies.toString());

		return policies;
	}

	private synchronized void getQueryOnce(List<String> key) {
		log.info(String.format("Query verification started for test %s query %s state %s", key.get(0), key.get(1), getState()));
		if (!queryResult.containsKey(key)) {
			log.info(String.format("Query requestingstarted for test %s query %s state %s", key.get(0), key.get(1), getState()));
			queryResult.put(key, getQueryResult(key.get(0), key.get(1)));
		}
		log.info(String.format("Query verification completed for test %s query %s state %s", key.get(0), key.get(1), getState()));
	}
}
