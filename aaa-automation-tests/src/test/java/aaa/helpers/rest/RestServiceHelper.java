package aaa.helpers.rest;

import java.lang.reflect.Field;

import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aaa.rest.IModel;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;
import toolkit.verification.CustomAssert;

public class RestServiceHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestServiceHelper.class);

	public static final String REQUESTS_TD_KEY = "Requests";
	public static final String RESPONSES_TD_KEY = "Responses";

	public static void assertModels(IModel expectedModel, IModel actualModel, ResponseWrapper responseWrapper, TestData testData) {

		if (!actualModel.equals(expectedModel)) {
			CustomAssert.assertTrue(String.format("Scenario [%1$s] failed", testData.getValue("logMessage")), false);
			LOGGER.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			LOGGER.info("TestScenario [{}] FAILED. Please see log below.", testData.getValue("logMessage"));
			LOGGER.info("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
			logSentRequest(responseWrapper);
			LOGGER.info("Exp.: {}", expectedModel);
			LOGGER.info("Act.: {}", actualModel);
			LOGGER.info("Act. Response from RestUtil: {}", responseWrapper.getResponse().readEntity(String.class));
			LOGGER.info("====================================================================================");
		} else {
			LOGGER.info("TestScenario [{}] PASSED.", testData.getValue("logMessage"));
		}
	}

	public static TestData prepareTestDataForRestTest(TestData testData) {
		return DataProviderFactory.emptyData().adjust("testDataNode", testData).resolveLinks();
	}

	/**
	 * Not sure if it needed, has no way to get sent request for logging
	 * 
	 * @param responseWrapper
	 */
	private static void logSentRequest(ResponseWrapper responseWrapper) {
		Response response = responseWrapper.getResponse();
		Field field;
		try {
			field = response.getClass().getDeclaredField("context");
			field.setAccessible(true);
			ClientResponse clientResponse = (ClientResponse) field.get(response);
			String request = String.format("URI:[%1$s]", clientResponse.getRequestContext().getUri().toString());
			if (clientResponse.getRequestContext().getMethod().equals(RestServiceUtil.RestMethod.POST.name()) || clientResponse.getRequestContext().getMethod().equals(RestServiceUtil.RestMethod.PUT.name())) {
				request += "\n" + clientResponse.getRequestContext().getEntity();
			}
			LOGGER.info("Sent request: \n{}", request);
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
			LOGGER.info("Unable to fetch request body from response.");
		}
	}

}
