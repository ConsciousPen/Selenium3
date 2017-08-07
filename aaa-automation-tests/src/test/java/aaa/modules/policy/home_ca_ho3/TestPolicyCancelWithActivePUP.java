package aaa.modules.policy.home_ca_ho3;

import org.testng.annotations.Test;
import toolkit.datax.TestData;
import toolkit.utils.TestInfo;
import aaa.common.enums.SearchEnum.SearchBy;
import aaa.common.enums.SearchEnum.SearchFor;
import aaa.common.pages.Page;
import aaa.common.pages.SearchPage;
import aaa.main.enums.PolicyConstants;
import aaa.main.enums.ProductConstants;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ca.HomeCaPolicyActions;
import aaa.main.modules.policy.home_ca.actiontabs.CancelActionTab;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.modules.policy.PersonalUmbrellaBaseTest;

public class TestPolicyCancelWithActivePUP extends PersonalUmbrellaBaseTest {

    /**
     * @author Jurij Kuznecov
     * @name Test CAH Policy Cancel With Active PUP
     * @scenario 
     * 1.  Create new or open existent Customer
     * 2.  Create a new PUP policy (CAH-HO3 + CA-Auto)
     * 3.  Navigate to CAH policy
     * 4.  Initiate 'Cancellation' action
     * 5.  Verify alert message
     * 6.  Confirm cancellation and verify Policy Status 'Policy Cancelled'
     */

    @Test
    @TestInfo(component = "Policy.HomeCA")
    public void testPolicyCancelWithActivePUP() {
        String alert = "Are you sure you want to cancel the underlying home policy? The companion PUP will need review.";

        mainApp().open();

        createCustomerIndividual();
        createPolicy();

        String cahPolicyNumber = PolicySummaryPage.tablePupPropertyInformation.getRow(1).getCell(PolicyConstants.PolicyPupPropertyInformationTable.POLICY_NO).getValue();
        SearchPage.search(SearchFor.POLICY, SearchBy.POLICY_QUOTE, cahPolicyNumber);

        TestData tdHome = testDataManager.policy.get(PolicyType.HOME_CA_HO3);
        new HomeCaPolicyActions.Cancel().start();
        CancelActionTab cancelActionTab = new CancelActionTab();
        cancelActionTab.fillTab(getStateTestData(tdHome, "Cancellation", "TestData"));
        cancelActionTab.submitTab();

        NotesAndAlertsSummaryPage.alertConfirmPolicyCancellation.verify.contains(alert);

        Page.dialogConfirmation.buttonOk.click();
        PolicySummaryPage.labelPolicyStatus.verify.value(ProductConstants.PolicyStatus.POLICY_CANCELLED);
    }
}
