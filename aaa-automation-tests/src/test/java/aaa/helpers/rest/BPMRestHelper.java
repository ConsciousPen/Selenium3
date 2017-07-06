package aaa.helpers.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;

import aaa.rest.RESTServiceType;
import aaa.rest.RESTServiceUser;
import aaa.rest.platform.bpm.BPMRestClient;
import aaa.rest.platform.bpm.model.tasks.Assignment;
import aaa.rest.platform.bpm.model.tasks.TaskAssignmentResponse;
import aaa.rest.platform.bpm.model.tasks.TasksResponse;
import aaa.rest.platform.bpm.model.tasks.TasksResponseWrapper;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.verification.CustomAssert;

public class BPMRestHelper {

    private static BPMRestClient bpmRestClient = RESTServiceType.BPM.get();

    public static void assertTasksResponse(TestData testData, RESTServiceUser user, Map<String, String> taskIdMapper) {
        bpmRestClient.getRestClient().adjustUser(user.getLogin(), user.getPassword());

        List<TestData> tdGetRequests = testData.getTestDataList("Requests");
        List<TestData> tdGetResponses = testData.getTestDataList("Responses");
        int i = 0;
        for (TestData data : tdGetRequests) {
            TasksResponseWrapper expectedResponse = new TasksResponseWrapper(tdGetResponses.get(i).getTestDataList("responses"), taskIdMapper);
            ResponseWrapper responseWrapper = bpmRestClient.getTasks(data);
            TasksResponseWrapper actualResponse = responseWrapper.getResponse().readEntity(TasksResponseWrapper.class);
            RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, tdGetResponses.get(i));
            i++;
        }
    }

    public static void assertTasksCreateResponse(TestData testData, RESTServiceUser user, String referenceId) {
        bpmRestClient.getRestClient().adjustUser(user.getLogin(), user.getPassword());

        List<TestData> tdGetRequests = testData.getTestDataList("Requests");
        List<TestData> tdGetResponses = testData.getTestDataList("Responses");
        int i = 0;
        for (TestData data : tdGetRequests) {
            ResponseWrapper responseWrapper = bpmRestClient.postTasks(RestServiceHelper.prepareTestDataForRestTest(data.adjust("referenceId", referenceId)));
            Assignment expectedResponse = new Assignment(tdGetResponses.get(i));
            Assignment actualResponse = responseWrapper.getResponse().readEntity(TasksResponse.class).getAssignment();
            RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, tdGetResponses.get(i));
            i++;
        }
    }

    public static void assertTasksCreateErrorResponse(TestData testData, RESTServiceUser user, String referenceId) {
        bpmRestClient.getRestClient().adjustUser(user.getLogin(), user.getPassword());

        List<TestData> tdGetRequests = testData.getTestDataList("Requests");
        List<TestData> tdGetResponses = testData.getTestDataList("Responses");
        int i = 0;
        for (TestData data : tdGetRequests) {
            ResponseWrapper responseWrapper = bpmRestClient.postTasks(RestServiceHelper.prepareTestDataForRestTest(data.adjust("referenceId", referenceId)));
            TasksResponseWrapper expectedResponse = new TasksResponseWrapper(tdGetResponses.get(i));
            TasksResponseWrapper actualResponse = responseWrapper.getResponse().readEntity(TasksResponseWrapper.class);
            RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, tdGetResponses.get(i));
            i++;
        }
    }

    public static void assertTasksAssigneeSuccessfulPutResponse(TestData testData, RESTServiceUser user, TasksResponse[] taskIds) {
        bpmRestClient.getRestClient().adjustUser(user.getLogin(), user.getPassword());

        List<TestData> tdGetRequests = testData.getTestDataList("Requests");
        List<TestData> tdGetResponses = testData.getTestDataList("Responses");
        int i = 0;
        for (TestData data : tdGetRequests) {
            String taskId = taskIds[Integer.parseInt(data.getValue("taskId")) - 1].getId();
            ResponseWrapper responseWrapper = bpmRestClient.putTaskAssignmentById(Entity.json(new Assignment(data)),
                    DataProviderFactory.emptyData().adjust("taskId", taskId));
            CustomAssert.assertEquals(String.format("Unsuccessful assignee for [%1$s]", tdGetResponses.get(i).getValue("logMessage")), "Success",
                    responseWrapper.getResponse().readEntity(TaskAssignmentResponse.class).getStatus());
            responseWrapper = bpmRestClient.getTaskAssignmentById(DataProviderFactory.emptyData().adjust("taskId", taskId));
            Assignment expectedResponse = new Assignment(tdGetResponses.get(i));
            Assignment actualResponse = responseWrapper.getResponse().readEntity(Assignment.class);
            RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, tdGetResponses.get(i));
            i++;
        }
    }

    public static void assertTasksAssigneeErrorPutResponse(TestData testData, RESTServiceUser user, TasksResponse[] taskIds) {
        bpmRestClient.getRestClient().adjustUser(user.getLogin(), user.getPassword());

        List<TestData> tdGetRequests = testData.getTestDataList("Requests");
        List<TestData> tdGetResponses = testData.getTestDataList("Responses");
        int i = 0;
        for (TestData data : tdGetRequests) {
            String taskId = taskIds[Integer.parseInt(data.getValue("taskId")) - 1].getId();
            ResponseWrapper responseWrapper = bpmRestClient.putTaskAssignmentById(Entity.json(new Assignment(data)),
                    DataProviderFactory.emptyData().adjust("taskId", taskId));
            TestData responseData = populateMsgWithRealTaskId(tdGetResponses.get(i), taskId);
            TasksResponseWrapper expectedResponse = new TasksResponseWrapper(responseData);
            TasksResponseWrapper actualResponse = responseWrapper.getResponse().readEntity(TasksResponseWrapper.class);
            RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, responseData);
            i++;
        }
    }

    public static BPMRestClient getBpmRestClient() {
        return bpmRestClient;
    }

    private static TestData populateMsgWithRealTaskId(TestData testData, String taskId) {
        String matcher = "TASK_ID_PUT_HERE";
        String message;
        if (testData.containsKey("message") && testData.getValue("message").contains(matcher)) {
            message = testData.getValue("message").replace(matcher, taskId);
            testData.adjust("message", message);
        }
        if (testData.getTestDataList("errors").size() > 0) {
            List<TestData> updatedData = new ArrayList<>();
            for (TestData data : testData.getTestDataList("errors")) {
                if (data.containsKey("message") && data.getValue("message").contains(matcher)) {
                    updatedData.add(data.adjust("message", data.getValue("message").replace(matcher, taskId)).resolveLinks());
                } else {
                    updatedData.add(data);
                }
            }
            testData.adjust("errors", updatedData);
        }
        return testData.resolveLinks();
    }
}
