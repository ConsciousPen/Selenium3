package aaa.helpers.logs;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

/**
 * PasAppLogGrabber is used to take pas-app wrapper log
 * @author Mantas Garsvinskas
 */
public class PasAppLogGrabber {

    private static String pasAppLogFolderPrefix = PropertyProvider.getProperty(CsaaTestProperties.PAS_APP_LOG_FOLDER, "/AAA/tcserver/pivotal-tc-server-developer-3.2.8.RELEASE/");
    private static final String PAS_APP_LOG = "%Spas-app/logs";

    public static String getPasAppLogFolder() {
        return getFormattedFolderPathForPasAppLog(PAS_APP_LOG);
    }

    private static String getFormattedFolderPathForPasAppLog(String template) {
        return String.format(template, pasAppLogFolderPrefix);
    }

    /*
    * Method returns all claims analytics rows as List<String> items from pas-app wrapper log
    */
    public List<String> retrieveClaimsAnalyticsLogValues(String appLog) {
        List<String> matches = new ArrayList<>();
        String regexp = "\\{\"claims-assignment\":(.*)\"}}";

        Matcher m = Pattern.compile(regexp).matcher(appLog);

        while (m.find()) {
            matches.add(m.group(0));
        }

        assertThat(matches).as("Log doesn't contain any Claim Analytic values").isNotEmpty();
        return matches;

    }

}
