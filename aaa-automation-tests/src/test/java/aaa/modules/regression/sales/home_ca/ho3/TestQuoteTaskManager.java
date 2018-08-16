package aaa.modules.regression.sales.home_ca.ho3;

import static toolkit.verification.CustomAssertions.assertThat;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import toolkit.utils.TestInfo;
import aaa.common.enums.Constants.States;
import aaa.common.enums.NavigationEnum.AppMainTabs;
import aaa.common.pages.NavigationPage;
import aaa.helpers.constants.ComponentConstant;
import aaa.helpers.constants.Groups;
import aaa.main.enums.MyWorkConstants;
import aaa.main.modules.mywork.MyWork;
import aaa.main.modules.mywork.actiontabs.CompleteTaskActionTab;
import aaa.main.modules.policy.home_ca.defaulttabs.BindTab;
import aaa.main.modules.policy.home_ca.defaulttabs.ErrorTab;
import aaa.main.modules.policy.home_ca.defaulttabs.PurchaseTab;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.TaskDetailsSummaryPage;
import aaa.modules.policy.HomeCaHO3BaseTest;
import aaa.utils.StateList;

public class TestQuoteTaskManager extends HomeCaHO3BaseTest {

    /**
      * @author Jurij Kuznecov
      * @name Test CAH Quote Task Manager
      * @scenario 
      * 1.  Create new or open existent Customer
      * 2.  Initiate HO3 policy creation
      * 3.  Fill all mandatory fields on all tabs, set Coverage A Dwelling Limit on 'PropertyInfo' tab to $95000
      * 4.  Purchase policy with refer for approval and override
      * 5.  Check that System creates an Override Approval task
      * 6.  Complete task
      * 7.  Check task is completed
      */

    @Parameters({"state"})
    @StateList(states =  States.CA)
	@Test(groups = {Groups.REGRESSION, Groups.CRITICAL})
    @TestInfo(component = ComponentConstant.Sales.HOME_CA_HO3)
    public void testQuoteTaskManager(@Optional("CA") String state) {
        BindTab bindTab = new BindTab();
        ErrorTab errorTab = new ErrorTab();
        MyWork myWork = new MyWork();

        String expectedTaskName = "Refer for Override Approval";

        mainApp().open();
        createCustomerIndividual();

        policy.initiate();

        policy.getDefaultView().fillUpTo(getPolicyTD().adjust(getTestSpecificTD("TestData")), BindTab.class);


        //  4.  Purchase policy with refer for approval and override
        bindTab.btnPurchase.click();
        errorTab.fillTab(getTestSpecificTD("TestData_Approval"));
        errorTab.buttonApproval.click();
        bindTab.btnPurchase.click();
        errorTab.fillTab(getTestSpecificTD("TestData_Override")).submitTab();
        new PurchaseTab().fillTab(getPolicyTD()).submitTab();

        //  5.  Check that System creates an Override Approval task
        String referenceID = PolicySummaryPage.labelPolicyNumber.getValue();
        NavigationPage.toMainTab(AppMainTabs.MY_WORK.get());
        myWork.filterTask().performByReferenceId(referenceID);
        MyWorkSummaryPage.linkAllQueues.click();
        //MyWorkSummaryPage.tableTasks.getRowContains(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID, referenceID).getCell(MyWorkConstants.MyWorkTasksTable.TASK_NAME).verify.contains(expectedTaskName);
        assertThat(MyWorkSummaryPage.tableTasks.getRowContains(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID, referenceID).getCell(MyWorkConstants.MyWorkTasksTable.TASK_NAME).getValue()).contains(expectedTaskName);

        //  6.  Complete task
        MyWorkSummaryPage.tableTasks.getRowContains(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID, referenceID).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID).controls.links.getFirst().click();
        TaskDetailsSummaryPage.buttonComplete.click();
        CompleteTaskActionTab.buttonComplete.click();

        //  7.  Check task is completed
        NavigationPage.toMainTab(AppMainTabs.MY_WORK.get());
        myWork.filterTask().performByReferenceId(referenceID);
        MyWorkSummaryPage.linkAllQueues.click();
        assertThat(MyWorkSummaryPage.tableTasks.getRowContains(MyWorkConstants.MyWorkTasksTable.REFERENCE_ID, referenceID)).isPresent(false);
    }
}
