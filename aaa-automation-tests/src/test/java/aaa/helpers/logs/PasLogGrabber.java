package aaa.helpers.logs;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

/**
 * PasLogGrabber is used to take selected (App, Admin, etc.) PAS wrapper log
 * @author Mantas Garsvinskas
 */
public class PasLogGrabber {

    private static String pasLogFolderPrefix = PropertyProvider.getProperty(CsaaTestProperties.PAS_LOG_FOLDER, "/AAA/tcserver/pivotal-tc-server-developer-3.2.8.RELEASE/");
    private static final String PAS_APP_LOG = "%Spas-app/logs";
    private static final String PAS_ADMIN_LOG = "%Spas-admin/logs";

    public static String getPasAppLogFolder() {
        return getFormattedFolderPathForPasSelectedLog(PAS_APP_LOG);
    }

    public static String getPasAdminLogFolder() {
        return getFormattedFolderPathForPasSelectedLog(PAS_ADMIN_LOG);
    }

    private static String getFormattedFolderPathForPasSelectedLog(String template) {
        return String.format(template, pasLogFolderPrefix);
    }

    /*
    * Method returns all claims analytics rows as List<String> items from pas-app wrapper log
    */
    public List<String> retrieveClaimsAnalyticsLogValues(String selectedLog) {
        List<String> matches = new ArrayList<>();
        String regexp = "\\{\"claims-assignment\":(.*)\"}}";

        Matcher m = Pattern.compile(regexp).matcher(selectedLog);

        while (m.find()) {
            matches.add(m.group(0));
        }

        assertThat(matches).as("Log doesn't contain any Claim Analytic values").isNotEmpty();
        return matches;

    }

}
