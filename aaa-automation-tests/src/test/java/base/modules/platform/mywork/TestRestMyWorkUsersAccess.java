/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 * CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent. */
package base.modules.platform.mywork;

import org.testng.annotations.Test;

import aaa.admin.pages.workflow.ProcessManagementPage;
import aaa.main.enums.MyWorkConstants;
import aaa.main.pages.summary.CustomerSummaryPage;
import aaa.modules.RestBaseTest;
import aaa.rest.RESTServiceUser;
import aaa.rest.platform.bpm.BPMDevRestClient;
import aaa.rest.platform.bpm.BPMRestClient;
import aaa.rest.platform.bpm.model.processinstance.ProcessInstanceInfoReply;
import aaa.rest.platform.bpm.model.tasks.TasksResponseWrapper;
import toolkit.datax.DataProviderFactory;
import toolkit.utils.TestInfo;
import toolkit.verification.CustomAssert;

/**
 * @author Guliam Illia
 * @name Block user's access to MyWork tab for all users - TC
 * @scenario :
 * User 1 with privileges MyWork Access and MyWork Task Inquiry exists in system
 * User 2 without privilege MyWork Access exists in system
 * User 3 without privilege MyWork Task Inquiry exists in system
 * User 4 without privilege MyWork Services Access exists in system
 * Task for customer exist in system and is assigned to user 3
 * Customer exists in system
 * Billing Account exists in system
 * Quote exists in system
 * Policy exists in system
 * Claim exists in system
 * Note:
 * User 1 - qa (All Rights)
 * User 2 - MyWorkRSUsr1 (no MyWork Access)
 * User 3 - MyWorkRSUsr2 (no MyWork Task Inquiry)
 * User 4 - MyWorkRSUsr3 (no MyWork Services Access)
 * @details
 */
public class TestRestMyWorkUsersAccess extends RestBaseTest {

    private BPMRestClient bpmRestClient = new BPMRestClient();
    private BPMDevRestClient bpmDevRestClient = new BPMDevRestClient();

    @Test
    @TestInfo(component = "Platform.REST", testCaseId = "IPBQA-22297")
    public void testMyWorkAllUsersAccess() {
        mainApp().open();
        customer.create(tdCustomerNonIndividual.getTestData("DataGather", "TestData"));
        String customerNumber = CustomerSummaryPage.labelCustomerNumber.getValue();
        String taskId = createTaskAndGetId(tdMyWorkRest, customerNumber, "CreateManualTask1", MyWorkConstants.MyWorkFilterTaskStatus.ACTIVE);

        //MyWork Task Inquiry privilege WS REST steps
        CustomAssert.enableSoftMode();
        TasksResponseWrapper expectedResponse = new TasksResponseWrapper(tdMyWorkRest.getTestData("EmptyNonAuthorizedData", "Response"));
        bpmRestClient.getRestClient().adjustUser(RESTServiceUser.MY_WORK_RS_USER_2.getLogin(), RESTServiceUser.MY_WORK_RS_USER_2.getPassword());
        CustomAssert.assertEquals("Step 9 from IPBQA-22297 FAILED", expectedResponse,
                bpmRestClient.getInbox(tdMyWorkRest.getTestData("EmptyNonAuthorizedData", "Request")).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 10 from IPBQA-22297 FAILED", expectedResponse,
                bpmRestClient.getTasks(tdMyWorkRest.getTestData("EmptyNonAuthorizedData", "Request")).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 11 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskById(
                DataProviderFactory.emptyData().adjust("taskId", taskId).resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 12 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskCompletionById(
                DataProviderFactory.emptyData().adjust("taskId", taskId).resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 13 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskAssignmentById(
                DataProviderFactory.emptyData().adjust("taskId", taskId).resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 14 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskActivitiesById(
                DataProviderFactory.emptyData().adjust("taskId", taskId).resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 15 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskActionsHistory(
                DataProviderFactory.emptyData().adjust("taskId", taskId).resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));

        adminApp().open();
        task.navigate();
        ProcessManagementPage.buttonDeployProcessDefinition.click();
        ProcessManagementPage.dialogDeployProcess.fillAssetList(tdMyWorkRest, "Upload");
        ProcessManagementPage.uploadStatusMsg.waitForAccessible(5000);
        ProcessManagementPage.uploadStatusMsg.verify.value("Succesful");
        String processInstanceId =
                bpmDevRestClient.postProcessItem(DataProviderFactory.emptyData().adjust("processKey", "MYWORKRESTEST").adjust("entityRefNo", customerNumber).adjust("entityType", "Customer"))
                        .getResponse()
                        .readEntity(ProcessInstanceInfoReply.class).getActProcessInstanceId();

        CustomAssert.assertEquals("Step 16 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getProcessInstance(
                DataProviderFactory.emptyData().adjust("pinId", processInstanceId).resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));

        //'MyWork Services Access privilege using swagger steps
        bpmRestClient.getRestClient().adjustUser(RESTServiceUser.MY_WORK_RS_USER_3.getLogin(), RESTServiceUser.MY_WORK_RS_USER_3.getPassword());
        CustomAssert.assertEquals("Step 1 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getProcDefinition(DataProviderFactory.emptyData().adjust("procDefId", "10000").resolveLinks())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 2 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getProcessInstance(DataProviderFactory.emptyData().adjust("pinId", "10000").resolveLinks())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 3 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.createWorkGroup(tdMyWorkRest.getTestData("EmptyNonAuthorizedData", "Request"))
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 4 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getWorkGroup(DataProviderFactory.emptyData().adjust("groupCode", "10000").resolveLinks())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 5 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.deleteWorkGroup(DataProviderFactory.emptyData().adjust("groupCode", "10000").resolveLinks())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 6 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.updateWorkGroup(DataProviderFactory.emptyData().adjust("groupCode", "10000").resolveLinks())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 7 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.updloadDocument(tdMyWorkRest.getTestData("UploadDocumentRequest"))
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 8 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getEvents(DataProviderFactory.emptyData())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 9 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getInbox(DataProviderFactory.emptyData().adjust("offset", "0").adjust("limit", "10").resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 10 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getManualTaskDefinition(DataProviderFactory.emptyData())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 11 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.deployManualTaskDefinition(DataProviderFactory.emptyData().adjust("migration", "false").resolveLinks())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 12 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.postProcessDefinition(DataProviderFactory.emptyData())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 13 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.postProcessDefinitionBindEvent(DataProviderFactory.emptyData())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 14 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getQueues(DataProviderFactory.emptyData())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 15 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.deployQueues(DataProviderFactory.emptyData())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 16 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getQueuesExport(DataProviderFactory.emptyData().adjust("subsystem", "platform").resolveLinks())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 17 from IPBQA-22297(MyWork Services Access privilege) FAILED", expectedResponse,
                bpmRestClient.getQueuesExportByFileName(DataProviderFactory.emptyData().adjust("subsystem", "platform").adjust("fileName", "MISC").resolveLinks())
                        .getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 18 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskActionsHistory(
                DataProviderFactory.emptyData().adjust("taskId", "10000").adjust("actionType", "COMPLETE").adjust("lastAction", "true").resolveLinks()).getResponse()
                .readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 19 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTasks(
                DataProviderFactory.emptyData().adjust("offset", "0").adjust("limit", "10").resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 20 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.postTasks(
                DataProviderFactory.emptyData()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 21 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskById(
                DataProviderFactory.emptyData().adjust("taskId", "10000")).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 22 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskActivitiesById(
                DataProviderFactory.emptyData().adjust("taskId", "10000").adjust("notes", "process").resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 23 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.postTasks(
                DataProviderFactory.emptyData().adjust("taskId", "10000").resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 24 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskAssignmentById(
                DataProviderFactory.emptyData().adjust("taskId", "10000").resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 25 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.putTaskAssignmentById(
                DataProviderFactory.emptyData().adjust("taskId", "10000").resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 26 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getTaskCompletionById(
                DataProviderFactory.emptyData().adjust("taskId", "10000").resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 27 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.postTaskCompletionById(
                DataProviderFactory.emptyData().adjust("taskId", "10000").resolveLinks()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 28 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getUsersPermissions(
                DataProviderFactory.emptyData()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 29 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.postSearch(
                DataProviderFactory.emptyData()).getResponse().readEntity(TasksResponseWrapper.class));
        CustomAssert.assertEquals("Step 30 from IPBQA-22297 FAILED", expectedResponse, bpmRestClient.getSearch(
                DataProviderFactory.emptyData().adjust("firstName", "QA").adjust("location", "EXG").adjust("availableForWork", "true").adjust("lastName", "lastName").resolveLinks())
                .getResponse().readEntity(TasksResponseWrapper.class));

        CustomAssert.disableSoftMode();
        CustomAssert.assertAll();

    }

}
