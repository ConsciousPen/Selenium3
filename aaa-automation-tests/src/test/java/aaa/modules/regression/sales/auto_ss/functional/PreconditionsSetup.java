package aaa.modules.regression.sales.auto_ss.functional;

import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import aaa.helpers.config.CustomTestProperties;
import aaa.helpers.listeners.AaaTestListener;
import aaa.modules.regression.sales.auto_ss.functional.preconditions.EvalueInsertSetupPreConditions;
import toolkit.config.PropertyProvider;
import toolkit.db.DBService;

@Listeners({AaaTestListener.class})
public class PreconditionsSetup implements EvalueInsertSetupPreConditions {

    private static final String SET_POLICY_NUMBER_RANGE = "update NumberRange\n"
            + "set indx = %s\n"
            + "where ENTITYCLASS = 'com.exigen.ipb.policy.domain.PolicySummary'";

    private static final String GET_DAY_OF_WEEK = "select to_char(sysdate,'D') from dual";

    @Test(description = "updates number range for policies to start with")
    public void numberRangeUpdate() {

        String env = PropertyProvider.getProperty(CustomTestProperties.APP_HOST);
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



