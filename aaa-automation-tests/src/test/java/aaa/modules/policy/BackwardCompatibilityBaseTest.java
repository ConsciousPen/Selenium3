package aaa.modules.policy;

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
import aaa.helpers.http.BackendJobNames;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BctType;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.CustomSoftAssertions;

public class BackwardCompatibilityBaseTest extends PolicyBaseTest {
	protected static ConcurrentHashMap<List<String>, List<Map<String, String>>> queryResult = new ConcurrentHashMap<>();

	public static final String SELECT_POLICY_QUERY_TYPE = "SelectPolicy";

	private static final String SELECT_ALL_FROM_JOB_SUMMARY = "Select * from JobSummary WHERE JOBNAME like '%s' AND STARTED like '%s' AND ENDED like '%s' order by ENDED DESC";
	private static final String ddMMyy = "dd-MMM-yy";
	private static final String PRE_VALIDATION = "PreValidation";
	private static final String POST_VALIDATION = "PostValidation";

	public BillingAccount billingAccount = new BillingAccount();

	protected BctType getBctType() {
		return BctType.ONLINE_TEST;
	}

	/**
	 * Execute job and calculate failure percentage, if % of failed tasks > 5% hit production team or/and create a defect
	 * @param job
	 */
	protected void executeBatchTest(Job job){
		// Get sql compatible job name, based on parameter
		String backEndJobName = BackendJobNames.getBackEndJobNames(job.getJobName());
		// Get job start date
		String startDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern(ddMMyy)).toUpperCase();

		JobUtils.executeJob(job);

		// Get job finish date
		String endedDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern(ddMMyy)).toUpperCase();
		String query = String.format(SELECT_ALL_FROM_JOB_SUMMARY, "%" + backEndJobName + "%", startDate + "%", endedDate + "%");
		// Verify that failure % is below 5%
		assertThat(getFailurePercentage(backEndJobName, query)).as("Percentage of failed tasks is more 5%").isEqualTo(true);
	}

	protected List<String> getPoliciesByQuery(String testName, String queryName) {
		return getPoliciesFromQuery(getQueryResult(testName, queryName), queryName);
	}

	public List<String> getEmptyEndorsementPolicies(String testName, String startRangeDate, String endRangeDate,String state) {
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue(SELECT_POLICY_QUERY_TYPE);
		query = query.replace("/DATE1/", startRangeDate);
		query = query.replace("/DATE2/", endRangeDate);
		query = query.replace("/STATE/", state);
		query = query.replace("/AND_ROWNUM_3/", "and rownum = 1");

		return getPoliciesFromQuery(DBService.get().getRows(query), SELECT_POLICY_QUERY_TYPE);
	}

	private List<Map<String, String>> getQueryResult(String testName, String queryName) {
		String executionDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern(ddMMyy));
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue(queryName);
		query = query.replace("/EXECDATE/", executionDate);
		query = query.replace("/STATE/", getState());
		query = query.replace("pasadm.", "");
		query = query.replace("PASADM.", "");
		query = query.replace("/AND_ROWNUM_1/", " and rownum = 1");

		return DBService.get().getRows(query);
	}

	protected List<String> getPoliciesWithDateRangeByQuery(String testName, String date1, String date2) {
		String executionDate = TimeSetterUtil.getInstance().getCurrentTime().format(DateTimeFormatter.ofPattern(ddMMyy));
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue(SELECT_POLICY_QUERY_TYPE);
		query = query.replace("/EXECDATE/", executionDate);
		query = query.replace("/STATE/", getState());
		query = query.replace("pasadm.", "");
		query = query.replace("PASADM.", "");
		query = query.replace("/DATE1/", date1);
		query = query.replace("/DATE2/", date2);

		return getPoliciesFromQuery(DBService.get().getRows(query), "SelectPolicy");
	}

	public List<String> getPoliciesFromQuery(List<Map<String, String>> queryResult, String queryName) {
		List<String> policies = queryResult.stream()
				.filter(map -> (!map.containsKey("RISKSTATECD") || map.get("RISKSTATECD").equals(getState())) && (!map.containsKey("RISKSTATE") || map.get("RISKSTATE").equals(getState())))
				.map(map -> map.get("POLICYNUMBER")).collect(Collectors.toList());
		switch (queryName) {
			case PRE_VALIDATION:
				if (policies.isEmpty()) {
					log.error("No policies found by '{}' query", queryName);
					throw new SkipException("No policies found by '" + queryName + "' query");
				}
				break;
			case POST_VALIDATION:
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
		if (PolicySummaryPage.buttonPendedEndorsement.isPresent() && PolicySummaryPage.buttonPendedEndorsement.isEnabled()) {
			PolicySummaryPage.buttonPendedEndorsement.click();
			policy.deletePendedTransaction().perform(new SimpleDataProvider());
		}

		// if policy found and was opened at the Renewals action tab
		if(PolicySummaryPage.buttonBackFromRenewals.isPresent() && PolicySummaryPage.buttonBackFromRenewals.isEnabled()){
			PolicySummaryPage.buttonBackFromRenewals.click();
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
		String policyNumber = getPoliciesByQuery(queryName, SELECT_POLICY_QUERY_TYPE).get(0);
		log.info(String.format("Policy #%s has been selected for test using %s query", policyNumber, queryName));
		IPolicy policy = policyType.get();
		SearchPage.openPolicy(policyNumber);
		return policy;
	}

	public String getMethodName() {
		return Thread.currentThread().getStackTrace()[2].getMethodName();
	}

	private Boolean getFailurePercentage(String backEndJobName, String sql) {
		Boolean failurePercentageExceeded;
		Map<String, String> lastJobResult = DBService.get().getRow(sql);
		assertThat(lastJobResult).as(String.format("Results for %s were not found in database", backEndJobName)).isNotEmpty();

		long processedCount = Long.parseLong(lastJobResult.get("TOTALITEMS"));
		long successCount = Long.parseLong(lastJobResult.get("TOTALSUCCESS"));
		long errorCount = Long.parseLong(lastJobResult.get("TOTALFAIL"));

		if(processedCount == 0){
			failurePercentageExceeded = true;
		}
		else{
			failurePercentageExceeded = isErrorsCountLessFivePercents(processedCount, errorCount);
		}
		return failurePercentageExceeded;
	}

	private boolean isErrorsCountLessFivePercents(long processedCount, long errorCount) {
		boolean erorrsCountLessOfFivePercents = false;
		long percentage = 0;
		if(processedCount > 0){
			if(errorCount > 0){
				percentage = (errorCount * 100)/processedCount ;
				erorrsCountLessOfFivePercents = percentage > 5; // false if > 5% of errors
			}
			erorrsCountLessOfFivePercents = true; // if processed count > 0 and errorCount 0
		}else {
			erorrsCountLessOfFivePercents = false; // if processed count = 0 or job failed.. or ..
		}

		log.info("Job processed items count {}\nErrors count {}\nPercent of failed items {}",errorCount,processedCount,percentage);
		return erorrsCountLessOfFivePercents;
	}

	@Deprecated
	protected void executeBatchTestWithQueries(String name, Job job) {
		List<String> preKey = Collections.unmodifiableList(Arrays.asList(name, PRE_VALIDATION));
		synchronized (name) {
			if (!queryResult.containsKey(preKey)) {
				queryResult.put(preKey, getQueryResult(name, PRE_VALIDATION));
			}
		}
		List<String> foundPolicies = getPoliciesFromQuery(queryResult.get(preKey), PRE_VALIDATION);

		//		TimeSetterUtil.getInstance().nextPhase(TimeSetterUtil.getInstance().getCurrentTime());
		JobUtils.executeJob(job);

		List<String> postKey = Collections.unmodifiableList(Arrays.asList(name, POST_VALIDATION));
		synchronized (name) {
			if (!queryResult.containsKey(postKey)) {
				queryResult.put(postKey, getQueryResult(name, POST_VALIDATION));
			}
		}
		List<String> processedPolicies = getPoliciesFromQuery(queryResult.get(postKey), POST_VALIDATION);

		CustomSoftAssertions.assertSoftly(softly -> {
			foundPolicies.forEach(policy -> assertThat(processedPolicies).as("Policy " + policy + " was processed by " + job.getJobName()).contains(policy));
		});
	}
}
