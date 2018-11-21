package aaa.helpers.buglist;

import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

public class JiraUtils {

	public static String url = PropertyProvider.getProperty(CsaaTestProperties.JIRA_URL, "https://csaaig.atlassian.net");
	public static String username = PropertyProvider.getProperty(CsaaTestProperties.JIRA_LOGIN);
	public static String password = PropertyProvider.getProperty(CsaaTestProperties.JIRA_PASSWORD);

	protected static Logger log = LoggerFactory.getLogger(JiraUtils.class);
	public static JiraRestClient client;

	public static JiraRestClient getClient() {
		if (client == null) {
			try {
				client = new AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(new URI(url), username, password);
				return client;
			} catch (Exception e) {
				log.error("JiraRestClient: Jira is unavailable or unable to connect Username: \"{}\", Password: \"{}\"" , username, password, e);
				return null;
			}
		} else {
			return client;
		}
	}

	/**
	 * @param issueId
	 * @return issue status
	 */
	public static String getIssueStatus(String issueId) {
		try {
			return getClient().getIssueClient().getIssue(issueId).get().getStatus().getName();
		} catch (Exception e) {
			return "";
		}
	}
}
