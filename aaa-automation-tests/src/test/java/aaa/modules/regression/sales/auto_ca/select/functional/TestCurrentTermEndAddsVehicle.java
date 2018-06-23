package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.TestCurrentTermEndAddsVehicleTemplate;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestCurrentTermEndAddsVehicle extends TestCurrentTermEndAddsVehicleTemplate {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

    /**
     * * @author Sarunas Jaraminas
     *
     * @name Current Term End Adds Vehicle:
     * Make refresh correct for current and renewal terms
     *
     * @scenario
     *1. Create customer.
     *2. Create Auto CA Quote.
     *
     * @details
     */

    @Parameters({"state"})
    @Test(groups = {Groups.FUNCTIONAL, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT, testCaseId = "PAS-535")
    public void pas14532_MakeRefreshCorrectForRenewalTerm(@Optional("") String state) {
        makeRefreshCorrectForRenewalTerm();
    }
}