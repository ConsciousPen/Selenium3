package aaa.modules.regression.service.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import aaa.common.enums.Constants.States;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum.SearchBy;
import aaa.main.enums.SearchEnum.SearchFor;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.actiontabs.CancelActionTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;

public class TestPolicyCancelWithActivePUP extends HomeCaHO3BaseTest {

    /**
     * @author Jurij Kuznecov
	 * <b> Test CAH Policy Cancel With Active PUP </b>
	 * <p> Steps:
	 * <p> 1.  Create new or open existent Customer
	 * <p> 2.  Create a new PUP policy (CAH-HO3 + CA-Auto)
	 * <p> 3.  Navigate to CAH policy
	 * <p> 4.  Initiate 'Cancellation' action
	 * <p> 5.  Verify alert message
	 * <p> 6.  Confirm cancellation and verify Policy Status 'Policy Cancelled'
     */

	@Parameters({"state"})
	@StateList(states =  States.CA)
	@Test(groups = { Groups.REGRESSION, Groups.HIGH })
	@TestInfo(component = ComponentConstant.Service.HOME_CA_HO3)
    public void testPolicyCancelWithActivePUP(@Optional("CA") String state) {
        String alert = "Are you sure you want to cancel the underlying home policy? The companion PUP will need review.";

        mainApp().open();

        createCustomerIndividual();

        String caHomePolicyNumber = createPolicy();
        TestData tdAuto = testDataManager.policy.get(PolicyType.AUTO_CA_SELECT);
        PolicyType.AUTO_CA_SELECT.get().createPolicy(getStateTestData(tdAuto, "DataGather", "TestData"));
        String caAutoPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        HashMap<String, String> policies = new HashMap<>();
        policies.put("Primary_HO3", caHomePolicyNumber);
        policies.put("Primary_Auto", caAutoPolicyNumber);
        TestData tdPup = testDataManager.policy.get(PolicyType.PUP);
        TestData tdPupData = getStateTestData(tdPup, "DataGather", "TestData");
        tdPupData = new PrefillTab().adjustWithRealPolicies(tdPupData, policies);
        PolicyType.PUP.get().createPolicy(tdPupData);

        SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, caHomePolicyNumber);

        new HomeCaPolicyActions.Cancel().start();
        CancelActionTab cancelActionTab = new CancelActionTab();
        cancelActionTab.fillTab(getPolicyTD("Cancellation", "TestData"));
        CancelActionTab.buttonOk.click();

        assertThat(NotesAndAlertsSummaryPage.alertConfirmPolicyCancellation).valueContains(alert);

        Page.dialogConfirmation.buttonOk.click();
        assertThat(PolicySummaryPage.labelPolicyStatus).hasValue(ProductConstants.PolicyStatus.POLICY_CANCELLED);
    }
}
