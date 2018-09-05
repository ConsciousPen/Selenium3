package aaa.modules.bct;

import static toolkit.verification.CustomAssertions.assertThat;
import java.lang.reflect.Method;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import com.exigen.ipb.etcsa.utils.Dollar;
import com.exigen.ipb.etcsa.utils.TimeSetterUtil;
import aaa.common.pages.SearchPage;
import aaa.config.CsaaTestProperties;
import aaa.helpers.jobs.Job;
import aaa.helpers.jobs.JobUtils;
import aaa.main.modules.policy.IPolicy;
import aaa.main.modules.policy.PolicyType;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.BaseTest;
import toolkit.config.PropertyProvider;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.db.DBService;
import toolkit.verification.CustomSoftAssertions;

public class BackwardCompatibilityBaseTest extends BaseTest {

	private String date1 = "Date1 isn't specified";
	private String date2 = "Date2 isn't specified";

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

	@DataProvider(name = "getPoliciesForEmptyEndorsementTests", parallel = true)
	public Iterator<Object[]> getPolicyNumbersFromDB(Method m) {
		String state = context.getCurrentXmlTest().getAllParameters().get("state");
		if(state == null){
			state = PropertyProvider.getProperty(CsaaTestProperties.TEST_USSTATE);
		}
		log.info(" DataProvider got state: {}", state);
		List<String> policyNumbers = getPoliciesForEmptyEndorsementTests(m.getName(), date1, date2);
		log.info(" DataProvider got policies: {}", policyNumbers);
		String finalState = state;
		List<Object[]> data = policyNumbers.stream().map(policy -> new String[] {finalState, policy}).collect(Collectors.toList());
		return data.iterator();
	}

	private String getCUSTOM_DATE1(String date1) {
		if (!PropertyProvider.getProperty(CsaaTestProperties.CUSTOM_DATE1).isEmpty()) {
			date1 = PropertyProvider.getProperty(CsaaTestProperties.CUSTOM_DATE1);
		}
		return date1;
	}

	private String getCUSTOM_DATE2(String date2) {
		if (!PropertyProvider.getProperty(CsaaTestProperties.CUSTOM_DATE2).isEmpty()) {
			date2 = PropertyProvider.getProperty(CsaaTestProperties.CUSTOM_DATE2);
		}
		return date2;
	}

	public List<String> getPoliciesForEmptyEndorsementTests(String testName, String date1, String date2) {
		date1 = getCUSTOM_DATE1(date1);
		date2 = getCUSTOM_DATE2(date2);

		return getEmptyEndorsementPolicies(testName, date1, date2);
		//return getPoliciesWithDateRangeByQuery(testName, date1, date2).get(0);
	}

	protected List<String> getPoliciesByQuery(String testName, String queryName) {
		return getPoliciesFromQuery(getQueryResult(testName, queryName), queryName);
	}

	public List<String> getEmptyEndorsementPolicies(String testName, String startRangeDate, String endRangeDate) {
		String query = testDataManager.bct.get(getBctType()).getTestData(testName).getValue("SelectPolicy");
		query = query.replace("/DATE1/", startRangeDate);
		query = query.replace("/DATE2/", endRangeDate);
		/* have no idea why this replacements here*/
		query = query.replace("pasadm.", "");
		query = query.replace("PASADM.", "");


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
