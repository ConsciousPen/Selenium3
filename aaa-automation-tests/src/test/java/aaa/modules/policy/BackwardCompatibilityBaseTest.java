package aaa.modules.policy;

import static toolkit.verification.CustomAssertions.assertThat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.testng.SkipException;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import com.exigen.ipb.etcsa.utils.batchjob.JobGroup;
import com.exigen.ipb.etcsa.utils.batchjob.SoapJobActions;
import com.exigen.ipb.etcsa.utils.batchjob.ws.model.WSJobSummary;
import aaa.common.pages.SearchPage;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.main.modules.billing.account.BillingAccount;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.bct.BctType;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;

public class BackwardCompatibilityBaseTest extends PolicyBaseTest {
	protected static ConcurrentHashMap<List<String>, List<Map<String, String>>> queryResult = new ConcurrentHashMap<>();

	public static final String SELECT_POLICY_QUERY_TYPE = "SelectPolicy";

	private static final String SELECT_ALL_FROM_JOB_SUMMARY = "Select * from JobSummary WHERE JOBNAME like '%s' AND STARTED like '%s' order by ENDED DESC";
	private static final String ddMMyy = "dd-MMM-yy";
	private static final String PRE_VALIDATION = "PreValidation";
	private static final String POST_VALIDATION = "PostValidation";

	public BillingAccount billingAccount = new BillingAccount();

	protected BctType getBctType() {
		return BctType.ONLINE_TEST;
	}

	/**
	 * Execute job and calculate failure percentage.
	 * if % of failed tasks > 5% hit production team or/and create a defect
	 * if job processed 0 items, it is case for investigation
	 * @param job
	 */
	protected void executeBatchTest(Job job){
		JobGroup jobGroup = JobGroup.fromSingleJob(JobUtils.convertToIpb(job));
		SoapJobActions soapJobActions = new SoapJobActions();

		if(soapJobActions.isJobExist(jobGroup)){
			soapJobActions.createJob(jobGroup);
		}
		soapJobActions.startJob(jobGroup);
		WSJobSummary latestJobRun = JobUtils.getLatestJobRun(jobGroup);

		assertThat(latestJobRun.getTotalItems()).as("totalItems picked up by job should be > 0").isGreaterThan(0);
		verifyErrorsCountLessFivePercents(latestJobRun);

	}

	protected void createAndExecuteJob(Job job){
		JobGroup jobGroup = JobGroup.fromSingleJob(JobUtils.convertToIpb(job));
		SoapJobActions service = new SoapJobActions();

		service.createJob(jobGroup);

		JobUtils.executeJob(jobGroup);
	}

	/**
	 * @param wsJobSummary the Job summary
	 * @return true in case if % of failures < 5% from total amount of tasks
	 */
	private void verifyErrorsCountLessFivePercents(WSJobSummary wsJobSummary) {
		double percentage = (double) wsJobSummary.getTotalFailure() / (double) wsJobSummary.getTotalItems() * 100 ;
		log.info("Job processed \n totalItems: {}\n totalFailure: {}\n failed is {}% from total", wsJobSummary.getTotalItems(), wsJobSummary.getTotalFailure(), percentage);
		assertThat(new BigDecimal(percentage).setScale(2, RoundingMode.HALF_UP).doubleValue()).describedAs("Number of failed tasks is more then 5 percents").isLessThan(5);
	}

	protected List<String> getPoliciesByQuery(String testName, String queryName) {
		return getPoliciesFromQuery(getQueryResult(testName, queryName), queryName);
	}

	public List<String> getEmptyEndorsementPolicies(String testName, String startRangeDate, String endRangeDate,String state) {
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue(SELECT_POLICY_QUERY_TYPE);
		query = query.replace("/DATE1/", startRangeDate);
		query = query.replace("/DATE2/", endRangeDate);
		query = query.replace("/STATE/", state);
		query = query.replace("/AND_ROWNUM_3/", "AND ROWNUM <=1");

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

}
