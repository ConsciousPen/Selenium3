package aaa.modules.bct;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.testng.SkipException;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.CustomSoftAssertions;

public class BackwardCompatibilityBaseTest extends BaseTest {

	protected static ConcurrentHashMap<List<String>, List<Map<String, String>>> queryResult = new ConcurrentHashMap<>();

	protected BctType getBctType() {
		return BctType.ONLINE_TEST;
	}

	protected void executeBatchTest(String name, Job job) {
		List<String> preKey = Collections.unmodifiableList(Arrays.asList(name, "PreValidation"));
		synchronized (name) {
			if (!queryResult.containsKey(preKey)) {
				queryResult.put(preKey, getQueryResult(name, "PreValidation"));
			}
		}
		List<String> foundPolicies = getPoliciesFromQuery(queryResult.get(preKey), "PreValidation");

//		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(job);

		List<String> postKey = Collections.unmodifiableList(Arrays.asList(name, "PostValidation"));
		synchronized (name) {
			if (!queryResult.containsKey(postKey)) {
				queryResult.put(postKey, getQueryResult(name, "PostValidation"));
			}
		}
		List<String> processedPolicies = getPoliciesFromQuery(queryResult.get(postKey), "PostValidation");

		CustomSoftAssertions.assertSoftly(softly -> {
			foundPolicies.forEach(policy -> assertThat(processedPolicies).as("Policy " + policy + " was processed by " + job.getJobName()).contains(policy));
		});
	}

	protected List<String> getPoliciesByQuery(String testName, String queryName) {
		return getPoliciesFromQuery(getQueryResult(testName, queryName), queryName);
	}

	public List<String> getPoliciesWithDateRangeByQuery(String testName, String date1, String date2) {
		String executionDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern("dd-MMM-yy"));
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue("SelectPolicy");
		query = query.replace("/EXECDATE/", executionDate);
		if(!testName.toLowerCase().contains("endorsement")){
			query = query.replace("/STATE/", getState());
		}
		query = query.replace("pasadm.", "");
		query = query.replace("PASADM.", "");
		query = query.replace("/DATE1/", date1);
		query = query.replace("/DATE2/", date2);

		return getPoliciesFromQuery(DBService.get().getRows(query), "SelectPolicy");
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
		switch (queryName) {
			case "PreValidation":
				if (policies.size() == 0) {
					log.error("No policies found by '" + queryName + "' query");
					throw new SkipException("No policies found by '" + queryName + "' query");
				}
				break;
			case "PostValidation":
				assertThat(policies).as("No policies found by '" + queryName + "' query").isEmpty();
				break;
			default:
				if (policies.size() == 0) {
					log.error("No policies found by '" + queryName + "' query");
					throw new SkipException("No policies found by '" + queryName + "' query");
				}
		}
		log.info("Policies found by '" + queryName + "' query: " + policies);

		return policies;
	}

	protected Dollar getPreEndorsementPremium(IPolicy ipolicy, String policyNumber) {
		mainApp().open();
		SearchPage.openPolicy(policyNumber);
		deletePendingTransaction(ipolicy);
		Dollar policyPremium = PolicySummaryPage.TransactionHistory.getEndingPremium();
		log.info(String.format("Pre-Endorsement Premium: '%s'", policyPremium));
		return policyPremium;
	}

	protected void deletePendingTransaction(IPolicy policy) {
		if (PolicySummaryPage.buttonPendedEndorsement.isEnabled()) {
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.deletePendedTransaction().perform(new SimpleDataProvider());
		}
	}

	protected void deletePendingRenewals(IPolicy policy) {
		if (PolicySummaryPage.buttonRenewals.isEnabled()) {
			PolicySummaryPage.buttonRenewals.click();
			policy.deletePendingRenwals().perform(new SimpleDataProvider());
		}
	}

	protected IPolicy findAndOpenPolicy(String queryName, PolicyType policyType) {
		mainApp().open();
		String policyNumber = getPoliciesByQuery(queryName, "SelectPolicy").get(0);
		log.info(String.format("Policy #%s has been selected for test using %s query", policyNumber, queryName));
		IPolicy policy = policyType.get();
		SearchPage.openPolicy(policyNumber);
		return policy;
	}

}
