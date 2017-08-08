package aaa.helpers.rest;

import static aaa.helpers.rest.RestServiceHelper.REQUESTS_TD_KEY;
import static aaa.helpers.rest.RestServiceHelper.RESPONSES_TD_KEY;

import java.util.List;

import aaa.common.Tab;
import aaa.admin.constants.AdminConstants;
import aaa.main.pages.summary.NotesAndAlertsSummaryPage;
import aaa.rest.IModel;
import aaa.rest.RESTServiceType;
import aaa.rest.RESTServiceUser;
import aaa.rest.platform.notes.NotesRestClient;
import aaa.rest.platform.notes.RESTNoteType;
import aaa.rest.platform.notes.model.NotesResponse;
import aaa.rest.platform.notes.model.NotesResponseWrapper;
import toolkit.datax.DataProviderFactory;
import toolkit.datax.TestData;
import toolkit.exceptions.IstfException;
import toolkit.rest.ResponseWrapper;
import toolkit.rest.RestServiceUtil;

public class NotesRestHelper {

	private static NotesRestClient notesRestClient = RESTServiceType.NOTES.get();

	private static final String NOTE_ID_KEY = "noteId";
	private static final String ENTITY_REF_KEY = "entityRefNo";

	public static void assertNotesResponse(TestData testData, RESTServiceUser user, RestServiceUtil.RestMethod method, String entityRef) {
		notesRestClient.getRestClient().adjustUser(user.getLogin(), user.getPassword());

		List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
		List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
		int i = 0;
		for (TestData data : requests) {
			if (entityRef != null && data.getValue(ENTITY_REF_KEY) != null && data.getValue(ENTITY_REF_KEY).isEmpty()) {
				data = data.adjust(ENTITY_REF_KEY, entityRef).resolveLinks();
			}
			switch (method) {
			case GET: {
				ResponseWrapper responseWrapper = notesRestClient.getNotesByTypeAndRefItem(data);
				NotesResponseWrapper actualResponse = responseWrapper.getResponse().readEntity(NotesResponseWrapper.class);
				NotesResponseWrapper expectedResponse;
				if (responses.get(i).getTestDataList(RESPONSES_TD_KEY.toLowerCase()).isEmpty()) {
					expectedResponse = new NotesResponseWrapper(responses.get(i));
				} else {
					expectedResponse = new NotesResponseWrapper(responses.get(i).getTestDataList(RESPONSES_TD_KEY.toLowerCase()));
				}
				RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, responses.get(i));
				break;
			}
			case POST: {
				ResponseWrapper responseWrapper = notesRestClient.postNotes(prepareTestDataForRestTest(data));
				NotesResponse actualResponse = responseWrapper.getResponse().readEntity(NotesResponse.class);
				NotesResponse expectedResponse = new NotesResponse(responses.get(i));

				RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, responses.get(i));
				assertNotePresence(data, responses.get(i));
				break;
			}
			case PUT: {
				String noteId = data.getValue(ENTITY_REF_KEY);
				ResponseWrapper responseWrapper = notesRestClient.putNotesItem(prepareTestDataForRestTest(data.mask(ENTITY_REF_KEY).mask(NOTE_ID_KEY).resolveLinks()).adjust(NOTE_ID_KEY, noteId).resolveLinks());
				NotesResponse actualResponse = responseWrapper.getResponse().readEntity(NotesResponse.class);
				NotesResponse expectedResponse = new NotesResponse(responses.get(i));
				RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, responses.get(i));
				break;
			}
			default:
				break;
			}
			i++;
		}
	}

	public static void assertNotesGetResponseByNoteId(TestData testData, RESTServiceUser user, String noteId) {
		notesRestClient.getRestClient().adjustUser(user.getLogin(), user.getPassword());

		List<TestData> requests = testData.getTestDataList(REQUESTS_TD_KEY);
		List<TestData> responses = testData.getTestDataList(RESPONSES_TD_KEY);
		int i = 0;
		for (TestData data : requests) {
			if (noteId != null && data.getValue(NOTE_ID_KEY) != null && data.getValue(NOTE_ID_KEY).isEmpty()) {
				data = data.adjust(NOTE_ID_KEY, noteId).resolveLinks();
			}
			ResponseWrapper responseWrapper = notesRestClient.getNotesItem(data);
			NotesResponse actualResponse = responseWrapper.getResponse().readEntity(NotesResponse.class);
			NotesResponse expectedResponse = new NotesResponse(responses.get(i));

			RestServiceHelper.assertModels(expectedResponse, actualResponse, responseWrapper, responses.get(i));
			i++;
		}
	}

	public static String getNoteIdByUser(RESTServiceUser user, RESTNoteType noteType, String entityRefNo) {
		notesRestClient.getRestClient().adjustUser(RESTServiceUser.ALL_RIGHTS.getLogin(), RESTServiceUser.ALL_RIGHTS.getPassword());
		ResponseWrapper responseWrapper = notesRestClient.getNotesByTypeAndRefItem(DataProviderFactory.emptyData().adjust("entityType", noteType.get()).adjust(ENTITY_REF_KEY, entityRefNo).resolveLinks());
		for (IModel response : responseWrapper.getResponse().readEntity(NotesResponseWrapper.class).getModels()) {
			if (((NotesResponse) response).getPerformerId().equals(user.getLogin())) {
				return ((NotesResponse) response).getId(); // currently works
															// only with on note
															// per user. enhance
															// to support
															// multiple notes
			}
		}
		throw new IstfException(String.format("There are no Notes created by User[%1$s] for Quote[%2$s]", user.getLogin(), entityRefNo));
	}

	public static NotesRestClient getNotesRestClient() {
		return notesRestClient;
	}

	private static void assertNotePresence(TestData requestTestData, TestData expectedTestData) {
		if (null == expectedTestData.getValue("presentOnUi")) {
			return;
		}
		NotesAndAlertsSummaryPage.open();
		if ("true".equals(expectedTestData.getValue("presentOnUi"))) {
			NotesAndAlertsSummaryPage.tableFilterResults.getRowContains(AdminConstants.AdminFilterResultsTable.TITLE, requestTestData.getValue("title")).verify.present();
		} else {
			NotesAndAlertsSummaryPage.tableFilterResults.getRowContains(AdminConstants.AdminFilterResultsTable.TITLE, requestTestData.getValue("title")).verify.present(false);
		}
		Tab.buttonBack.click();
	}

	private static TestData prepareTestDataForRestTest(TestData testData) {
		return DataProviderFactory.emptyData().adjust("testDataNode", testData).resolveLinks();
	}
}
