package aaa.modules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;

import com.exigen.ipb.etcsa.utils.DBManager;
import com.google.common.base.CaseFormat;

import aaa.admin.metadata.general.GeneralMetaData;
import aaa.admin.metadata.workflow.WorkFlowMetadata;
import aaa.admin.modules.general.note.INote;
import aaa.admin.modules.general.note.NoteType;
import aaa.admin.modules.security.profile.ProfileType;
import aaa.admin.modules.security.role.IRole;
import aaa.admin.modules.security.role.RoleType;
import aaa.admin.modules.workflow.processmanagement.ITask;
import aaa.admin.modules.workflow.processmanagement.TaskType;
import aaa.admin.modules.workflow.processmanagement.defaulttabs.CreateManualTaskDefinitionTab;
import aaa.common.Tab;
import aaa.common.enums.NavigationEnum;
import aaa.common.pages.NavigationPage;
import aaa.main.enums.MyWorkConstants;
import aaa.main.metadata.CustomerMetaData;
import aaa.main.metadata.MyWorkMetaData;
import aaa.main.modules.customer.Customer;
import aaa.main.modules.customer.CustomerActions;
import aaa.main.modules.customer.actiontabs.AddAgencyActionTab;
import aaa.main.modules.customer.defaulttabs.RelationshipTab;
import aaa.main.modules.mywork.IMyWork;
import aaa.main.modules.mywork.MyWorkActions;
import aaa.main.modules.mywork.MyWorkType;
import aaa.main.modules.mywork.actiontabs.CreateTaskActionTab;
import aaa.main.modules.mywork.actiontabs.FilterTaskActionTab;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.main.pages.summary.MyWorkSummaryPage;
import aaa.rest.RESTServiceType;
import aaa.rest.billing.BillingRestClient;
import aaa.rest.customer.CustomerCoreRESTMethods;
import aaa.rest.partysearch.model.Party;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.utils.DBHelper;
import toolkit.webdriver.controls.Button;
import toolkit.webdriver.controls.CheckBox;
import toolkit.webdriver.controls.composite.assets.metadata.AttributeDescriptor;
import toolkit.webdriver.controls.composite.table.Row;
import toolkit.webdriver.controls.composite.table.Table;

public class RestBaseTest extends BaseTest {

    private NoteType noteType = NoteType.NOTE;
    private MyWorkType myWorkType = MyWorkType.MY_WORK;
    private RoleType roleType = RoleType.CORPORATE;
    private TaskType taskType = TaskType.PROCESS_MANAGEMENT;
    private ProfileType profileType = ProfileType.CORPORATE;
    private RESTServiceType notesType = RESTServiceType.NOTES;
    private RESTServiceType bpmType = RESTServiceType.BPM;
    private RESTServiceType partySearchType = RESTServiceType.PARTY_SEARCH;
    private RESTServiceType customersType = RESTServiceType.CUSTOMERS;
    private RESTServiceType billingType = RESTServiceType.BILLING;

    protected static final String CUSTOMER_RS_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss'Z'";
    protected static final String CHECK_TASK_PRESENCE = "select count(ID_) from ACT_RE_PROCDEF where NAME_ like '%1$s'";
    protected static final String CHECK_NOTE_CATEGORY_PRESENCE = "select count(id) from QuickNote where categoryCd='%1$s'";

    protected String testDataKey = "TestData";
    protected TestData tdProfile = testDataManager.profiles.get(profileType);
    protected TestData tdNotesRest = testDataManager.rest.get(notesType).getTestData("NotesRestData", testDataKey);
    protected TestData tdNotesBOBRest = testDataManager.rest.get(notesType).getTestData("NotesBOBRestData", testDataKey);
    protected TestData tdTasksRest = testDataManager.rest.get(bpmType).getTestData("TasksRestData", testDataKey);
    protected TestData tdTasksAgencyTransferRest = testDataManager.rest.get(bpmType).getTestData("TasksRestAgencyTransferData", testDataKey);
    protected TestData tdCustomersRest = testDataManager.rest.get(customersType).getTestData("CustomerRestData", testDataKey);
    protected TestData tdMyWork = testDataManager.myWork.get(myWorkType);
    protected TestData tdMyWorkRest = testDataManager.rest.get(bpmType).getTestData("MyWorkUsersAccessRestData", testDataKey);
    protected TestData tdBillingRest = testDataManager.rest.get(billingType).getTestData("BillingRestData", testDataKey);
    protected TestData tdPartySearchRest = testDataManager.rest.get(partySearchType).getTestData("PartySearchRestData", testDataKey);

    protected INote note = noteType.get();
    protected ITask task = taskType.get();
    protected IRole role = roleType.get();
    protected IMyWork myWork = myWorkType.get();
    protected Customer customer = new Customer();

    protected DBHelper dbHelper = DBManager.netInstance();
    protected CustomerCoreRESTMethods customerRestClient = customersType.get();
    protected BillingRestClient billingRestClient = billingType.get();

    protected String specialSymbols = "!@#$%^&*()?\"\'/\\:;.|-+=_";

    public static Button buttonCreateTask = new Button(By.xpath("//a[contains(@id,'createTask') and text()='Create Task']"));
    public static Button buttonTasks = new Button(By.xpath("//*[contains(@id,'tasksList') and text()='Tasks']"));

    protected void createNoteCategoryIfNotExist(TestData testData) {
        if (dbHelper.getValue(String.format(CHECK_NOTE_CATEGORY_PRESENCE, testData.getValue(GeneralMetaData.AddNoteCategory.class.getSimpleName(), GeneralMetaData.AddNoteCategory.TITLE.getLabel()))).equals("0")) {
            note.create(testData);
        }
    }

    protected void createTaskIfNotExist(TestData testData) {
        if (dbHelper.getValue(String.format(CHECK_TASK_PRESENCE, testData.getValue(CreateManualTaskDefinitionTab.class.getSimpleName(), WorkFlowMetadata.CreateManualTaskDefinitionTab.TASK_NAME.getLabel()))).equals("0")) {
            task.create(testData);
        }
    }

    protected String createTaskAndGetId(TestData testData, String entityReferenceNumber, String taskTestDataKey, MyWorkConstants.MyWorkFilterTaskStatus status) {
        navigateToMyWorkTab();
        myWork.createTask().perform(testData.getTestData(taskTestDataKey)
                .adjust(CreateTaskActionTab.class.getSimpleName(),
                        testData.getTestData(taskTestDataKey).getTestData(CreateTaskActionTab.class.getSimpleName())
                                .adjust(MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel(), entityReferenceNumber)));
        filterTasks(MyWorkMetaData.FilterTaskActionTab.REFERENCE_ID, entityReferenceNumber, status);
        Map<String, String> temporary = new HashMap<>();
        temporary.put(MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel(), testData.getTestData(taskTestDataKey).getTestData(CreateTaskActionTab.class.getSimpleName()).getValue(MyWorkMetaData.CreateTaskActionTab.TASK_NAME.getLabel()));
        temporary.put(MyWorkMetaData.CreateTaskActionTab.REFERENCE_ID.getLabel(), entityReferenceNumber);
        return MyWorkSummaryPage.tableTasks.getRow(temporary).getCell(MyWorkConstants.MyWorkTasksTable.TASK_ID).getValue();
    }


    protected int getTableRowNumber(Table table, String column, String value) {
        int i = 1;
        boolean exist = false;
        for (Row row : table.getRows()) {
            if (row.getCell(column).getValue().equals(value)) {
                exist = true;
                break;
            }
            i++;
        }
        if (!exist) {
            throw new IstfException(String.format("There are no Rows in the table with value=[%1$s] in column=[%2$s]", value, column));
        }
        return i;
    }

    protected void filterTasks(AttributeDescriptor attributeDescriptor, String id, MyWorkConstants.MyWorkFilterTaskStatus status) {
        navigateToMyWorkTab();
        String taskState = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, status.name());
        TestData testData = testDataManager.myWork.get(myWorkType).getTestData(MyWorkActions.FilterTask.class.getSimpleName(), testDataKey).resolveLinks();
        TestData filterTaskData = testData.getTestData(FilterTaskActionTab.class.getSimpleName()).resolveLinks();
        filterTaskData = filterTaskData.adjust(attributeDescriptor.getLabel(), id).adjust(MyWorkMetaData.FilterTaskActionTab.STATUS.getLabel(), taskState).resolveLinks();
        MyWorkType.MY_WORK.get().filterTask()
                .perform(testData.adjust(FilterTaskActionTab.class.getSimpleName(), filterTaskData));
        MyWorkSummaryPage.openMyInboxSection();
        String tableColumn = attributeDescriptor.getLabel().contains(MyWorkConstants.MyWorkTasksTable.TASK_ID) ? MyWorkConstants.MyWorkTasksTable.TASK_ID : MyWorkConstants.MyWorkTasksTable.REFERENCE_ID;
        if (!MyWorkSummaryPage.tableTasks.getRow(1).getCell(tableColumn).isPresent()) {
            MyWorkSummaryPage.openAllQueuesSection();
        }
    }

    protected CustomerWithRelationship createCustomerWithRelationship(TestData customerData, TestData relationshipData) {
        CustomerWithRelationship result = new CustomerWithRelationship();
        customer.createViaUI(relationshipData);
        removeOrAddAdditionalAgency(relationshipData);
        String[] customerNames = CustomerSummaryPage.labelCustomerName.getValue().split(" ");
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        List<TestData> testDatas = customerData.getTestDataList(RelationshipTab.class.getSimpleName());
        TestData test;
        if (customerNames.length == 2) {
            result.setRelatedEntity(new Party(customerNames[1], customerNames[0], customerNumber));
            test = testDatas.get(0).adjust(CustomerMetaData.RelationshipTab.FIRST_NAME.getLabel(), customerNames[0]).adjust(CustomerMetaData.RelationshipTab.LAST_NAME.getLabel(), customerNames[1]).resolveLinks();
        } else {
            result.setRelatedEntity(new Party(customerNames[0], customerNumber));
            test = testDatas.get(0).adjust(CustomerMetaData.RelationshipTab.NAME_LEGAL.getLabel(), customerNames[0]).resolveLinks();
        }
        testDatas.clear();
        testDatas.add(test);
        customerData.adjust(RelationshipTab.class.getSimpleName(), testDatas);
        customer.createViaUI(customerData);
        removeOrAddAdditionalAgency(customerData);
        customerNames = CustomerSummaryPage.labelCustomerName.getValue().split(" ");
        customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        if (customerNames.length == 2) {
            result.setCustomer(new Party(customerNames[1], customerNames[0], customerNumber));
        } else {
            result.setCustomer(new Party(customerNames[0], customerNumber));
        }
        return result;
    }

    protected void verifyTaskButtonsState(TaskControlsState controlState) {
        switch (controlState) {
            case ABSENT: {
                buttonTasks.verify.present(false);
                buttonCreateTask.verify.present(false);
                break;
            }
            case DISABLED: {
                buttonTasks.verify.enabled(false);
                buttonCreateTask.verify.enabled(false);
                break;
            }
            case ENABLED: {
                buttonTasks.verify.enabled();
                buttonCreateTask.verify.enabled();
                break;
            }
            case CREATE_TASK_ENABLED_TASKS_DISABLED: {
                buttonCreateTask.verify.enabled();
                buttonTasks.verify.enabled(false);
                break;
            }
            case ONLY_CREATE_TASK: {
                buttonCreateTask.verify.enabled();
                buttonTasks.verify.present(false);
                MyWorkSummaryPage.tableTasks.verify.present(false);
                MyWorkSummaryPage.buttonShowFilter.verify.present(false);
                break;
            }
            default: {
                throw new IstfException("Stub, please implement needed check above");
            }
        }

    }

    // TODO IGULIAM: added due to liquidBase script changes did not applied by application (known issue)
    protected void setLeadLifecycle() {
        adminApp().open();
        NavigationPage.toMainTab(NavigationEnum.AdminAppMainTabs.CEM.get());
        new CheckBox(By.id("configForm:leadLifecycle")).setValue(true);
        Tab.buttonSave.click();
    }

    private void removeOrAddAdditionalAgency(TestData testData) {
        if (testData.containsKey(CustomerActions.RemoveAgency.class.getSimpleName())) {
            NavigationPage.toViewSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
            customer.removeAgency().start(0);
            RelationshipTab.buttonSaveAndExit.click();
        } else if (testData.containsKey(AddAgencyActionTab.class.getSimpleName())) {
            NavigationPage.toViewSubTab(NavigationEnum.CustomerSummaryTab.CONTACTS_RELATIONSHIPS.get());
            if (testData.containsKey(RelationshipTab.class.getSimpleName())) {
                testData.mask(RelationshipTab.class.getSimpleName());
            }
            customer.addAgency().perform(testData);
            RelationshipTab.buttonSaveAndExit.click();
            testData.purgeAdjustments();
        }
    }

    private void navigateToMyWorkTab() {
        if (!MyWorkSummaryPage.buttonShowFilter.isPresent()) {
            NavigationPage.toMainTab(NavigationEnum.AppMainTabs.MY_WORK.get());
        }
    }

    protected enum TaskControlsState {
        ABSENT,
        DISABLED,
        PRESENT,
        ENABLED,
        CREATE_TASK_ENABLED_TASKS_DISABLED,
        ONLY_CREATE_TASK;
    }

    protected enum PartOfDay {
        MORNING,
        NIGHT,
        EVENING,
        AFTERNOON
    }

    protected enum DayOfWeek {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY
    }

    protected enum ConsentStatus {
        GRANTED,
        DENIED,
        REQUESTED,
        NOT_REQUESTED
    }

    protected class CustomerWithRelationship {

        private Party customer;
        private Party relatedEntity;

        public Party getCustomer() {
            return customer;
        }

        public void setCustomer(Party customer) {
            this.customer = customer;
        }

        public Party getRelatedEntity() {
            return relatedEntity;
        }

        public void setRelatedEntity(Party relatedEntity) {
            this.relatedEntity = relatedEntity;
        }
    }
}
