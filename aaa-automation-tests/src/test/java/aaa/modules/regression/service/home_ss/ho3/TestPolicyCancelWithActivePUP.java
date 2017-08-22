package aaa.modules.regression.service.home_ss.ho3;

import java.util.HashMap;
import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.ProductConstants;
import aaa.main.enums.SearchEnum.SearchBy;
import aaa.main.enums.SearchEnum.SearchFor;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.HomeSSPolicyActions;
import aaa.main.modules.policy.home_ss.actiontabs.CancellationActionTab;
import aaa.main.modules.policy.pup.defaulttabs.PrefillTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.HomeSSHO3BaseTest;

public class TestPolicyCancelWithActivePUP extends HomeSSHO3BaseTest {

    /**
     * @author Jurij Kuznecov
     * @name Test HSS Policy Cancel With Active PUP
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Create a new PUP policy (HSS-HO3 + SS-Auto)
     * 3.  Navigate to HSS policy
     * 4.  Initiate 'Cancellation' action
     * 5.  Verify alert message
     * 6.  Confirm cancellation and verify Policy Status 'Policy Cancelled'
     */

    @Test(groups = {Groups.REGRESSION, Groups.HIGH})
    @TestInfo(component = ComponentConstant.Service.HOME_SS_HO3)
    public void testPolicyCancelWithActivePUP() {
        String alert = "Are you sure you want to cancel the underlying home policy? The companion PUP will need review.";

        mainApp().open();

        createCustomerIndividual();

        String ssHomePolicyNumber = createPolicy();
        TestData tdAuto = testDataManager.policy.get(PolicyType.AUTO_SS);
        PolicyType.AUTO_SS.get().createPolicy(getStateTestData(tdAuto, "DataGather", "TestData"));
        String ssAutoPolicyNumber = PolicySummaryPage.labelPolicyNumber.getValue();
        HashMap<String, String> policies = new HashMap<>();
        policies.put("Primary_HO3", ssHomePolicyNumber);
        policies.put("Primary_Auto", ssAutoPolicyNumber);
        TestData tdPup = testDataManager.policy.get(PolicyType.PUP);
        TestData tdPupData = getStateTestData(tdPup, "DataGather", "TestData");
        tdPupData = new PrefillTab().adjustWithRealPolicies(tdPupData, policies);
        PolicyType.PUP.get().createPolicy(tdPupData);

        SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, ssHomePolicyNumber);

        new HomeSSPolicyActions.Cancel().start();
        CancellationActionTab cancellationActionTab = new CancellationActionTab();
        cancellationActionTab.fillTab(getPolicyTD("Cancellation", "TestData"));
        CancellationActionTab.buttonOk.click();

        NotesAndAlertsSummaryPage.alertConfirmPolicyCancellation.verify.contains(alert);

        Page.dialogConfirmation.buttonOk.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
    }
}
