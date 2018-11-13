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
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.fasterxml.jackson.dataformat.yaml.snakeyaml.Yaml;
import com.google.common.base.Joiner;
import toolkit.utils.buglist.Bug;
import toolkit.utils.buglist.YAMLBugListReader;

public class BuglistUtils {
	private static final String PATH_BUGLIST = "src/test/resources/buglist.yaml";
	protected static Logger log = LoggerFactory.getLogger(BuglistUtils.class);

	public static void removeClosed() throws ExecutionException, InterruptedException, IOException {
		File bugListFile = new File(FilenameUtils.separatorsToSystem(PATH_BUGLIST));
		List<Bug> bugsFromFile = new YAMLBugListReader().loadBugs(bugListFile);

		// Get all defect id from buglist.yaml
		Set<String> bugListIds = bugsFromFile.stream().map(Bug::getId).collect(Collectors.toCollection(() -> new HashSet<>(bugsFromFile.size())));

		// Get status for defects from Jira
		String jqlQuery = "key in (" + Joiner.on(",").join(bugListIds) + ")"
				+ " and status in (Resolved, Closed, Delivered, Passed, Done, Ready)";

		Iterable<Issue> foundIssues = JiraUtils.getClient().getSearchClient().searchJql(jqlQuery).get().getIssues();
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
}
