package aaa.modules.regression.sales.auto_ca.select.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.CompCollSymbolsPresence;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestRatingDetailsView extends CompCollSymbolsPresence {
    /**
     * PAS-535
     *
     * @author Viktor Petrenko
     * @name View Rating details UI update.
     * @scenario 0. Create customer and auto SS policy with 2 Vehicles
     * 1. Initiate quote creation
     * 2. Go to the vehicle tab
     * 3. Add second vehicle
     * 4. Rate quote
     * 5. Open rating detail view
     * @details
     */

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_SELECT;
    }

	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_SELECT)
    public void testQuoteRatingViewDetailsCompCollSymbolsArePresentAndNotEmpty() {
        super.verifyCompCollSymbolsPresence();
    }

}
