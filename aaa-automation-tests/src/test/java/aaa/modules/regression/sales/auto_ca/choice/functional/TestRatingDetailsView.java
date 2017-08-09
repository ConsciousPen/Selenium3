package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.RatingDetailsCompCollSymbolsPresence;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestRatingDetailsView extends RatingDetailsCompCollSymbolsPresence {

    /**
     * PAS-535
     *
     * @author Viktor Petrenko
     * @name View Rating details UI update.
     * @scenario
     * 0. Create customer and Auto CA policy with 2 Vehicles
     * 1. Initiate quote creation
     * 2. Go to the vehicle tab
     * 3. Add second vehicle
     * 4. Rate quote
     * 5. Open rating detail view
     * @details
     */

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    @Test
    @TestInfo(component = "Policy.AutoCA")
    public void testCompCollSymbolsPresence() {
        super.verifyCompCollSymbolsPresence();
    }
}
