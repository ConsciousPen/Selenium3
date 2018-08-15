package aaa.modules.regression.service.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.datax.impl.SimpleDataProvider;
import toolkit.utils.TestInfo;
import aaa.common.enums.Constants.States;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.metadata.policy.HomeCaMetaData;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;

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

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.CRITICAL })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyManualRenew(@Optional("CA") String state) {

        mainApp().open();
        createCustomerIndividual();
        createPolicy();

        new HomeCaPolicyActions.ManualRenew().perform(new SimpleDataProvider().adjust(HomeCaMetaData.AddManualRenewFlagActionTab.class.getSimpleName(),
                new SimpleDataProvider().adjust(HomeCaMetaData.AddManualRenewFlagActionTab.REASON.getLabel(), "Other")));
        assertThat(PolicySummaryPage.labelManualRenew).isPresent();
    }

}
