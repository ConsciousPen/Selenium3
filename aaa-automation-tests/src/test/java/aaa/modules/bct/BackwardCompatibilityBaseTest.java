package aaa.modules.bct;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.testng.SkipException;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.enums.JobResultEnum;
import aaa.common.pages.SearchPage;
import aaa.helpers.http.HttpJob;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.billing.account.actiontabs.AcceptPaymentActionTab;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PolicyBaseTest;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.CustomSoftAssertions;

public class BackwardCompatibilityBaseTest extends PolicyBaseTest {
	protected static ConcurrentHashMap<List<String>, List<Map<String, String>>> queryResult = new ConcurrentHashMap<>();

	public static final String SELECT_POLICY_QUERY_TYPE = "SelectPolicy";

	public BillingAccount billingAccount = new BillingAccount();
	public AcceptPaymentActionTab paymentTab = new AcceptPaymentActionTab();

	protected BctType getBctType() {
		return BctType.ONLINE_TEST;
	}

	protected void executeBatchTest(Job job){
		JobUtils.executeJob(job);

		String result = null;
		try {
			result = HttpJob.getJobProcessedStatistic(job.getJobName());
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> temp = Arrays.asList(result.toString().replace(",", "").replace(".", "").split(" "));

		HashMap<String, String> splittedRow = new HashMap<>();
		splittedRow.put(JobResultEnum.JobStatisticsConstants.DATE, temp.get(0));
		splittedRow.put(JobResultEnum.JobStatisticsConstants.TIME, temp.get(1));
		splittedRow.put(JobResultEnum.JobStatisticsConstants.PROCESSED_COUNT, temp.get(7));
		splittedRow.put(JobResultEnum.JobStatisticsConstants.SUCCESS_COUNT, temp.get(9));
		splittedRow.put(JobResultEnum.JobStatisticsConstants.ERROR_COUNT, temp.get(11));

		long processedCount = Long.parseLong(splittedRow.get(JobResultEnum.JobStatisticsConstants.PROCESSED_COUNT));
		long successCount = Long.parseLong(splittedRow.get(JobResultEnum.JobStatisticsConstants.PROCESSED_COUNT));
		long errorCount = Long.parseLong(splittedRow.get(JobResultEnum.JobStatisticsConstants.PROCESSED_COUNT));
		boolean erorrsCountLessOfFivePercents = false;

		long percentage = 0;
		if(processedCount > 0){
			if(errorCount > 0){
				percentage = errorCount * 100 / processedCount ;
				erorrsCountLessOfFivePercents = percentage > 5; // false if > 5% of errors
			}
			erorrsCountLessOfFivePercents = true; // if processed count > 0 and errorCount 0
		}else {
			erorrsCountLessOfFivePercents = false; // if processed count = 0 or job failed.. or ..
		}

		assertThat(erorrsCountLessOfFivePercents).as(String.format("Errors %s from Processed Count %s is %d ",errorCount,processedCount,percentage)).isEqualTo(true);
	}

	protected void executeBatchTestWithQueries(String name, Job job) {
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

	public List<String> getEmptyEndorsementPolicies(String testName, String startRangeDate, String endRangeDate, String state) {
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue(SELECT_POLICY_QUERY_TYPE);
		query = query.replace("/DATE1/", startRangeDate);
		query = query.replace("/DATE2/", endRangeDate);
		query = query.replace("/STATE/", state);

		return getPoliciesFromQuery(DBService.get().getRows(query), SELECT_POLICY_QUERY_TYPE);
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

	public List<String> getPoliciesFromQuery(List<Map<String, String>> queryResult, String queryName) {
		List<String> policies = queryResult.stream()
				.filter(map -> (!map.containsKey("RISKSTATECD") || map.get("RISKSTATECD").equals(getState())) && (!map.containsKey("RISKSTATE") || map.get("RISKSTATE").equals(getState())))
				.map(map -> map.get("POLICYNUMBER")).collect(Collectors.toList());
		switch (queryName) {
			case "PreValidation":
				if (policies.isEmpty()) {
					log.error("No policies found by '{}' query", queryName);
					throw new SkipException("No policies found by '" + queryName + "' query");
				}
				break;
			case "PostValidation":
				assertThat(policies).as("No policies found by '" + queryName + "' query").isEmpty();
				break;
			default:
				if (policies.isEmpty()) {
					log.error("No policies found by '{}' query", queryName);
					throw new SkipException("No policies found by '" + queryName + "' query");
				}
		}
		log.info("Policies found by '{}' query: {}", queryName, policies);

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
		String policyNumber = getPoliciesByQuery(queryName, SELECT_POLICY_QUERY_TYPE).get(0);
		mainApp().open();
		log.info(String.format("Policy #%s has been selected for test using %s query", policyNumber, queryName));
		IPolicy policy = policyType.get();
		SearchPage.openPolicy(policyNumber);
		return policy;
	}

	public String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}

}
