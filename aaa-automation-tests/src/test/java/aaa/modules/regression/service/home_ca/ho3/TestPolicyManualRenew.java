package aaa.modules.regression.service.home_ca.ho3;

import org.testng.annotations.Test;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;

public class TestPolicyManualRenew extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Manual Renew
     * @scenario
     * 1. Create new or open existent Customer
     * 2. Find CAH quote or create new if quote does not exist
     * 3. Fill all mandatory fields
     * 4. Calculate premium
     * 5. Issue Policy
     * 6. Perform Manual Renew action
     * 7. Check Manual Renew flag
     */

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testPolicyManualRenew() {

        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        new HomeCaPolicyActions.ManualRenew().perform(new SimpleDataProvider().adjust(HomeCaMetaData.AddManualRenewFlagActionTab.class.getSimpleName(),
                new SimpleDataProvider().adjust(HomeCaMetaData.AddManualRenewFlagActionTab.REASON.getLabel(), "Other")));
        PolicySummaryPage.labelManualRenew.verify.present(true);
    }

}
