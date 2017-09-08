package aaa.modules.bct;

import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.modules.BaseTest;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import org.testng.SkipException;
import toolkit.db.DBService;
import toolkit.verification.CustomAssert;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BackwardCompatibilityBaseTest extends BaseTest {

	protected BctType getBctType() {
		return null;
	}

	protected void executeBatchTest(String name, Job job) {
		List<String> foundPolicies = getPoliciesByQuery(name, "PreValidation");

		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(job);

		List<String> processedPolicies = getPoliciesByQuery(name, "PostValidation");
		CustomAssert.enableSoftMode();
		foundPolicies.forEach(policy -> CustomAssert.assertTrue("Policy " + policy + " was processed by " + job.getJobName(), processedPolicies.contains(policy)));
		CustomAssert.disableSoftMode();
	}

	protected List<String> getPoliciesByQuery(String testName, String queryName) {
		String executionDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue(queryName);
		query = query.replace("/EXECDATE/", executionDate);
		query = query.replace("/STATE/", getState());
		query = query.replace("pasadm.", "");

		List<Map<String, String>> queryResult = DBService.get().getRows(query);
		List<String> policies = queryResult.stream()
				.filter(map -> !map.containsKey("RISKSTATECD") || map.get("RISKSTATECD").equals(getState()))
				.map(map -> map.get("POLICYNUMBER")).collect(Collectors.toList());
		if (policies.size()==0) {
			throw new SkipException("No policies found by '" + queryName + "' query");
		}
		log.info("Found policies: " + policies.toString());

		return policies;
	}
}
