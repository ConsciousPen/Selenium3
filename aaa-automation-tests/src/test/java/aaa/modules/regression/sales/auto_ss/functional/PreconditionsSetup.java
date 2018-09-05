package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import aaa.config.CsaaTestProperties;
import aaa.helpers.listeners.AaaTestListener;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;

@Listeners({AaaTestListener.class})
public class PreconditionsSetup {

    private static final String SET_POLICY_NUMBER_RANGE = "update NumberRange\n"
            + "set indx = %s\n"
            + "where ENTITYCLASS = 'com.exigen.ipb.policy.domain.PolicySummary'";

    private static final String GET_DAY_OF_WEEK = "select to_char(sysdate,'D') from dual";

    /**
     Fastlane SIT - 18.2 testing
     Fastlane E2E - 18.1/evalue CERT testing
     Fastlane BF - 18.1/evalue integration testing

     endpoints:
     docGenwebClient.endpointUri	http://sit-soaservices.tent.trt.csaa.pri:42000/3.1/StandardDocumentService
     aaaRetrieveAgreementWebClient.endpointUri	http://sit-soaservices.tent.trt.csaa.pri:42000/1.1/RetrieveAgreementRelatedDocuments
     aaaRetrieveDocumentWebClient.endpointUri	http://sit-soaservices.tent.trt.csaa.pri:42000/1.1/RetrieveDocument

     sql:
     select * from propertyconfigurerentity
     where propertyname in ('docGenwebClient.endpointUri', 'aaaRetrieveAgreementWebClient.endpointUri', 'aaaRetrieveDocumentWebClient.endpointUri');

     Current situation:
     When deploying with recreate DB the policy number we create are always the same. This leads to the situation when there are multiple Documents
     for the same policy create on several days in different environments.

     Solution:
     The test changes the sequence number of the policy depending on team number, env name, and day of week to not to overlap with each other.
     Once a week there will be a purge done by Fastlane team.
     */
    @Test(description = "updates number range for policies to start with")
    public void numberRangeUpdate() {

		String env = PropertyProvider.getProperty(CsaaTestProperties.APP_HOST);
        String dayOfWeek = DBService.get().getValue(GET_DAY_OF_WEEK).get();
        String teamToUse = "";
        String envNumToUse = "";

        for (Envss envs : Envss.values()) {
            if (env.contains(envs.getEnvName())) {
                teamToUse = envs.getTeamNum();
                envNumToUse = envs.getEnvNum();
                break;
            }
        }
        //starting range - 960011000
        switch (dayOfWeek) {
            case "1":
                setNumberRangeParamsOnly(dayOfWeek, teamToUse, envNumToUse);
                break;
            case "2":
                setNumberRangeParamsOnly(dayOfWeek, teamToUse, envNumToUse);
                break;
            case "3":
                setNumberRangeParamsOnly(dayOfWeek, teamToUse, envNumToUse);
                break;
            case "4":
                setNumberRangeParamsOnly(dayOfWeek, teamToUse, envNumToUse);
                break;
            case "5":
                setNumberRangeParamsOnly(dayOfWeek, teamToUse, envNumToUse);
                break;
            case "6":
                setNumberRangeParamsOnly(dayOfWeek, teamToUse, envNumToUse);
                break;
            case "7":
                setNumberRangeParamsOnly(dayOfWeek, teamToUse, envNumToUse);
                break;
            default:
        }
    }

    private void setNumberRangeParamsOnly(String dayOfWeek, String teamToUse, String envNumToUse) {
        setNumberRangeForPolicy("9600" + teamToUse + envNumToUse + dayOfWeek + "00");
    }

    private void setNumberRangeForPolicy(String s) {
        DBService.get().executeUpdate(String.format(SET_POLICY_NUMBER_RANGE, s));
    }

    /** envs enum per team per day of week
     * param1 - env name
     * param2 - team number
     * param3 - day of week
     */
    enum Envss {
        NVD7PASQATRG037("nvd7pasqatrg037", "1", "0"),
        NVD7PASQATRG038("nvd7pasqatrg038", "1", "1"),
        NVD7PASQATRG039("nvd7pasqatrg039", "1", "2"),
        NVD7PASQATRG041("nvd7pasqatrg041", "1", "3"),
        NVM7PAS546("nvm7pas546", "1", "4"),
        VDD7S0PLYPAS044("vdd7s0plypas044", "1", "5"),
        VDD7S0PLYPAS128("vdd7s0plypas128", "1", "6"),
        NVM7PASQATRG010("NVM7PASQATRG010", "1", "7"),
        NVM7PASQATRG029("nvm7pasqatrg029", "1", "8"),
        NVM7PASQATRG030("nvm7pasqatrg030", "1", "9");

        private final String envName;
        private final String teamNum;
        private final String envNum;

        Envss(String envName, String teamNum, String envNum) {
            this.envName = envName;
            this.teamNum = teamNum;
            this.envNum = envNum;
        }

        public String getEnvName() {
            return envName;
        }

        public String getTeamNum() {
            return teamNum;
        }

        public String getEnvNum() {
            return envNum;
        }
    }
}



