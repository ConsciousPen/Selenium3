package aaa.modules.regression.sales.home_ss.dp3.functional;

import aaa.common.enums.Constants;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestLegalMortgageeNameSSTemplate;
import aaa.utils.StateList;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestLegalMortgageeName extends TestLegalMortgageeNameSSTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_DP3;
    }

    /**
     * @author Sreekanth Kopparapu
     * @name Mortgagee & Additional Interests Tab - Validate New Mortgagee Clause filed and Text box value
     * 1. Create a Quote for HO3 and navigate till Mortgagee & Additional Interests Tab
     * 2. Select Option 'Yes' for Mortgagee and Mortgagee Information section is enabled
     * 3. Select Option 'Yes' for Use Legal Mortgagee Name and Mortgagee Clause field is enabled with the text box
     * 4. Validate the Mortgagee Clause Name with the value Passed in String
     * @details
     **/

    @StateList(states = {Constants.States.AZ, Constants.States.UT })
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Validate New Mortgagee Clause Text box")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-24699")
    public void pas24669_testLegalMortgageeNameNB(@Optional("") String state) {

        pas24669_testLegalMortgageeNameNB();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Mortgagee & Additional Interests Tab - Validate New Mortgagee Clause filed and Text box value
     * 1. Create a Policy for HO3 without adding any Legal Mortgagee Name - Mortgagee Clause on Mortgagee & Additional Interest tab
     * 2. Initiate an endorsement and navigate till Mortgagee & Additional Interests Tab
     * 3. Select Option 'Yes' for Mortgagee and Mortgagee Information section is enabled
     * 4. Select Option 'Yes' for Use Legal Mortgagee Name and Mortgagee Clause field is enabled with the text box
     * 5. Validate the Mortgagee Clause Name with the value Passed in String
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Validate New Mortgagee Clause Text box")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-24699")
    public void pas24699_testMortgageeClauseEndTx(@Optional("") String state) {

        pas24699_testMortgageeClauseSSEndTx();

    }

    /**
     * @author Sreekanth Kopparapu
     * @name Mortgagee & Additional Interests Tab - Validate New Mortgagee Clause filed and Text box value
     * 1. Create a Policy for HO3 without adding any Legal Mortgagee Name - Mortgagee Clause on Mortgagee & Additional Interest tab
     * 2. Initiate an renewal and navigate till Mortgagee & Additional Interests Tab
     * 3. Select Option 'Yes' for Mortgagee and Mortgagee Information section is enabled
     * 4. Select Option 'Yes' for Use Legal Mortgagee Name and Mortgagee Clause field is enabled with the text box
     * 5. Validate the Mortgagee Clause Name with the value Passed in String
     * 6. Navigate till Bind page and 'Save & Exit'
     * @details
     **/

    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH}, description = "Validate New Mortgagee Clause Text box")
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_DP3, testCaseId = "PAS-24699")
    public void pas24699_testMortgageeClauseSSRenewal(@Optional("") String state) {

        pas24699_testMortgageeClauseSSRenewal();

    }

}
