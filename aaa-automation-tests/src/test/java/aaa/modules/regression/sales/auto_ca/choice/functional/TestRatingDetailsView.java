package aaa.modules.regression.sales.auto_ca.choice.functional;

import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.modules.policy.PolicyType;
import aaa.modules.regression.sales.template.functional.CompCollSymbolsPresence;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;

public class TestRatingDetailsView extends CompCollSymbolsPresence {

    @Override
    protected PolicyType getPolicyType() {
        return PolicyType.AUTO_CA_CHOICE;
    }

    /**
     * PAS-1904
     *
     * @author Viktor Petrenko
     * @modified Lev Kazarnovskiy
     * @name View Rating details UI update.
     * @scenario 0. Create customer
     * 1. Initiate Auto Select quote creation
     * 2. Go to the vehicle tab, fill info with valid VIN
     * 3. Add second vehicle with VIN that do not match any values in DB
     * 4. Rate quote
     * 5. Open rating detail view and verify if Comp And Coll Symbols are displayed for both vehicles
     * Verify that they are the same for Vehicle 2
     * @details
     */
	@Parameters({"state"})
	@Test(groups = { Groups.FUNCTIONAL, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Sales.AUTO_CA_CHOICE)
    public void testQuoteRatingViewDetailsCompCollSymbolsArePresentAndNotEmpty(@Optional("CA") String state) {
        super.verifyCompCollSymbolsOnRatingDetails();
    }
}
