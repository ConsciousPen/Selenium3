package aaa.rest.platform.bpm;

import javax.ws.rs.client.Entity;

import aaa.rest.IRestClient;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;
import toolkit.rest.RestServiceUtil.RestMethod;

public class BPMRestClient implements IRestClient {

    private ThreadLocal<RestServiceUtil> restClient = new ThreadLocal<RestServiceUtil>() {
        @Override protected RestServiceUtil initialValue() {
            return new RestServiceUtil("bpm-rs");
        }
    };

    /**
     * <b>Target:</b> Tasks
     */
    public ResponseWrapper getTasks(TestData data) {
        return restClient.get().processRequest("TASKS", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Tasks
     */
    public ResponseWrapper postTasks(TestData data) {
        return restClient.get().processRequest("POSTTASKS", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> Task
     */
    public ResponseWrapper getTaskById(TestData data) {
        return restClient.get().processRequest("TASKSBYID", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Completion
     */
    public ResponseWrapper getTaskCompletionById(TestData data) {
        return restClient.get().processRequest("TASKCOMPLETION", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Completion
     */
    public ResponseWrapper postTaskCompletionById(TestData data) {
        return restClient.get().processRequest("POSTTASKCOMPLETION", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> Assignment
     */
    public ResponseWrapper getTaskAssignmentById(TestData data) {
        return restClient.get().processRequest("TASKASSIGNMENT", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Assignment
     */
    public ResponseWrapper putTaskAssignmentById(TestData data) {
        return restClient.get().processRequest("PUTTASKASSIGNMENT", RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> Assignment
     */
    public ResponseWrapper putTaskAssignmentById(Entity<?> entity, TestData data) {
        return restClient.get().processRequest("PUTTASKASSIGNMENTENTITY", RestMethod.PUT, entity, data);
    }

    /**
     * <b>Target:</b> Activities
     */
    public ResponseWrapper getTaskActivitiesById(TestData data) {
        return restClient.get().processRequest("TASKACTIVITIES", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Activities
     */
    public ResponseWrapper postTaskActivitiesById(TestData data) {
        return restClient.get().processRequest("POSTTASKACTIVITIES", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> Action History
     */
    public ResponseWrapper getTaskActionsHistory(TestData data) {
        return restClient.get().processRequest("TASKACTIONHISTORY", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Inbox
     */
    public ResponseWrapper getInbox(TestData data) {
        return restClient.get().processRequest("INBOX", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Inbox
     */
    public ResponseWrapper getProcDefinition(TestData data) {
        return restClient.get().processRequest("PROCDEFINITION", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> WorkGroup
     */
    public ResponseWrapper createWorkGroup(TestData data) {
        return restClient.get().processRequest("CREATEWORKGROUP", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> WorkGroup
     */
    public ResponseWrapper getWorkGroup(TestData data) {
        return restClient.get().processRequest("GETWORKGROUP", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> WorkGroup
     */
    public ResponseWrapper deleteWorkGroup(TestData data) {
        return restClient.get().processRequest("DELETEWORKGROUP", RestMethod.DELETE, data);
    }

    /**
     * <b>Target:</b> WorkGroup
     */
    public ResponseWrapper updateWorkGroup(TestData data) {
        return restClient.get().processRequest("UPDATEWORKGROUP", RestMethod.PUT, data);
    }

    /**
     * <b>Target:</b> Document
     */
    public ResponseWrapper updloadDocument(TestData data) {
        return restClient.get().processRequest("UPLOADDOCUMENT", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> ProcessInstance
     */
    public ResponseWrapper getProcessInstance(TestData data) {
        return restClient.get().processRequest("PROCESSINSTANCE", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Manual Task
     */
    public ResponseWrapper getManualTaskDefinition(TestData data) {
        return restClient.get().processRequest("MANUALTASKDEF", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Deploy Task
     */
    public ResponseWrapper deployManualTaskDefinition(TestData data) {
        return restClient.get().processRequest("MANUALTASKDEPLOY", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> ProcessDefinition
     */
    public ResponseWrapper postProcessDefinition(TestData data) {
        return restClient.get().processRequest("POSTPROCESSDEFINITION", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> ProcessDefinition Bind Event
     */
    public ResponseWrapper postProcessDefinitionBindEvent(TestData data) {
        return restClient.get().processRequest("POSTPROCESSDEFINITIONBINDEVENT", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> Queues
     */
    public ResponseWrapper getQueues(TestData data) {
        return restClient.get().processRequest("QUEUES", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Queues
     */
    public ResponseWrapper deployQueues(TestData data) {
        return restClient.get().processRequest("QUEUESDEPLOY", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> Queues
     */
    public ResponseWrapper getQueuesExport(TestData data) {
        return restClient.get().processRequest("QUEUESEXPORT", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Queues
     */
    public ResponseWrapper getQueuesExportByFileName(TestData data) {
        return restClient.get().processRequest("QUEUESEXPORTBYFN", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> Events
     */
    public ResponseWrapper getEvents(TestData data) {
        return restClient.get().processRequest("EVENTS", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> PERMISSIONS
     */
    public ResponseWrapper getUsersPermissions(TestData data) {
        return restClient.get().processRequest("PERMISSIONS", RestMethod.GET, data);
    }

    /**
     * <b>Target:</b> PERMISSIONS
     */
    public ResponseWrapper postSearch(TestData data) {
        return restClient.get().processRequest("POSTSEARCH", RestMethod.POST, data);
    }

    /**
     * <b>Target:</b> PERMISSIONS
     */
    public ResponseWrapper getSearch(TestData data) {
        return restClient.get().processRequest("GETSEARCH", RestMethod.GET, data);
    }

    @Override public RestServiceUtil getRestClient() {
        return this.restClient.get();
    }
}
