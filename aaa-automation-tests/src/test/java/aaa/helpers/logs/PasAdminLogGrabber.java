package aaa.helpers.logs;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import aaa.config.CsaaTestProperties;
import toolkit.config.PropertyProvider;

/**
 * PasAdminLogGrabber is used to take pas-admin logs' specific lines with claims-assignment info for Claim Analytics, Microservice Request and Response
 * @author Mantas Garsvinskas
 */
public class PasAdminLogGrabber {

    private static String pasAdminLogFolderPrefix = PropertyProvider.getProperty(CsaaTestProperties.PAS_ADMIN_LOG_FOLDER, "/AAA/tcserver/pivotal-tc-server-developer-3.2.8.RELEASE/");
    private static final String PAS_ADMIN_LOG = "%Spas-admin/logs";

    public static String getPasAdminLogFolder() {
        return getFormattedFolderPathForPasAdminLog(PAS_ADMIN_LOG);
    }

    private static String getFormattedFolderPathForPasAdminLog(String template) {
        return String.format(template, pasAdminLogFolderPrefix);
    }

    /*
    * Method returns all claims analytics rows as List<String> items from pas-admin wrapper log
    */
    public List<String> retrieveClaimsAnalyticsLogValues(String adminLog) {
        List<String> matches = new ArrayList<>();
        String regexp = "\\{\"claims-assignment\":(.*)\"}}";

        Matcher m = Pattern.compile(regexp).matcher(adminLog);

        while (m.find()) {
            matches.add(m.group(0));
        }

        assertThat(matches).as("Log doesn't contain any Claim Analytic values").isNotEmpty();
        return matches;

    }

}
