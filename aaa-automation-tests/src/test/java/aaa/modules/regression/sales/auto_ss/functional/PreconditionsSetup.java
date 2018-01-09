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

        switch (dayOfWeek) {
            case "1":
                setNumberRangeForPolicy("9272" + teamToUse + envNumToUse + dayOfWeek + "45");
                break;
            case "2":
                setNumberRangeForPolicy("9272" + teamToUse + envNumToUse + dayOfWeek + "45");
                break;
            case "3":
                setNumberRangeForPolicy("9272" + teamToUse + envNumToUse + dayOfWeek + "45");
                break;
            case "4":
                setNumberRangeForPolicy("9272" + teamToUse + envNumToUse + dayOfWeek + "45");
                break;
            case "5":
                setNumberRangeForPolicy("9272" + teamToUse + envNumToUse + dayOfWeek + "45");
                break;
            case "6":
                setNumberRangeForPolicy("9272" + teamToUse + envNumToUse + dayOfWeek + "45");
                break;
            case "7":
                setNumberRangeForPolicy("9272" + teamToUse + envNumToUse + dayOfWeek + "45");
                break;
            default:
        }
    }

    private void setNumberRangeForPolicy(String s) {
        DBService.get().executeUpdate(String.format(SET_POLICY_NUMBER_RANGE, s));
    }

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



