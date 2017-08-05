package aaa.rest.platform.bpm;
	
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;
import toolkit.rest.RestServiceUtil.RestMethod;

public class BPMDevRestClient{

	private ThreadLocal<RestServiceUtil> restClient = new ThreadLocal<RestServiceUtil>() {
		@Override protected RestServiceUtil initialValue() {
			return new RestServiceUtil("bpmdev-rs");
		}
	};
	
/*	*//**
	 * <b>Target:</b> /process/{entityType} 
	 *//*
	public ResponseWrapper getProcessItem(TestData data) {
		return restClient.processRequest("PROCESS.ITEM", RestMethod.GET, data);
	}*/

	/**
	 * <b>Target:</b> /process/{entityType}/{entityRefNo} 
	 */
	public ResponseWrapper getProcessItem(TestData data) {
		return restClient.get().processRequest("PROCESS.ITEM", RestMethod.GET, data);
	}

	/**
	 * <b>Target:</b> /process/{processKey}/{entityRefNo}/{entityType} 
	 */
	public ResponseWrapper postProcessItem(TestData data) {
		return restClient.get().processRequest("PROCESS.ITEM", RestMethod.POST, data);
	}

	/**
	 * <b>Target:</b> /tasks/assignToQueue/{taskId}/{queueCode} 
	 */
	public ResponseWrapper postTasksAssignToQueueItem(TestData data) {
		return restClient.get().processRequest("TASKS.ASSIGNTOQUEUE.ITEM", RestMethod.POST, data);
	}

	/**
	 * <b>Target:</b> /tasks/attachDocument/{taskId}/{documentUuid} 
	 */
	public ResponseWrapper postTasksAttachDocumentItem(TestData data) {
		return restClient.get().processRequest("TASKS.ATTACHDOCUMENT.ITEM", RestMethod.POST, data);
	}

	/**
	 * <b>Target:</b> /tasks/{processId} 
	 */
	public ResponseWrapper getTasksItem(TestData data) {
		return restClient.get().processRequest("TASKS.ITEM", RestMethod.GET, data);
	}

/*	*//**
	 * <b>Target:</b> /tasks/{taskId} 
	 *//*
	public ResponseWrapper postTasksItem(TestData data) {
		return restClient.processRequest("TASKS.ITEM", RestMethod.POST, data);
	}*/

	/**
	 * <b>Target:</b> /tasks/{taskId}/suspense 
	 */
	public ResponseWrapper deleteTasksSuspense(TestData data) {
		return restClient.get().processRequest("TASKS.SUSPENSE", RestMethod.DELETE, data);
	}

	/**
	 * <b>Target:</b> /tasks/{taskId}/suspense 
	 */
	public ResponseWrapper putTasksSuspense(TestData data) {
		return restClient.get().processRequest("TASKS.SUSPENSE", RestMethod.PUT, data);
	}

	/**
	 * <b>Target:</b> /tasks/{taskId}/{loginName} 
	 */
	public ResponseWrapper postTasksItem(TestData data) {
		return restClient.get().processRequest("TASKS.ITEM", RestMethod.POST, data);
	}

	/**
	 * <b>Target:</b> SwaggerJson 
	 */
	public ResponseWrapper getSwaggerJson(TestData data) {
		return restClient.get().processRequest("SWAGGERJSON", RestMethod.GET, data);
	}

}
