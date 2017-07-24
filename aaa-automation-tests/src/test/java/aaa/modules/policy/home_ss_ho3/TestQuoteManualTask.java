package aaa.modules.policy.home_ss_ho3;

import org.testng.annotations.Test;

import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.enums.SearchEnum.SearchBy;
import aaa.common.enums.SearchEnum.SearchFor;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.MyWorkConstants.MyWorkTasksTable;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.modules.mywork.IMyWork;
import aaa.main.modules.mywork.MyWork;
import aaa.main.modules.mywork.actiontabs.CreateTaskActionTab;
import aaa.main.modules.policy.PolicyType;
import aaa.main.modules.policy.home_ss.defaulttabs.ApplicantTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.main.pages.summary.PolicySummaryPage;
import aaa.main.pages.summary.TaskDetailsSummaryPage;
import aaa.modules.BaseTest;
import toolkit.datax.TestData;

/**
 * <pre>
 * TC Steps:
 *  1. Login
 *  2. Create new customer
 *  3. Initiate a quote creation, fill in General and Applicant tab.
 *  4. Save and Exit.
 *  5. Create a new Task.Fill all necessary fields.
 *  6. Open active tasks from quote summary view, check the task presence
 *  7. Open active tasks from customer tab, check the task presence
 *  8. Open active tasks from account tab, check the task presence
 *  9. Click on Id link of the task, and go to a Task Summary Panel.
 *  10. Check id and Reference on this tab.
 *  11. Complete the task. 
 *  12. Verify task is removed from Active tasks panel.
 *  </pre>
 **/
public class TestQuoteManualTask extends BaseTest {

	@Override
	protected PolicyType getPolicyType() {
		return PolicyType.HOME_SS_HO3;
	}
	
	@Test
	public void testQuoteManualTask() {
		
		IMyWork myWork = new MyWork();
	    TestData tdMyWork = testDataManager.myWork;
	    TestData tdPolicy = testDataManager.policy.get(getPolicyType());
		
		mainApp().open();
		
		createCustomerIndividual();
		
		getPolicyType().get().initiate();
		getPolicyType().get().getDefaultView().fillUpTo(
				getStateTestData(tdPolicy, "DataGather", "TestData"), ApplicantTab.class, true);
		Tab.buttonSaveAndExit.click();
		String quoteNum = PolicySummaryPage.labelPolicyNumber.getValue();
		
		myWork.createTask().perform(tdMyWork.getTestData("CreateTask", "TestData"));
		
		String taskName = tdMyWork.getTestData("CreateTask", "TestData").getValue(CreateTaskActionTab.class.getSimpleName(),
				MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel());
		
		PolicySummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_NAME, taskName).verify.present();
		String taskId = MyWorkSummaryPage.getTaskIdNyName(taskName);
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		CustomerSummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId).verify.present();
		MyWorkSummaryPage.buttonCancel.click();
		
		NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.ACCOUNT.get());
		CustomerSummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId).verify.present();
		
		MyWorkSummaryPage.openTaskDetailsById(taskId);
		TaskDetailsSummaryPage.taskID.verify.value(taskId);
		TaskDetailsSummaryPage.referanceId.verify.value(quoteNum);
		TaskDetailsSummaryPage.buttonCancel.click();
		
		myWork.completeTask().perform(taskId, tdMyWork.getTestData("CompleteTask", "TestData"));
		MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId).verify.present(false);
		
		SearchPage.search(SearchFor.QUOTE, SearchBy.POLICY_QUOTE, quoteNum);
		PolicySummaryPage.buttonTasks.click();
		MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId).verify.present(false);
	}
	
}
