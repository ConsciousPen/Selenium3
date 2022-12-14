package aaa.modules.regression.sales.template;

import static toolkit.verification.CustomAssertions.assertThat;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.common.pages.SearchPage;
import aaa.main.enums.MyWorkConstants.MyWorkTasksTable;
import aaa.main.enums.SearchEnum.SearchBy;
import aaa.main.enums.SearchEnum.SearchFor;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.modules.mywork.IMyWork;
import aaa.main.modules.mywork.MyWork;
import aaa.main.modules.mywork.actiontabs.CreateTaskActionTab;
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
 *  6. Open active tasks from Quote summary view, check the task presence
 *  7. Open active tasks from Customer tab, check the task presence
 *  8. Open active tasks from Account tab, check the task presence
 *  9. Open active tasks from My Work tab, check the task presence
 *  10. Click on Id link of the task, and go to a Task Summary Panel.
 *  11. Check id and Reference on this tab.
 *  12. Complete the task. 
 *  13. Verify task is removed from Active tasks panel.
 *  </pre>
 **/
public class QuoteManualTask extends BaseTest {

	public void testQuoteManualTask() {
		
		IMyWork myWork = new MyWork();
	    TestData tdMyWork = testDataManager.myWork;
		
		mainApp().open();

		createCustomerIndividual();
		String quoteNum = createQuote();
		myWork.createTask().perform(tdMyWork.getTestData("CreateTask", "TestData"));
		
		String taskName = tdMyWork.getTestData("CreateTask", "TestData").getValue(CreateTaskActionTab.class.getSimpleName(),
				MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel());
		
		PolicySummaryPage.buttonTasks.click();
		assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_NAME, taskName)).isPresent();
		String taskId = MyWorkSummaryPage.getTaskIdNyName(taskName);
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.CUSTOMER.get());
		CustomerSummaryPage.buttonTasks.click();
		assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId)).isPresent();
		MyWorkSummaryPage.buttonCancel.click();
		
		NavigationPage.toMainTab(NavigationEnum.CustomerSummaryTab.ACCOUNT.get());
		CustomerSummaryPage.buttonTasks.click();
		assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId)).isPresent();
		
		NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK.get());
		myWork.filterTask().performByReferenceId(quoteNum);
		assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_NAME, taskName)).isPresent();
		
		MyWorkSummaryPage.openTaskDetailsById(taskId);
		assertThat(TaskDetailsSummaryPage.taskID).hasValue(taskId);
		assertThat(TaskDetailsSummaryPage.referanceId).hasValue(quoteNum);
		TaskDetailsSummaryPage.buttonCancel.click();
		
		myWork.completeTask().perform(taskId, tdMyWork.getTestData("CompleteTask", "TestData"));
		assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId)).isPresent(false);
		
		SearchPage.search(SearchFor.QUOTE, SearchBy.POLICY_QUOTE, quoteNum);
		PolicySummaryPage.buttonTasks.click();
		assertThat(MyWorkSummaryPage.tableTasks.getRow(MyWorkTasksTable.TASK_ID, taskId)).isPresent(false);
	}
	
}
