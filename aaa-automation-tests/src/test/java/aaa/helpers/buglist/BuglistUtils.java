package aaa.helpers.buglist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.rest.client.api.RestClientException;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.common.base.Joiner;
import toolkit.utils.buglist.Bug;
import toolkit.utils.buglist.YAMLBugListReader;

public class BuglistUtils {
	private static final String PATH_BUGLIST = "src/test/resources/buglist.yaml";
	private static Logger log = LoggerFactory.getLogger(BuglistUtils.class);

	/**
	 * broken until https://bitbucket.org/atlassian/jira-rest-java-client/pull-requests/67/jrjc-233-fixed-deprecated-method/diff
	 * not merged
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void removeClosed() throws ExecutionException, InterruptedException, IOException {
		File bugListFile = new File(FilenameUtils.separatorsToSystem(PATH_BUGLIST));
		List<Bug> bugsFromFile = new YAMLBugListReader().loadBugs(bugListFile);

		// Get all defect id from buglist.yaml
		Set<String> bugListIds = bugsFromFile.stream().map(Bug::getId).collect(Collectors.toCollection(() -> new HashSet<>(bugsFromFile.size())));

		// Get status for defects from Jira
		String jqlQuery = "key in (" + Joiner.on(",").join(bugListIds) + ")"
				+ " and status in (Resolved, Closed, Delivered, Passed, Done, Ready)";

		Iterable<Issue> foundIssues = null;
		try{
			foundIssues = JiraUtils.getClient().getSearchClient().searchJql(jqlQuery).get().getIssues();
		}catch (Exception e){
			if(e.getCause() instanceof RestClientException){
				RestClientException restClientException = (RestClientException) e.getCause();
				throw new RestClientException("RestClientException: autorization settings, HTTP Status Code " + restClientException.getStatusCode().get(),e.getCause());
			}else {
				e.printStackTrace();
			}
		}
		// delete and print(Closed, Delivered, Passed, Done, Ready) ids from bugListIds
		for (Issue defect : foundIssues) {
			String defectStatus = defect.getStatus().getName();
			if (!"Resolved".equalsIgnoreCase(defectStatus)) {
				log.info("Removed from buglist \n {} : {}, Summary: {}", defect.getKey(), defectStatus, defect.getSummary());
				bugListIds.remove(defect.getKey());
			} else {
				log.info("{} : {}, Summary: {}", defect.getKey(), defectStatus, defect.getSummary());
			}
		}
		List<Bug> toDelete = bugsFromFile.stream().filter(bug -> !bugListIds.contains(bug.getId())).collect(Collectors.toList());

		bugsFromFile.removeAll(toDelete);
		// Generate clear buglist
		Yaml refreshedBugList = new Yaml();
		FileWriter writer = new FileWriter(PATH_BUGLIST);

		writer.write(refreshedBugList.dumpAll(getBuglistModel(bugsFromFile).iterator()).replace("---", ""));
		writer.close();
	}

	//todo add remove duplicates.

	static List<Map<String, Map<String, Object>>> getBuglistModel(List<Bug> buglist) {
		List<Map<String, Map<String, Object>>> buglistContent = new ArrayList<>();

		buglistContent.add(buglistItemExample());

		for (Bug bug : buglist) {
			Map<String, Map<String, Object>> buglistItem = new LinkedHashMap<>();

			Map<String, Object> defectBody = new HashMap<>();

			defectBody.put("summary", bug.getSummary());
			defectBody.put("reason", bug.getFailureReason());

			List<Map<String, String[]>> failuresList = new ArrayList<>();
			for (Pair<Set<String>, Set<String>> failure : bug.getFailures()) {
				Map<String, String[]> failures = new LinkedHashMap<>();
				String[] tests = failure.getLeft().toArray(new String[1]);
				String[] messages = failure.getRight().toArray(new String[1]);
				failures.put("tests", tests);
				failures.put("messages", messages);

				failuresList.add(failures);
			}
			defectBody.put("failures", failuresList);

			buglistItem.put(bug.getId(), defectBody);

			buglistContent.add(buglistItem);
		}

		return buglistContent;
	}

	private static Map<String, Map<String, Object>> buglistItemExample() {
		Map<String, Map<String, Object>> buglistItem = new LinkedHashMap<>();

		Map<String, Object> defectBody = new HashMap<>();

		defectBody.put("summary", "[TO_BE_CREATED] Maximum allowable length of 'Underwriting Company' criteria warning is incorrect on Search page");
		defectBody.put("reason", "PROD");

		List<Map<String, String[]>> failuresList = new ArrayList<>();

		Map<String, String[]> failures = new LinkedHashMap<>();
		String[] tests = new String[] {"[aaa.modules.regression.common.TestSearchPageFillingAndWarnings.testSearchWarnings]"};
		String[] messages = new String[] {"[\"Unexpeted warning message(s) is(are) present on search page: [Maximum criteria length for 'Underwriting Company #' is 255]\"]"};
		failures.put("tests", tests);
		failures.put("messages", messages);

		failuresList.add(failures);
		defectBody.put("failures", failuresList);

		buglistItem.put("EISDEV-XXXXX1", defectBody);

		return 	buglistItem;
	}
}
