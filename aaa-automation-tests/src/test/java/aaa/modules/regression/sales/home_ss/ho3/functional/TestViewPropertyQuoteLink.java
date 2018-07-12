package aaa.modules.regression.sales.home_ss.ho3.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestViewPropertyQuoteLinkTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestViewPropertyQuoteLink extends TestViewPropertyQuoteLinkTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.HOME_SS_HO3;
    }

       /**
     * @author Sreekanth Kopparapu
     * @name Test that View Property Quote link is displayed and is disabled before Calculate Premium for SS Property policies
     * @scenario
     * 1. Create Customer
     * 2. Create Property SS Policy
     * 3. Navigate to P&C Page
     * 5. Go to Quote Tab and Verify  View Property Quote Link is displayed
     * 6. View Property Quote Link is disabled before calculating Premium
     * 7. Calculate Premium
     * 8. Verify  View Property Quote Link is enabled
     * 9. Click on View Property Quote Link and a new tab is opened with the details
     * @details
     */
    @Parameters({"state"})
    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.HOME_SS_HO3, testCaseId = "PAS-14842")
    public void pas14842_testViewPropertyQuoteLinkSSHO3NB(@Optional("") String state) {

        pas14842_testViewPropertyQuoteLinkSS(getPolicyTD());
    }

}
